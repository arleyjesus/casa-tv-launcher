param([string]$JavaHome=$env:JAVA_HOME)
$ErrorActionPreference='Stop'
$repoRoot=Split-Path -Parent $PSScriptRoot
$testDir=Join-Path $repoRoot ('build\home-tests-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force -Path $testDir | Out-Null
& "$JavaHome\bin\javac.exe" --release 8 -d $testDir (Join-Path $repoRoot 'src\tv\casa\launcher\HomeRedirectPolicy.java') (Join-Path $repoRoot 'tests\HomeRedirectPolicyTest.java')
if($LASTEXITCODE){throw 'Home test compilation failed.'}
& "$JavaHome\bin\java.exe" -cp $testDir tv.casa.launcher.HomeRedirectPolicyTest
if($LASTEXITCODE){throw 'Home redirect regression.'}
