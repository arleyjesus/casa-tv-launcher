param([string]$JavaHome=$env:JAVA_HOME)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$dest=Join-Path $root ('build\game-tests-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force $dest | Out-Null
& "$JavaHome\bin\javac.exe" --release 8 -d $dest (Join-Path $root 'src\tv\casa\launcher\GamePolicy.java') (Join-Path $root 'tests\GamePolicyTest.java')
if($LASTEXITCODE){throw 'Game policy compilation failed.'}
& "$JavaHome\bin\java.exe" -cp $dest tv.casa.launcher.GamePolicyTest
if($LASTEXITCODE){throw 'Invalid game navigation policy.'}
