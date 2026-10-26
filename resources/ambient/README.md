# Catálogo de Ambiente · esquema 2

El archivo `catalog-v2.json` se publica como contenido remoto y se incluye como índice inicial en el APK. Las imágenes y los vídeos nuevos no se empaquetan en la aplicación.

Cada colección tiene `id`, `name` e `items`. Cada recurso contiene `id`, `type` (`image` o `mp4`), `title`, `author`, `license`, `source` y entre una y tres `variants`. Las variantes declaran URL HTTPS, anchura y altura reales; las imágenes incluyen además `bytes` y `sha256`. Los nombres y créditos son texto, sin HTML.

Antes de añadir contenido, comprueba su licencia, tamaño, hash y resolución. Usa IDs estables y URLs duraderas. La app no ejecuta campos de configuración ni acciones procedentes del catálogo. Otros tipos de vídeo se rechazan hasta que tengan un reproductor compatible.

La copia de `assets/ambient-catalog-v2.json` es el índice inicial de cada APK. Se puede actualizar `resources/ambient/catalog-v2.json` de forma independiente, sin publicar otro APK; al preparar una versión de aplicación puedes sincronizar su copia inicial. No cambies el esquema 1: las versiones 0.7–0.9 siguen utilizándolo.

Las condiciones de cada recurso se consultan en `source`; los recursos de terceros no pasan a tener la licencia MIT del código. [Guía de uso y límites](../../docs/AMBIENTE-ONLINE.es.md).
