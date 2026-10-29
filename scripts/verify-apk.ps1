[CmdletBinding()]
param(
    [Parameter(Mandatory=$true)][string]$ApkPath,
    [string]$AndroidSdk = $(if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { $env:ANDROID_SDK_ROOT }),
    [string]$BuildToolsVersion = '36.0.0'
)
$ErrorActionPreference = 'Stop'
$aapt = Join-Path $AndroidSdk "build-tools\$BuildToolsVersion\aapt2.exe"
$badging = (& $aapt dump badging $ApkPath) -join "`n"
if ($LASTEXITCODE) { throw 'Could not inspect APK.' }
if ($badging -notmatch "package: name='tv.casa.launcher'") { throw 'Unexpected application ID.' }
if ($badging -notmatch "sdkVersion:'26'") { throw 'Unexpected minimum Android API.' }
if ($badging -notmatch "targetSdkVersion:'30'") { throw 'Unexpected target SDK change.' }
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip=[IO.Compression.ZipFile]::OpenRead((Resolve-Path -LiteralPath $ApkPath).Path)
try {
    if(@($zip.Entries | Where-Object { $_.FullName.Contains('\') }).Count){throw 'APK contains Windows ZIP paths.'}
    if($badging -match "versionCode='11'") {
        foreach($preset in @('classic','modern','immersive')) {
            if($null -eq $zip.GetEntry($(if($preset -eq "classic"){"assets/landscape.png"}else{"assets/ambient-fjord.png"}))){throw "Missing home wallpaper: $preset"}
        }
    }
} finally {$zip.Dispose()}
if ($badging -match 'uses-permission[^\r\n]*android.permission.(WRITE_SECURE_SETTINGS|MANAGE_EXTERNAL_STORAGE)') { throw 'Privileged permission contract changed. Review required.' }
if ($badging -notmatch 'uses-permission[^\r\n]*android.permission.INTERNET') { throw 'Explicit network permission missing.' }
$permissions = [regex]::Matches($badging, "uses-permission: name='([^']+)'")
foreach ($permission in $permissions) { if ($permission.Groups[1].Value -notin @('android.permission.INTERNET','android.permission.ACCESS_NETWORK_STATE')) { throw 'Unexpected runtime permission.' } }
$accessibility = (& $aapt dump xmltree $ApkPath --file res/xml/accessibility.xml) -join "`n"
if ($LASTEXITCODE) { throw 'Accessibility configuration missing.' }
if ($accessibility -notmatch 'canRetrieveWindowContent[^\r\n]*=(false|0x0+)\b') { throw 'Accessibility must not retrieve window content.' }
if ($accessibility -notmatch 'com.google.android.apps.tv.launcherx,com.spocky.projengmenu') { throw 'Accessibility package scope changed. Review required.' }
Write-Output 'APK checks passed: identity, minimum SDK, on-demand network permission contract, limited accessibility scope.'
