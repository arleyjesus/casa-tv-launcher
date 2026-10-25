param([string]$JavaHome=$env:JAVA_HOME)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$dest=Join-Path $root ('build\gallery-tests-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force $dest | Out-Null
& "$JavaHome\bin\javac.exe" --release 8 -d $dest (Join-Path $root 'src\tv\casa\launcher\GalleryPolicy.java') (Join-Path $root 'tests\GalleryPolicyTest.java')
if($LASTEXITCODE){throw 'Gallery test compilation failed.'}
& "$JavaHome\bin\java.exe" -cp $dest tv.casa.launcher.GalleryPolicyTest
if($LASTEXITCODE){throw 'Gallery policy tests failed.'}
