# Velira TV 0.11 · en desarrollo

Actualizado el 28 de octubre de 2026. El código se comparte en GitHub como trabajo en curso; no hay una release 0.11. La versión pública sigue siendo Casa Aura TV 0.10.0. Esta candidata conserva `tv.casa.launcher`, el certificado original, MainActivity, HomeService, CasaDream y los archivos/claves de preferencias. No se ha creado otra clave ni se han borrado datos.

## Interfaz y marca

Se adaptaron Clásico, Moderno e Inmersivo a las referencias del usuario. Azul profundo, acento cian, bandeja común de aplicaciones, superficies translúcidas estáticas, espacios amplios y un contorno de foco visible incluso con efectos apagados. Clásico conserva seis favoritas y barra de acciones; Moderno muestra cuatro con utilidades 2×2 y conserva seis guardadas; Inmersivo mantiene carrusel, contexto y fondo suavizado 48×27 con caché de cuatro y debounce de 90 ms.

Se usan funciones reales. El usuario aprobó omitir clima, carteles, recomendaciones y Smart Home de las referencias. No hay vídeo ni desenfoque continuo en Inicio. Se añadieron icono/banner vectoriales originales y la marca visible Velira TV. Los banners de las aplicaciones se obtienen del software instalado; no se redistribuyen sus APK.

Los fondos originales son dunas ámbar, fiordo azul/teal y costa índigo/coral. La herramienta generó 1672×941; los derivados 1920×1080 no son 4K nativo. Los tres WebP suman 518032 bytes. `assets/home/README.md` conserva prompts y `provenance.json`, hashes, dimensiones y comparación JPEG/WebP/PNG. Se eligió ARGB8888 para estos fondos de Inicio; cada fondo 1080p supone unos 7,9 MiB de píxeles. No se anuncia una reducción de RAM. Ambiente conserva su decodificación RGB565 anterior, pendiente de comparación visual prolongada.

## Correcciones y evidencia

| Prioridad | Archivos | Cambio y evidencia |
| --- | --- | --- |
| P0 | RangeMediaSource / RangePolicy | Un cuerpo truncado lanza error, no EOF. La nueva prueba falla contra HEAD original y pasa con la corrección. Los rangos parciales válidos continúan con número acotado de respuestas. Se validan inicio, fin, total y Content-Length. |
| P0 | RangeMediaSource / AmbientHttp | ETag fuerte por sesión e If-Range. Se rechaza el cambio de representación, un 200 en búsqueda no inicial, 416 y cabeceras incoherentes. Sin validador fuerte solo se admite una respuesta HTTP y los bloques ya almacenados; una nueva búsqueda vuelve a fotos. No hay reintentos automáticos de respuestas fallidas. |
| P0 | OnlineAmbientActivity | Al cancelar y volver se reconstruyen los controles, se mantiene pestaña/foco/scroll solicitado y no se reinicia la operación. La instrumentación de la TV probó cancelación y reentrada en la misma instancia mediante callbacks controlados. No equivale a suspensión física. |
| P0 | AmbientSession / AmbientScene | Estados PHOTO/PREPARING/VIDEO/FALLBACK/STOPPED y tokens que descartan eventos antiguos. Se conserva una foto durante preparación y vídeo; perder Surface recupera la foto sin esperar otra decodificación. Dos recuperaciones reales de Surface pasaron en TV. |
| P0 | AmbientVideo | Cola multimedia compartida: todas las llamadas MediaPlayer ocurren allí y se completa release antes de crear el reemplazo. El plazo de preparación de 15 s se agenda fuera de la cola. Cancelación de red fuera de UI y cierre idempotente. Las escenas reemplazadas vuelven a foto. |
| P1 | ImageTransfer / AmbientHttp / OnlineAmbientStore | Descarga directa a archivo temporal con SHA256 incremental, límites, validación y rename posterior. Sin ByteArrayOutputStream de imagen completa. Pruebas de truncado, exceso y cancelación; descarga HTTPS real con tamaño/hash correctos en TV. |
| P1 | OnlineAmbientActivity | Catálogo, selección local y recuento de caché se leen en un trabajador y se presentan como una instantánea invalidable. Cambiar un ajuste de aspecto no vuelve a analizar JSON ni recorre el directorio. |
| P1 | Assets / AppLibraryView / MainActivity | Iconos/banners asíncronos con placeholder, cola de 32 tareas y verificación del vínculo/generación. Se descartan resultados de paquetes obsoletos. No se reciclan bitmaps compartidos ni se fuerza GC. |
| P1 | AmbientScene / AmbientGallery | Verificación de SHA fuera de UI y resultado de decodificación con crédito propio. Un archivo local dañado vuelve a un paisaje integrado con su crédito; probado con un archivo temporal, sin alterar la galería del usuario. |

La prueba de transporte también cubre TLS, límite de redirecciones HTTPS, rechazo de bajada a HTTP, ETag condicional/304, cuotas exactas del lector y cancelación. Es una prueba de lógica con HTTP simulado; no sustituye todos los escenarios de red física.

## Ensayos en la C645

TCL 50C645, Android 11, firmware V8-R51MT05-LF1V652.021344. Se conservaron las seis favoritas. HomeService y CasaDream siguen seleccionados. Se comprobaron Clásico, Moderno, Inmersivo, biblioteca, Atrás, carrusel, mantener OK y foco con efectos apagados. El preset del TV al retomar era Clásico; no se impuso el elegido en la sesión anterior.

La primera tanda con captura completa registró 20 creaciones y 20 liberaciones, máximo simultáneo 1 y cero pendientes al terminar. Hubo dos primeros fotogramas de la muestra 1080p y dos recuperaciones al perder Surface. La fuente respondió 206 con ETag fuerte; los registros incluyen rango/longitud/tiempo/bytes, sin URL. No son mediciones de FPS ni pruebas de 60 minutos.

Se añadieron a la regresión dos escenas concurrentes para verificar el relevo y un bloqueo controlado de la cola multimedia para comprobar el plazo independiente. El relevo entre dos escenas pasó. La prueba aislada del plazo de 15 s también pasó con la cola bloqueada de forma controlada; esto comprueba el supervisor independiente, no todos los bloqueos de red reales. La última tanda rápida completa quedó interrumpida por la incidencia del sistema descrita abajo. El auxiliar de pruebas se instala aparte con la misma firma y se retira al terminar; no forma parte del APK del launcher.

Pasaron las suites Home, diseños, galería, imagen TCL, juegos y online; compilación/firma y contrato del APK. Se corrigieron barras invertidas en rutas ZIP de assets y se fijó compileSdk android-37.0, JDK21 y build-tools36.0.0, conservando minSdk26 y targetSdk30. CI prepara explícitamente esa plataforma; su ejecución remota queda pendiente de publicar el lote.

## Tecnología conservada

Java y vistas Android nativas, MediaPlayer/SurfaceView y herramientas oficiales del SDK. Ventaja actual: no se añade una biblioteca de reproducción ni una capa de UI nueva. Coste: el lector HTTP, las cuotas y el ciclo de vida requieren pruebas propias. Media3 en Java sigue siendo una alternativa para HLS/DASH; antes habría que medir módulos, APK, buffers, cuota, latencia y liberación. No se afirma que consuma menos RAM. HTTPS directo de MediaPlayer tampoco conserva automáticamente nuestra cuota exacta.

## Incidencia abierta durante el ensayo

Una tanda posterior de cambios rápidos de ventanas se abortó con `INSTRUMENTATION_ABORTED: System has crashed`. El registro de Android del 26 de octubre a las 19:27:26 muestra SIGSEGV en `system_server`, hilo `android.anim`, dentro de ART e InsetsController/ViewRootImpl. El proceso de Velira tenía otro PID; no se registró una excepción Java de Velira como causa.

La TV recuperó por sí sola el servicio de actividades. Esto no demuestra independencia del ensayo: la secuencia rápida de superficies/ventanas pudo ser un disparador. Esa tanda no se considera aprobada, aunque las anteriores sí terminaron. Se suspendió su repetición inmediata y se separó la prueba del plazo de preparación. La incidencia sigue abierta y bloquea la publicación como versión terminada.

## Pendiente antes de publicar

- Comprobaciones puntuales de los casos pendientes: pérdida de red, cuotas y suspensión/reactivación. Las sesiones largas no se retomarán automáticamente.
- Una comparación breve y controlada de Home, biblioteca, frames y memoria, cuando un cambio concreto lo requiera. No hay un benchmark nuevo ni un margen de no regresión demostrado.
- Pruebas de 0/1 favoritas, otras escalas y equipos, cambios reales de paquetes y todas las rutas de restauración.
- Vídeos con metadatos al final, fuente sin ETag fuerte, varias redes, códecs y calidad superior. La política conservadora puede rechazar algunas URLs.
- Las copias de apariencia no incluyen todos los ajustes online/juegos; se conserva ese alcance.
- Revisar la documentación pública e historial visual al cerrar la versión. No se publicó una release parcial.

No se tocaron firmware, debloat, Guard, Google TV, DRM, animaciones globales, calibración ni AIPQ. No se añadieron HLS/DASH/YouTube ni servicios remotos. Los tirones de los paneles nativos de TCL siguen siendo un asunto separado.

Criterio indicado por el usuario: diagnósticos puntuales y breves, vinculados a una duda concreta; evitar tandas largas, repeticiones y seguimiento minuto a minuto. La observación de fotos se detuvo; la prueba larga de vídeo no se inició. Tras reconectar ADB se conservaron los mismos PID de Android y Velira, pero esto no cierra la incidencia previa.

Diagnóstico puntual posterior: se configuraron tamaño y flags del centro de control antes de Dialog.show(), conservando su diseño. La apertura/cierre y el APK se validaron. Las dos capturas breves no fueron equivalentes y no establecen una mejora de rendimiento; se conservaron ambas. No se repitieron pruebas prolongadas ni se publicó una release.
