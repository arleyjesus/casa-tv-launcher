param([string]$JavaHome=$env:JAVA_HOME)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$dest=Join-Path $root ('build\display-tests-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force $dest | Out-Null
& "$JavaHome\bin\javac.exe" --release 8 -d $dest (Join-Path $root 'src\tv\casa\launcher\DisplayOptions.java') (Join-Path $root 'tests\DisplayOptionsTest.java')
if($LASTEXITCODE){throw 'Display test compilation failed.'}
& "$JavaHome\bin\java.exe" -cp $dest tv.casa.launcher.DisplayOptionsTest
if($LASTEXITCODE){throw 'Invalid display option contract.'}
