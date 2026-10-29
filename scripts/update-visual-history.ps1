$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
$data=[IO.File]::ReadAllText((Join-Path $root 'docs/history/releases.json')) | ConvertFrom-Json
$lines=[System.Collections.Generic.List[string]]::new()
$lines.Add('# Evolución visual')
$lines.Add('')
$lines.Add('Cada versión conserva sus capturas y su enlace de descarga. El README muestra la interfaz actual; este archivo permite recorrer las anteriores. Las imágenes apuntan a la etiqueta de cada versión y no se sustituyen al actualizar el proyecto.')
$lines.Add('')
$lines.Add('[Cambios de código](../CHANGELOG.md) · [Resultados en C645](RESULTADOS-C645.es.md)')
foreach($release in ($data.releases | Sort-Object { [version]$_.version } -Descending)){
    if($release.version -notmatch '^\d+\.\d+\.\d+$'){throw 'Version no valida'}
    $lines.Add('');$lines.Add("## $($release.version) · $($release.title)");$lines.Add('')
    $lines.Add("[Descargar y leer notas](https://github.com/arleyjesus/velira-tv/releases/tag/v$($release.version))");$lines.Add('')
    if($release.images.Count -eq 0){$lines.Add('Esta versión no tiene capturas propias archivadas.');continue}
    foreach($image in $release.images){
        if($image.file -notmatch '^docs/images/[a-zA-Z0-9-]+\.png$'){throw 'Ruta de captura no valida'}
        $lines.Add("![$($image.caption)](https://raw.githubusercontent.com/arleyjesus/velira-tv/v$($release.version)/$($image.file))");$lines.Add('')
    }
}
while($lines.Count -gt 0 -and $lines[$lines.Count-1] -eq ''){$lines.RemoveAt($lines.Count-1)}
[IO.File]::WriteAllLines((Join-Path $root 'docs/HISTORIAL-VISUAL.es.md'),$lines,[Text.UTF8Encoding]::new($false))
