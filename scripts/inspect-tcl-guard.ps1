[CmdletBinding()]
param(
    [Parameter(Mandatory=$true)][string]$AdbPath,
    [Parameter(Mandatory=$true)][string]$TvEndpoint
)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$folder=Join-Path $root ('build\guard-inspection\'+(Get-Date -Format 'yyyyMMdd-HHmmss'))
New-Item -ItemType Directory -Path $folder -Force | Out-Null
function Read-Tv([string[]]$Arguments) {
    $result=@(& $AdbPath -s $TvEndpoint @Arguments 2>&1)
    if($LASTEXITCODE){throw 'ADB query failed. Check connection and authorization.'}
    return ($result -join "`n")
}
$live=Read-Tv -Arguments @('shell','dumpsys','activity','service','com.tcl.guard/.memorymanager.TGuardService')
$raw=Read-Tv -Arguments @('shell','content','query','--uri','content://com.tcl.config.ConfigProvider/TGuardMemoryManager','--projection','project_id:config_content')
$live | Set-Content -LiteralPath (Join-Path $folder 'guard-live.txt') -Encoding UTF8
$raw | Set-Content -LiteralPath (Join-Path $folder 'guard-provider.txt') -Encoding UTF8
$rows=@($raw -split "`n" | Where-Object {$_ -match '^Row:\s*\d+.*config_content='})
if($rows.Count -ne 1){throw 'Expected one configuration row. No policy was modified.'}
$json=($rows[0] -split 'config_content=',2)[1].Trim()
if(-not $json.StartsWith('[') -or -not $json.EndsWith(']')){throw 'Unsupported configuration format.'}
$data=ConvertFrom-Json -InputObject $json
$entries=@($data | Where-Object {$_.name -eq 'KillWhiteList'})
if($entries.Count -ne 1 -or $entries[0].value -isnot [array]){throw 'Whitelist missing or ambiguous.'}
if(@($entries[0].value | Where-Object {$_ -isnot [string] -or $_ -notmatch '^[A-Za-z0-9_]+(?:\.[A-Za-z0-9_]+)+$'}).Count){throw 'Invalid package names in whitelist.'}
$packages=@($live -split "`n" | ForEach-Object {if($_ -match '^\s*KillWhiteList:\s*([A-Za-z0-9_]+(?:\.[A-Za-z0-9_]+)+)\s*$'){$Matches[1]}} | Sort-Object -Unique)
if(-not $packages.Count){throw 'Live whitelist unavailable. Protection not confirmed.'}
[PSCustomObject]@{
    CasaPersistent=(@($entries[0].value) -contains 'tv.casa.launcher')
    CasaLive=($packages -contains 'tv.casa.launcher')
    PersistentEntries=@($entries[0].value).Count
    LiveEntries=$packages.Count
    PrivateBackupFolder=$folder
    ModifiedSettings=$false
}
