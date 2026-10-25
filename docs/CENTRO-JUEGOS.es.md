# Centro de Juegos y nuevo acabado visual · 0.9

Probado en TCL **50C645, Android 11, V652**. El Centro de Juegos forma parte de Casa Aura TV y se abre desde **Modo juego**. No instala un segundo launcher ni cambia el perfil de imagen al entrar.

## Entradas y laptop

Las tarjetas corresponden a las tres entradas HDMI de la C645. **ARC/eARC es HDMI 3**, no un cuarto conector. Mantén OK sobre una tarjeta para cambiar su nombre o restablecerlo. El nombre solo cambia dentro de Casa.

Casa consulta el estado que publica Android TV: conectada, en espera o sin señal/desconectada. «En espera» no demuestra que haya una consola conectada: este firmware devuelve ese estado incluso en las entradas sin una señal validada. Si no puede identificar el puerto muestra estado no disponible, sin adivinar números internos del fabricante.

El último uso cuenta aperturas desde este Centro; no registra cambios hechos con el mando o desde otras apps. Abre la entrada con el servicio original de TCL. Para una laptop, HDMI 1 es una buena primera opción; HDMI 2 también admite los modos indicados en la ficha de esta unidad. No se ha medido una diferencia de latencia. Consulta la [guía de conexión](JUGAR-C645.es.md).

**Panel de juego TCL** pide abrir el panel original. Puede necesitar HDMI activo. Casa no activa ALLM, no cambia resolución ni anuncia un modo Juego activado sin verificarlo.

## Mandos

**Conectar mando** abre los accesorios de Android/TCL, donde puedes buscar y vincular. En V652 el acceso Bluetooth estándar no existe; Casa usa la pantalla de accesorios verificada y conserva una alternativa para otros equipos.

Casa muestra los dispositivos de entrada que parecen mandos de juego: ejes de joystick o botones de juego, externos y no virtuales. Se excluyen el control remoto TCL y dispositivos internos que el firmware anuncia como gamepad. Es una detección orientativa, no una lista Bluetooth completa. Buscar dispositivos cercanos, vincularlos y reconectarlos sigue siendo tarea del sistema. No se añade un servicio para mantener conexiones.

La pantalla muestra nombre, estado conectado, tipo externo y última detección por Casa. **Android 11 no ofrece la API pública de batería de InputDevice**; se muestra «no disponible». En Android 12 o posterior se consulta solo si el dispositivo publica esa información. No se infiere una carga ni se promete compatibilidad con todos los mandos. No se ha probado un gamepad físico en esta versión.

## Navegador y juegos web

El navegador se crea al abrirlo y se destruye al salir. No se precarga al arrancar Casa. Incluye estos accesos a los catálogos de sus respectivos proyectos:

- [TIC-80](https://tic80.com/play/games/top): juegos de su comunidad.
- [PICO-8](https://www.lexaloffle.com/bbs/?cat=7): cartuchos publicados por sus autores.
- [DOS Zone](https://dos.zone/): juegos de PC en el navegador; muchos requieren teclado o ratón.

Los sitios, sus contenidos y condiciones pertenecen a terceros. Casa no incluye ROMs, no descarga APKs y no certifica la licencia o la compatibilidad de cada título. La disponibilidad puede cambiar. El rendimiento depende del juego y del componente Android System WebView instalado en el TV. El control remoto permite navegar enlaces y botones enfocados; muchos juegos necesitan un mando, teclado o ratón. No se transforma el mando del TV en un gamepad.

La barra permite volver, elegir catálogo, escribir una URL HTTPS, guardar la página actual, ampliar y cerrar. **Atrás** sale de la pantalla completa antes de retroceder de página. Se guardan hasta ocho favoritos y ocho páginas recientes, solo en este TV. «Borrar favoritos e historial» limpia esas listas; no borra cookies ni almacenamiento que los sitios hayan creado en WebView. Las listas, nombres HDMI e historial de mandos no forman parte de las copias de apariencia del launcher.

Para los juegos se permiten JavaScript y almacenamiento web. No hay puente JavaScript hacia Android, ventanas emergentes, cargas de archivos, descargas, acceso a archivos del TV, cámara, micrófono o geolocalización. Las navegaciones externas se limitan a HTTPS; no se ignoran errores de certificado. Los sitios reciben tu IP y los datos normales del navegador. Al salir se pausa la reproducción y se cierra la vista; el sistema puede conservar procesos de WebView en caché. La partida no se conserva al abandonar el navegador.

## Apariencia

**Ajustes → Apariencia → Efectos visuales** ofrece cuatro niveles:

| Nivel | Acabado |
| --- | --- |
| Desactivados | Superficies sólidas; selección visible y sin ampliación decorativa. |
| Bajos | Transparencia moderada y borde de selección. Recomendado para C645. |
| Medios | Más transparencia y brillo estático en la esquina superior. |
| Altos | Acabado medio y contornos más luminosos en las tarjetas. |

Son degradados y opacidades reales, sin refracción ni desenfoque del fondo en tiempo real. Inmersivo conserva su pequeño fondo de color calculado al cambiar de aplicación; Desactivados lo omite. El control de animación de selección se conserva por separado. Los niveles no alteran las animaciones del sistema ni los efectos del ambiente configurados por el usuario. No se promete un impacto de cero FPS.

La paleta común usa negros, blanco frío y azul; Juegos añade verde discreto. El degradado inferior ocupa el 32 % de Inicio y Ambiente, es transparente arriba y llega a negro abajo. Solo se recalcula al cambiar de tamaño. Mejora la unión visual, pero no recupera detalle perdido en la imagen original ni elimina las limitaciones del panel.

## Límites y próximos pasos

Los controles comprobados de Contenido adaptable siguen en [Imagen TCL](IMAGEN-TCL.es.md). No se ha descubierto acceso programable a AIPQ, brillo ambiente, contraste automático o cambios fiables por aplicación/SDR/HDR. No se añade automatización de imagen.

Para vídeo ambiente se mantiene separada la descarga de recursos, el catálogo y la presentación. El siguiente módulo deberá tener fuentes tipadas de imagen/vídeo, validación HTTPS y un reproductor con pausa, liberación al salir y límites de búfer. El catálogo actual acepta únicamente imágenes: no se introduce un campo de vídeo que la versión no pueda reproducir. GitHub puede servir catálogos o packs; un enlace a un archivo no garantiza servir vídeo correctamente. YouTube necesitará una integración oficial propia, sin extracción de enlaces de reproducción. Ningún vídeo se incluye en la APK de esta versión.

Referencias técnicas: [InputDevice y batería](https://developer.android.com/reference/android/view/InputDevice#getBatteryState()), [WebView y puentes nativos](https://developer.android.com/privacy-and-security/risks/insecure-webview-native-bridges), [PICO-8](https://www.lexaloffle.com/pico-8.php).
