param([string]$JavaHome=$env:JAVA_HOME)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$dest=Join-Path $root ('build\online-tests-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force $dest | Out-Null
& "$JavaHome\bin\javac.exe" --release 8 -encoding UTF-8 -d $dest (Join-Path $root 'src\tv\casa\launcher\OnlinePolicy.java') (Join-Path $root 'src\tv\casa\launcher\RangePolicy.java') (Join-Path $root 'tests\OnlinePolicyTest.java')
if($LASTEXITCODE){throw 'Online test compilation failed.'}
& "$JavaHome\bin\java.exe" -cp $dest tv.casa.launcher.OnlinePolicyTest
if($LASTEXITCODE){throw 'Online policy failed.'}
foreach($relative in @('resources/ambient/catalog-v2.json','assets/ambient-catalog-v2.json')){
    $catalog=[IO.File]::ReadAllText((Join-Path $root $relative)) | ConvertFrom-Json
    if($catalog.schema -ne 2 -or $catalog.collections.Count -lt 1 -or $catalog.collections.Count -gt 12){throw 'Invalid catalog fixture.'}
}

& "$JavaHome\bin\javac.exe" --release 8 -encoding UTF-8 -cp $dest -d $dest (Join-Path $root 'tests/stubs/android/media/MediaDataSource.java') (Join-Path $root 'src/tv/casa/launcher/AmbientHttp.java') (Join-Path $root 'src/tv/casa/launcher/RangeMediaSource.java') (Join-Path $root 'tests/RangeMediaSourceTest.java')
if($LASTEXITCODE){throw 'Range source test compilation failed.'}
& "$JavaHome\bin\java.exe" -cp $dest tv.casa.launcher.RangeMediaSourceTest
if($LASTEXITCODE){throw 'Range source test failed.'}
