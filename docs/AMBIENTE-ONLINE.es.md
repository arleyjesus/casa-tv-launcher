# Ambiente online · 0.10

Entra en **Centro de control → Ambiente**. El panel tiene cuatro pestañas: Colecciones, Aspecto, Vídeo y Catálogo. El nombre de la aplicación sigue siendo Casa Aura TV mientras se elige su nueva identidad.

## Colecciones

El catálogo inicial contiene seis imágenes en Paisajes cinematográficos, Montañas, Bosques, Naturaleza, Océanos y Espacio. Tres son los paisajes originales del proyecto; las otras proceden de WordPress Photo Directory, USGS y NASA/Wikimedia Commons. También hay una colección de vídeo de NASA y una muestra Full HD de diez segundos de Big Buck Bunny para comprobar reproducción. Ciudades nocturnas, arquitectura, arte y minimalismo podrán incorporarse cuando tengan contenido seleccionado y acreditado; no se muestran categorías vacías.

1. Elige una colección, Todas o Mis favoritas.
2. Pulsa **Descargar imágenes que faltan y usarlas**. Solo se descargan los archivos ausentes o que no coincidan con su hash. Descargar también selecciona esa fuente para Ambiente.
3. Abre **Ver ambiente**. Las imágenes ya guardadas se reproducen sin conexión.
4. **Elegir imágenes favoritas** permite preparar tu propia selección. El orden aleatorio evita repetir inmediatamente la imagen anterior cuando hay varias.
5. **Usar los tres paisajes integrados** vuelve al contenido incluido en la aplicación. No borra las descargas.

La caché de imágenes tiene un límite de **64 MiB**. Al necesitar espacio se retiran primero archivos antiguos que no pertenecen a la selección actual. Un archivo en curso puede ocupar hasta 8 MiB; las partes incompletas se limpian al cancelar o al iniciar la siguiente descarga. Cambiar o actualizar el catálogo no descarga automáticamente sus fotos. Los favoritos y archivos descargados permanecen en este TV; no se suben a un servidor.

Se aceptan fuentes Full HD y 4K, además de fotografías de hasta 4096 × 4096. Antes de decodificar se comprueban dimensiones, formato y tamaño. La resolución usada en memoria se adapta a la vista y se limita a unos 8,8 megapíxeles por imagen. En la C645 de referencia Android dibuja la interfaz a 1920 × 1080: aceptar una foto de 3840 × 2160 no significa que la interfaz esté renderizando a 4K. Se mantiene la imagen visible y se prepara la siguiente; no se carga toda la colección en RAM.

## Aspecto

Tiempo por imagen, transición y su duración, Ken Burns y zoom se configuran aquí. Hay corte, disolución, desplazamiento corto y zoom suave. Se conservan los valores de las versiones anteriores.

Brillo, contraste y saturación son filtros de las **imágenes de Ambiente**. No escriben ajustes del televisor, no actúan sobre vídeo y no modifican YouTube, Netflix, HDR o AIPQ. Con sus valores neutros no se aplica el filtro de color. La elección de imagen inicial por hora también sigue disponible.

## Vídeo

Puedes elegir el vídeo del catálogo o introducir una URL HTTPS directa de MP4. **Probar el vídeo seleccionado** abre una sesión manual. **Vídeo al entrar en Ambiente** es opcional y está desactivado inicialmente; permite usarlo también en el salvapantallas.

El reproductor siempre está silenciado. Mantiene la imagen hasta recibir el primer fotograma, usa un único decodificador y se libera al salir. Si la carga falla, se pierde la conexión requerida o se alcanza un límite, vuelve a imágenes. No se incluye ni se guarda un archivo de vídeo en la aplicación.

Opciones:

- Calidad máxima de 720p o 1080p. El archivo debe ofrecer una resolución compatible; Casa no convierte un vídeo 4K en un stream de menor calidad. Una URL propia que supere el máximo se detiene.
- Solo Wi-Fi, activado inicialmente. Ethernet queda excluido mientras esa opción esté activa.
- 25, 100 o 250 MiB de datos de vídeo por sesión.
- 5, 15 o 30 minutos de reproducción como máximo.

El límite contabiliza los bytes de vídeo que lee la aplicación, incluidas búsquedas y repeticiones. No equivale al total facturado: hay cabeceras, TLS y buffers de red. Una nueva entrada en Ambiente inicia otra sesión. El límite de datos puede alcanzarse antes que el de tiempo, especialmente con una fuente de bitrate alto.

El servidor debe admitir HTTPS, longitud conocida y peticiones HTTP Range para buscar dentro del MP4. Se permiten como máximo tres redirecciones HTTPS y se conserva la verificación normal de certificados. Las URLs con credenciales incrustadas o fragmentos se rechazan. Los enlaces de YouTube, un archivo HLS o un manifiesto DASH no son URLs MP4 directas.

### Formatos estudiados

| Fuente | Estado |
| --- | --- |
| MP4/M4V por HTTPS | Implementado con el reproductor Android y lectura parcial limitada. Depende del códec que soporte el TV. |
| HLS | Android ofrece soporte nativo con limitaciones. No se incorpora al lector MP4; requiere validar segmentos, variantes y consumo. |
| DASH / streaming adaptativo | Pendiente de integrar y probar un reproductor como Media3, incluidos sus módulos, buffers y ciclo de vida. |
| YouTube | Posible mediante reproductor oficial visible, sin extraer streams ni alterar publicidad. No incorporado al salvapantallas. |

Se usa la superficie nativa recomendada para vídeo en Android TV. En esta TCL las capturas ADB pueden omitir el plano de vídeo aunque se vea en la pantalla física. [Tipos de superficie en Android](https://developer.android.com/media/media3/ui/surface).

La documentación de [formatos Android](https://developer.android.com/media/platform/supported-formats) y [Media3](https://developer.android.com/media/media3/exoplayer/supported-formats) distingue contenedores, códecs y protocolos. Una extensión compatible no garantiza que cualquier TV pueda decodificar el contenido.

YouTube exige respetar su reproductor y los anuncios; una integración propia no ofrece un parámetro para hacerlos desaparecer. Para sesiones ambientales controladas se priorizan vídeos propios o con permiso para reproducirlos mediante URL directa. Las cuentas de YouTube y sus suscripciones se gestionan en su aplicación oficial; Casa no inicia sesión en ellas. Fuentes: [API IFrame](https://developers.google.com/youtube/iframe_api_reference), [políticas de integración](https://developers.google.com/youtube/terms/developer-policies-guide).

## Catálogo remoto

**Actualizar catálogo** consulta el JSON oficial alojado en GitHub. Se usa ETag cuando el servidor lo ofrece. También puedes introducir la URL de un catálogo propio compatible con el esquema 2. Solo se conecta al solicitarlo; no se añaden tareas periódicas ni consultas desde Inicio.

El JSON describe colecciones, archivos, resolución, autor, licencia y fuente. No modifica tus preferencias, no ejecuta acciones ni activa vídeo de forma remota. Los límites son 12 colecciones, 48 recursos, tres variantes por recurso, 256 KiB de catálogo y 8 MiB por imagen. Las imágenes requieren bytes exactos y SHA-256; se admiten JPEG, PNG y WebP válidos.

Se pueden usar repositorios GitHub, Pages, una CDN o un servidor HTTPS propio. Para vídeo se necesita además soporte Range. GitHub resulta práctico para el catálogo y packs pequeños; sus límites hacen preferible una CDN o servidor adecuado para uso intensivo de vídeo. [Límites de GitHub Pages](https://docs.github.com/en/pages/getting-started-with-github-pages/github-pages-limits).

El [catálogo publicado](../resources/ambient/catalog-v2.json) sirve de ejemplo completo. Cambiarlo permite incorporar contenido sin actualizar el APK. La versión inicial incluida dentro de la aplicación permite explorar sus colecciones antes de conectarse. Se conserva acceso a la galería anterior para administrar sus archivos.

## Contenido y licencias

**Fuentes y licencias** muestra los créditos y enlaces de cada recurso. Los proveedores reciben tu IP y los metadatos normales de las peticiones cuando descargas o reproduces contenido. Una URL externa puede cambiar o dejar de estar disponible; un hash diferente requiere actualizar el catálogo.

- [WordPress Photo Directory](https://wordpress.org/photos/photo/40669d7369/): foto de digantparmar bajo CC0.
- [USGS](https://www.usgs.gov/media/images/sunset-over-atlantic-ocean): fotografía de Kaitlin Kovacs identificada como dominio público.
- [NASA / Wikimedia Commons](https://commons.wikimedia.org/wiki/File:Earth_Eastern_Hemisphere.jpg): composición MODIS del hemisferio oriental, con su atribución y condiciones publicadas.
- Big Buck Bunny: © 2008 Blender Foundation · www.bigbuckbunny.org. Fragmento codificado y servido por [Test Videos](https://test-videos.co.uk/bigbuckbunny/mp4-h264), bajo [CC BY 3.0](https://creativecommons.org/licenses/by/3.0/); [licencia del proyecto](https://peach.blender.org/about/).
- [Perpetual Ocean](https://svs.gsfc.nasa.gov/3827/): visualización de NASA/Goddard SVS; [condiciones de uso de imágenes y medios NASA](https://www.nasa.gov/nasa-brand-center/images-and-media/).

Se evaluaron también [Pexels](https://www.pexels.com/api/documentation/) y [Unsplash](https://unsplash.com/documentation). Sus APIs añaden claves, atribución y condiciones específicas de uso, caché o enlaces a sus CDN. No se incluyen claves en el APK ni se presenta cualquier imagen encontrada en Internet como contenido libre. Se mantiene una selección curada con licencia por recurso.

Las copias de apariencia incluyen los filtros y transiciones. Las fuentes personalizadas, favoritas online, archivos, ETag y opciones de streaming permanecen en almacenamiento privado y no se incluyen en esas exportaciones.

En la C645 de referencia se conservó la variante ligera de NASA a 640 × 360. Su archivo Full HD de gran tamaño no fue fluido en la prueba y se retiró del catálogo inicial. La muestra de Big Buck Bunny permite comprobar 1080p con un archivo pequeño; no sustituye una colección de vídeos ambientales Full HD.
