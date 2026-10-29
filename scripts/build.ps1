[CmdletBinding()]
param(
    [string]$AndroidSdk = $(if ($env:ANDROID_HOME) { $env:ANDROID_HOME } elseif ($env:ANDROID_SDK_ROOT) { $env:ANDROID_SDK_ROOT } else { '' }),
    [string]$JavaHome = $env:JAVA_HOME,
    [string]$AndroidPlatform = 'android-37.0',
    [string]$BuildToolsVersion = '36.0.0',
    [switch]$Sign
)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
if (-not $AndroidSdk -or -not (Test-Path -LiteralPath $AndroidSdk)) { throw 'Set ANDROID_HOME or pass -AndroidSdk pointing to an installed Android SDK.' }
if (-not $JavaHome -or -not (Test-Path -LiteralPath (Join-Path $JavaHome 'bin\javac.exe'))) { throw 'Set JAVA_HOME or pass -JavaHome pointing to JDK 21.' }
$env:JAVA_HOME = $JavaHome
$env:PATH = "$(Join-Path $JavaHome 'bin');$env:PATH"
$tools = Join-Path $AndroidSdk "build-tools\$BuildToolsVersion"
if (-not (Test-Path -LiteralPath (Join-Path $tools 'aapt2.exe'))) { throw "Install Android build-tools $BuildToolsVersion using the official SDK manager." }
if (-not $AndroidPlatform) {
    $platform = Get-ChildItem -LiteralPath (Join-Path $AndroidSdk 'platforms') -Directory | Where-Object { $_.Name -match '^android-\d+(\.\d+)?$' } | Sort-Object { [double]($_.Name -replace '^android-', '') } -Descending | Select-Object -First 1
    if (-not $platform) { throw 'Install an Android SDK platform (API 30 or newer).' }
    $AndroidPlatform = $platform.Name
}
$androidJar = Join-Path $AndroidSdk "platforms\$AndroidPlatform\android.jar"
if (-not (Test-Path -LiteralPath $androidJar)) { throw "Missing platform: $AndroidPlatform" }
[xml]$manifest = Get-Content -LiteralPath (Join-Path $repoRoot 'AndroidManifest.xml') -Raw
$version = $manifest.manifest.GetAttribute('versionName', 'http://schemas.android.com/apk/res/android')
$buildRoot = Join-Path $repoRoot 'build'
$objectDir = Join-Path $buildRoot ('obj-' + [Guid]::NewGuid().ToString('N'))
$classesDir = Join-Path $objectDir 'classes'
$dexDir = Join-Path $objectDir 'dex'
New-Item -ItemType Directory -Force -Path $classesDir,$dexDir | Out-Null
$resources = Join-Path $objectDir 'resources.zip'
$baseApk = Join-Path $objectDir 'base.apk'
$unsignedApk = Join-Path $buildRoot "Casa-TV-Launcher-v$version-unsigned.apk"
& "$tools\aapt2.exe" compile --dir (Join-Path $repoRoot 'res') -o $resources
if ($LASTEXITCODE) { throw 'Resource compilation failed.' }
& "$tools\aapt2.exe" link -o $baseApk --manifest (Join-Path $repoRoot 'AndroidManifest.xml') -I $androidJar -A (Join-Path $repoRoot 'assets') $resources
if ($LASTEXITCODE) { throw 'Resource linking failed.' }
# Some Windows aapt2 builds retain backslashes in nested asset ZIP entries.
# Android AssetManager requires forward slashes on the device.
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$assetZip=[IO.Compression.ZipFile]::Open($baseApk,[IO.Compression.ZipArchiveMode]::Update)
try {
    foreach($entry in @($assetZip.Entries | Where-Object { $_.FullName.Contains('\') })) {
        $replacement=$assetZip.CreateEntry($entry.FullName.Replace('\','/'))
        $inputStream=$entry.Open();$outputStream=$replacement.Open()
        try {$inputStream.CopyTo($outputStream)} finally {$inputStream.Dispose();$outputStream.Dispose()}
        $entry.Delete()
    }
} finally {$assetZip.Dispose()}
$sources = @(Get-ChildItem -LiteralPath (Join-Path $repoRoot 'src') -Recurse -Filter '*.java' | ForEach-Object FullName)
& "$JavaHome\bin\javac.exe" -encoding UTF-8 --release 8 -classpath $androidJar -d $classesDir @sources
if ($LASTEXITCODE) { throw 'Java compilation failed.' }
$classesJar = Join-Path $objectDir 'classes.jar'
& "$JavaHome\bin\jar.exe" cf $classesJar -C $classesDir .
if ($LASTEXITCODE) { throw 'Class packaging failed.' }
& "$tools\d8.bat" --lib $androidJar --min-api 26 --output $dexDir $classesJar
if ($LASTEXITCODE) { throw 'DEX compilation failed.' }
Push-Location $dexDir
try { & "$JavaHome\bin\jar.exe" uf $baseApk classes.dex } finally { Pop-Location }
if ($LASTEXITCODE) { throw 'DEX packaging failed.' }
& "$tools\zipalign.exe" -f 4 $baseApk $unsignedApk
if ($LASTEXITCODE) { throw 'APK alignment failed.' }
if ($Sign) {
    foreach ($name in @('CASA_KEYSTORE','CASA_KEY_ALIAS','CASA_STORE_PASSWORD','CASA_KEY_PASSWORD')) {
        if (-not [Environment]::GetEnvironmentVariable($name)) { throw "Missing signing environment variable: $name" }
    }
    $signedApk = Join-Path $buildRoot "Casa-TV-Launcher-v$version.apk"
    & "$tools\apksigner.bat" sign --ks $env:CASA_KEYSTORE --ks-key-alias $env:CASA_KEY_ALIAS --ks-pass env:CASA_STORE_PASSWORD --key-pass env:CASA_KEY_PASSWORD --out $signedApk $unsignedApk
    if ($LASTEXITCODE) { throw 'Signing failed.' }
    & "$tools\apksigner.bat" verify $signedApk
    if ($LASTEXITCODE) { throw 'Signature verification failed.' }
    Write-Output "Signed APK: $signedApk"
} else {
    Write-Output "Unsigned APK: $unsignedApk"
    Write-Output 'This build cannot be installed until it is signed. Official downloads are in GitHub Releases.'
}
