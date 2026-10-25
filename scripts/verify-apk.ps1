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
if ($badging -match 'uses-permission[^\r\n]*android.permission.(WRITE_SECURE_SETTINGS|MANAGE_EXTERNAL_STORAGE)') { throw 'Privileged permission contract changed. Review required.' }
if ($badging -notmatch 'uses-permission[^\r\n]*android.permission.INTERNET') { throw 'Gallery network permission missing.' }
$permissions = [regex]::Matches($badging, "uses-permission: name='([^']+)'")
foreach ($permission in $permissions) { if ($permission.Groups[1].Value -ne 'android.permission.INTERNET') { throw 'Unexpected runtime permission.' } }
$accessibility = (& $aapt dump xmltree $ApkPath --file res/xml/accessibility.xml) -join "`n"
if ($LASTEXITCODE) { throw 'Accessibility configuration missing.' }
if ($accessibility -notmatch 'canRetrieveWindowContent[^\r\n]*=(false|0x0+)\b') { throw 'Accessibility must not retrieve window content.' }
if ($accessibility -notmatch 'com.google.android.apps.tv.launcherx,com.spocky.projengmenu') { throw 'Accessibility package scope changed. Review required.' }
Write-Output 'APK checks passed: identity, minimum SDK, explicit-download network permission contract, limited accessibility scope.'
