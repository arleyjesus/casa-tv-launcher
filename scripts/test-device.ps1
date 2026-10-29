[CmdletBinding()]
param(
    [Parameter(Mandatory=$true)][string]$AdbPath,
    [Parameter(Mandatory=$true)][string]$TvEndpoint,
    [Parameter(Mandatory=$true)][string]$TargetApkPath,
    [string]$AndroidSdk=$env:ANDROID_HOME,
    [string]$JavaHome=$env:JAVA_HOME,
    [ValidateSet("full","deadline")][string]$Mode="full"
)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$stage=Join-Path $root ('build\device-tests-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force (Join-Path $stage 'scripts'),(Join-Path $stage 'res'),(Join-Path $stage 'assets') | Out-Null
Copy-Item -LiteralPath (Join-Path $root 'tests\device\AndroidManifest.xml') -Destination $stage
Copy-Item -LiteralPath (Join-Path $root 'tests\device\src') -Destination $stage -Recurse
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'build.ps1') -Destination (Join-Path $stage 'scripts\build.ps1')
& (Join-Path $stage 'scripts\build.ps1') -AndroidSdk $AndroidSdk -JavaHome $JavaHome -Sign
& $AdbPath -s $TvEndpoint install -r $TargetApkPath
if($LASTEXITCODE){throw 'Target update failed. No data was removed.'}
& $AdbPath -s $TvEndpoint install -r (Join-Path $stage 'build\Casa-TV-Launcher-v1.apk')
if($LASTEXITCODE){throw 'Test APK install failed. Use the same authorized signing certificate.'}
$logProcess=$null
try {
    $logProcess=Start-Process -FilePath $AdbPath -ArgumentList @('-s',$TvEndpoint,'logcat','-T','1','-s','CasaAmbient:I','VeliraChecks:I') -WindowStyle Hidden -RedirectStandardOutput (Join-Path $stage 'media.log') -RedirectStandardError (Join-Path $stage 'logcat-error.txt') -PassThru
    $result=(& $AdbPath -s $TvEndpoint shell am instrument -w -e mode $Mode tv.casa.launcher.check/.Checks) -join "`n"
    [IO.File]::WriteAllText((Join-Path $stage 'results.txt'),$result)
    Write-Output $result
    $expected=if($Mode -eq 'deadline'){1}else{6}
    if($result.Contains('FAIL:') -or ([regex]::Matches($result,'PASS:')).Count -lt $expected){throw 'Device regression did not pass all checks.'}
} finally {
    if($null -ne $logProcess){Stop-Process -Id $logProcess.Id -ErrorAction SilentlyContinue}
    & $AdbPath -s $TvEndpoint uninstall tv.casa.launcher.check
    & $AdbPath -s $TvEndpoint shell am start -n tv.casa.launcher/.MainActivity
    Write-Output "Private device report: $stage"
}
