# Casa Aura TV: resultados en TCL 50C645

Actualizado: **25 de octubre de 2026**. Android 11 / V8-R51MT05-LF1V652.021344, 2 GiB de RAM. Registro de un solo equipo, no certificación para toda la familia C645.

## Qué se consiguió

| Cambio | Resultado comprobado | Límite |
| --- | --- | --- |
| Launcher propio, independiente | Favoritas, HDMI, ambiente y ajustes propios sin Projectivy ni permiso de Internet | Los controles de imagen/sonido siguen perteneciendo a TCL |
| Caché de iconos acotada, fondo compartido y carga de apps fuera del hilo principal | Casa 0.2 mantuvo muestras de navegación con mediana de 5 ms por frame | No demuestra aceleración del sistema completo |
| Servicio de Inicio 0.3 | Menor espera configurada y bloqueo de duplicados más corto | No se estableció una mejora consistente de latencia total de Home |
| Protección individual de Guard | Casa en listas activa/persistente después de reinicio; tres retornos desde YouTube y suspensión/reactivación comprobados | Ajuste externo al APK; ver [guía de Guard](TCL-C645-GUARD.es.md) |
| Retirada selectiva autorizada | Hola, Proton VPN, Mercado Play, Projectivy y QIYI retirados, con APK respaldados; otras apps de streaming conservadas | Quitar una app inactiva no demuestra ganar FPS |
| Casa Aura 0.4 preliminar | Centro de control de nueve tarjetas, iconos propios y paneles secundarios adaptados a su contenido | Rediseña Casa, no los paneles propietarios del firmware |
| Casa Aura 0.5 | Cuatro apps más grandes, entradas agrupadas, biblioteca al bajar y acceso de juego | Señal física de laptop y modo Juego efectivo aún sin comprobar |

## Validación de 0.5

- Navegación desde Inicio a la biblioteca y regreso con Arriba/Atrás, conservando la selección anterior.
- Se mantienen las seis favoritas guardadas y las opciones de apariencia; cuatro se muestran en Inicio.
- El botón de juego abre su panel de Casa. Desde «Jugar en HDMI 1» se comprobó la apertura de `com.tcl.tv/.TVActivity`. Sin una laptop conectada no se valida resolución, vídeo, audio ni latencia.
- Catálogo cargado en el trabajador existente; las tarjetas y su distribución se reutilizan. Reabrir sin cambios no vuelve a vincular todos los iconos.
- Caché de 24 miniaturas, entre 96 y 192 píxeles de lado. A 128 píxeles son aproximadamente 1,5 MiB de píxeles para esa caché; no equivale a la memoria total del proceso.
- Se retiró la animación de todo el panel y se evita animar las favoritas detrás durante la transferencia de foco. No se añade actividad periódica en segundo plano.

En la última muestra del APK final, los contadores de la misma ventana pasaron de **38 a 47 frames** y de **6 a 9 frames lentos** durante tres aperturas/cierres con la biblioteca cargada: nueve frames adicionales, tres lentos. Es una prueba breve de apertura, no una medición de FPS sostenidos ni una promesa de ausencia de tirones. Muestras anteriores dieron resultados distintos; no se selecciona únicamente la más favorable.

La lectura de memoria de ese momento fue **75.831 KiB de PSS, 168.184 KiB de RSS y 215 KiB de Swap PSS**. El catálogo completo añade recursos respecto a un Inicio que no lo ha abierto; esta lectura no demuestra una reducción frente a 0.4. No se forzó recolección de memoria para obtenerla.

La guía de [laptop y modo Juego](JUGAR-C645.es.md) separa el puerto recomendado de las capacidades que faltan por probar. Los cambios de interfaz de 0.5 no alteran el procesamiento de imagen del TV.

## Ajustes nativos: lo que sigue pendiente

Tres aperturas de `com.android.tv.settings/.MainSettings` por condición:

| Contexto | Tiempos `am start -W` | Mediana |
| --- | --- | --- |
| YouTube reproduciendo, estado confirmado | 573 / 239 / 294 ms | 294 ms |
| YouTube pausado | 203 / 151 / 155 ms | 155 ms |
| Desde Casa, vídeo pausado | 144 / 125 / 143 ms | 143 ms |

Son muestras pequeñas, no aleatorizadas; el sistema llevaba poco tiempo iniciado. Sugieren coste adicional durante reproducción, no una causa única. El tiempo de actividad no equivale a tiempo desde pulsar el mando ni garantiza que todos los fotogramas estén dibujados.

Una traza posterior conservó una apertura de aproximadamente 410 ms; el hilo principal estuvo unos 74 ms ejecutándose, 59 ms listo esperando CPU y 277 ms dormido/esperando. No identifica por sí sola el servicio esperado. El búfer perdió parte del inicio y no se confirmó reproducción activa al empezar esa captura, por lo que no se utiliza para anunciar una mejora.

La barra rápida inferior y el panel lateral requieren identificación y medición separadas. No se extrapolan los resultados del menú completo. Los contadores de frames nativos resultaron acumulativos entre tandas y sus porcentajes no se comparan como muestras independientes.

## RAM: cómo interpretar las cifras

Lecturas puntuales de `dumpsys meminfo tv.casa.launcher`, todas del **proceso de Casa**, no de toda la TV:

| Lectura | PSS | RSS | Swap PSS |
| --- | ---: | ---: | ---: |
| 0.3 antes del rediseño, ya residente | 28.066 KiB | 79.620 KiB | 4.666 KiB |
| 0.4 durante la primera exploración de pantallas/apps | 71.198 KiB | 169.872 KiB | 218 KiB |
| 0.4 tras la revisión final del panel y regreso a Inicio | 33.459 KiB | 130.236 KiB | 201 KiB |

PSS reparte memoria compartida; RSS la contabiliza de otra manera. Las condiciones y la antigüedad de los procesos no coinciden: **no calculamos un porcentaje antes/después con esas filas**. El último PSS es aproximadamente 32,7 MiB; no es una promesa de consumo máximo.

Una publicación que afirma «282 → 96 MB» y «3 veces más rápido» necesita indicar dispositivo, firmware, proceso medido, métrica y tarea. La captura compartida no contiene ese protocolo. No puede usarse como resultado esperado ni como comparación con los 2 GiB totales de esta TV.

Para la siguiente comparación controlada se conservarán firmware, vídeo, resolución, tiempo tras arrancar y secuencia del mando; se separarán proceso del launcher, memoria global y latencias de Inicio/paneles. No se vaciará RAM ni forzará recolección para mejorar artificialmente una cifra.

## Cambios externos al launcher

En este equipo se redujeron `window_animation_scale` y `transition_animation_scale` a 0,5; `animator_duration_scale` quedó sin modificar. Eso acorta transiciones, no demuestra mayor capacidad de procesamiento. Se deshabilitaron individualmente TCL BI y OverseasAppShow; otras deshabilitaciones ya existían. Ninguno de esos cambios es requisito ni se aplica automáticamente al instalar Casa.

La IA de imagen «Foto Inteligente → Contenido adaptable» se observó activada y se conservó. No se demostró que provoque los tirones. No se deshabilitaron Guard, Google TV, setup, códecs, entradas, búsqueda, actualizador, DRM ni servicios de imagen y sonido.

## Comparación visual

![Centro de control de Casa Aura TV](images/aura-control.png)

Hasta 0.8 se conservaba el paisaje y la paleta cálida de Casa. La referencia de tvOS se limita a jerarquía, espacios, tarjetas y foco claro: no se copian gráficos de Apple ni se añade desenfoque en tiempo real. El diseño usa vistas nativas e iconos dibujados, sin imágenes adicionales ni servicios.

Referencias de contexto, no benchmarks de nuestra TV: [TV Detox](https://github.com/Ansub/tv-detox), [CleanYourTV](https://github.com/Gathin23/cleanyourtv), [foco y selección en las guías de Apple](https://developer.apple.com/design/human-interface-guidelines/focus-and-selection/).

## 0.6.0 · 2026-10-25

Tres presets seleccionados y revisados en el TV. Rotación de desierto, fiordo y bosque comprobada en la vista previa. CasaDream arrancó como salvapantallas real y quedó enlazado al gestor de sueños de Android; se salió con el mando. Se conservaron las seis favoritas y los tres ajustes anteriores tras actualizar. Pasaron las pruebas de Home, valores de diseño/ambiente y contrato de permisos. No se repitieron mediciones de RAM ni se demuestra una mejora de los paneles TCL. Las opciones de movimiento no cuentan aún con una prueba prolongada.

## 0.7.0 · 2026-10-25

Descarga manual desde el catálogo público de GitHub, activación y vista previa de los tres archivos comprobadas. Cancelar una actualización y volver a abrir conservó tres fondos completos. Borrar las descargas seleccionó los integrados y su vista previa funcionó. Se corrigió el recorte de texto de los botones observado en la primera prueba. Al terminar se dejó Moderno, 120 s, disolución y movimiento apagado; la galería remota quedó desactivada y sin archivos de prueba. Favoritas conservadas. No se hizo una desconexión de red ni una prueba prolongada de consumo.

## 0.8.0 · 2026-10-25

El control verificado se integró en Casa: permiso de Android solo para Configuración, lectura OFF, activación ON confirmada, Deshacer a OFF y reapertura sin cambios automáticos. La actualización y una instalación posterior de la misma versión conservaron el permiso. La comparación de exportaciones confirmó favoritas, preset y opciones de ambiente intactos. El registro de errores revisado no mostró fallos del proceso. No se verificó vídeo en reproducción, HDR por fuente ni mejora de los paneles nativos.


## 0.9.0 · 2026-10-25

Se revisaron Clásico, Moderno e Inmersivo con la paleta negra/azul y el degradado inferior. Ambiente abrió con el nuevo fundido inferior; no se cambiaron sus tiempos ni se activó movimiento. Se seleccionaron Altos, Desactivados y Bajos; la exportación incluyó el nivel elegido y conservó las seis favoritas. Bajos quedó seleccionado y se restauró el diseño Clásico que estaba activo antes de estas pruebas.

El Centro de Juegos detectó las tres entradas HDMI, GeForce NOW y Google Play Games. Android publicó «en espera» en los tres puertos; no se presenta como prueba de señal conectada. Se abrió el servicio original del TV y se verificaron el editor HDMI, guardar y restablecer un nombre y la pulsación larga corregida para evitar que se abra la fuente al editar. No se validó imagen de una laptop, resolución negociada ni latencia HDMI.

Desde Casa se abrió la pantalla real de accesorios. Se excluyeron el control remoto TCL y virtual-search, que el firmware anunciaba parcialmente como gamepad. No había un mando físico de juego para validar conexión, reconexión o batería. La información no disponible se muestra como tal.

TIC-80 cargó en el WebView del TV. Guardar un favorito, volver a verlo en el Centro de Juegos, ampliar la página y salir con Atrás funcionaron. La actividad del navegador quedó finalizada al salir. Esto valida el catálogo y la navegación, no una partida completa ni la compatibilidad de todos los juegos. PICO-8 y DOS Zone se comprobaron como fuentes oficiales en la web; sus juegos no se probaron en el TV.

Pasaron las pruebas de Home, límites de apariencia, galería, política de imagen y URLs del navegador, junto con el contrato de permisos y la firma del APK. El registro revisado no mostró excepciones de Casa. No se hizo un benchmark de FPS/RAM, una prueba prolongada ni una comparación de los paneles nativos de TCL. No se cambió la calibración, AIPQ o Contenido adaptable para esta versión.


## 0.10.0 · 2026-10-25

Se descargaron seis imágenes y se repitió la operación: quedaron seis listas y cero archivos descargados en el segundo intento. Se seleccionó y mostró la foto de 3840 × 2160 de WordPress Photo Directory; Android la presenta dentro de su interfaz 1920 × 1080. El índice permite elegir colecciones y favoritas sin consultar un servicio al abrir el panel. La actualización remota sigue siendo manual.

El vídeo ligero de NASA mostró imagen en la prueba. Se descartó su variante Full HD de gran tamaño al no funcionar con fluidez en esta sesión. La muestra Full HD de Big Buck Bunny avanzó hasta 9914 ms y leyó 1.046.987 bytes; el usuario confirmó que se veía y se movía en la pantalla física. El plano de vídeo acelerado de esta TCL no apareció en la captura ADB. El reproductor tenía un cliente asociado al proceso de Casa antes de salir y ninguno después de volver.

Se revisó el regreso a imágenes tras fallos de carga. Las pruebas de lectura cubren cabeceras repetidas, cruces entre bloques, cancelación y un presupuesto exacto de datos. También pasaron las políticas de Home, diseños, galería anterior, imagen TCL, navegador y el contrato del APK. Las pruebas de cuota son del lector de vídeo, no una medición del tráfico facturado por la red.

Se dejaron Todas las imágenes, vídeo automático desactivado y filtros neutros. No se realizó una prueba prolongada de salvapantallas con vídeo, una desconexión física de Wi-Fi, una prueba HDR ni un benchmark nuevo de FPS/RAM. HLS, DASH y YouTube no se incorporan a esta versión. El APK firmado ocupa 6.357.960 bytes, unos 24 KiB más que 0.9.0; las imágenes y vídeos online no están empaquetados en él.
