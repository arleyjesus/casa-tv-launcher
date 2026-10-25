param([string]$JavaHome=$env:JAVA_HOME)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$dest=Join-Path $root ('build\picture-tests-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force $dest | Out-Null
& "$JavaHome\bin\javac.exe" --release 8 -d $dest (Join-Path $root 'src\tv\casa\launcher\PictureTogglePolicy.java') (Join-Path $root 'tests\PictureTogglePolicyTest.java')
if($LASTEXITCODE){throw 'Picture test compilation failed.'}
& "$JavaHome\bin\java.exe" -cp $dest tv.casa.launcher.PictureTogglePolicyTest
if($LASTEXITCODE){throw 'Picture policy tests failed.'}
