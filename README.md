# Casa TV Launcher

**Un inicio tranquilo para tu televisor.** Launcher independiente para Android TV y Google TV, con favoritas, accesos HDMI y salvapantallas ambiente.

[Descargar APK y versiones](https://github.com/arleyjesus/casa-tv-launcher/releases) · [Instalación](docs/INSTALL.es.md) · [Compatibilidad](docs/COMPATIBILITY.md) · [English](README.en.md)

![Pantalla real de Casa TV Launcher](docs/images/home.png)

## Qué ofrece

- Inicio sin anuncios propios, cuentas ni conexión a Internet desde Casa.
- Hasta seis favoritas, editables y ordenables con el mando.
- Lista de aplicaciones instaladas y accesos a entradas HDMI.
- Panel rápido que abre los ajustes originales de TCL.
- Paisaje estático, reloj y selección discreta; sin vídeo de fondo.
- Opciones para ocultar título/reloj y desactivar la animación de selección.
- Copias locales y restauración de favoritas y apariencia.
- Salvapantallas ambiente independiente, sin PIN.

**No necesita Projectivy, root ni desactivar el inicio de Google.** No elimina anuncios dentro de otras aplicaciones ni modifica sus interfaces.

## Estado del proyecto

**Preparación local: v0.3.0, pendiente de validación final y publicación.** Ajusta el servicio de Inicio y añade una miniprueba de regresión; todavía no demuestra una mejora general del rendimiento ni resuelve los tirones de los paneles TCL.

**Vista previa pública: v0.2.0.** Se desarrolló y probó en un **TCL 50C645 con Android 11 y firmware V8-R51MT05-LF1V652.021344**. El manifiesto permite Android 8.0/API 26 o posterior, pero otros modelos y versiones aún no están validados.

Los accesos a ajustes y entradas incluyen comportamiento específico de TCL. La interfaz está actualmente en español. Las tres entradas HDMI están fijadas en esta versión. Consulta la [matriz de compatibilidad y limitaciones](docs/COMPATIBILITY.md) antes de instalar.

## Instalar o actualizar

1. Descarga la APK de una [versión publicada](https://github.com/arleyjesus/casa-tv-launcher/releases).
2. Instálala y ábrela primero como una aplicación normal.
3. Prueba navegación, aplicaciones, imagen, sonido y entradas antes de cambiar Inicio.
4. Si quieres que Home abra Casa, activa **Casa · Botón Inicio** en Accesibilidad.

La [guía completa](docs/INSTALL.es.md) explica ADB, actualización conservando datos, autoarranque TCL, ambiente y cómo volver al inicio anterior. **No se necesita aplicar una lista de debloat para usar Casa.**

Mantén **OK** sobre una favorita para editarla. En **Ajustes** encontrarás ordenación, apariencia y copias de configuración.

## Ambiente

![Salvapantallas real de Casa](docs/images/ambient.png)

Casa incluye tanto una vista previa manual como un servicio de salvapantallas Android. Su selección y activación dependen del firmware. No es una pantalla de bloqueo: al suspenderse realmente el TV, la pantalla se apaga.

## Privacidad y permisos

La APK no solicita permiso de Internet ni acceso general al almacenamiento. Favoritas y preferencias permanecen en el dispositivo. El servicio opcional de Accesibilidad recibe únicamente cambios de ventana de los inicios de Google TV y Projectivy, sin capacidad de leer su contenido. [Detalle de privacidad](docs/PRIVACY.md).

## Rendimiento, sin promesas infladas

Casa usa vistas Android nativas y recursos locales. La muestra de navegación estabilizada de v0.2.0 registró 0 fotogramas lentos de 171 y mediana de 5 ms en el TV probado. La versión anterior también navegaba bien: **esto no demuestra una aceleración general del televisor**. [Método y resultados](docs/COMPATIBILITY.md#rendimiento).

## Compilar y contribuir

El proyecto usa Java y herramientas oficiales del SDK, sin dependencias externas de aplicación ni Gradle en esta versión. [Compilación y firma](docs/BUILD.md) · [Contribuir](CONTRIBUTING.md) · [Hoja de ruta](ROADMAP.md) · [Historial](CHANGELOG.md).

Para informar de un fallo, abre un [issue](https://github.com/arleyjesus/casa-tv-launcher/issues) con modelo, Android, firmware y pasos de reproducción. No publiques cuentas, IP, credenciales, claves ni volcados completos del TV.

## Licencia y créditos

Código y recursos originales bajo [MIT](LICENSE): puedes usarlos, modificarlos y compartirlos conservando el aviso de licencia. El paisaje se creó con asistencia de IA. Los nombres e iconos de las aplicaciones pertenecen a sus titulares; Casa obtiene los iconos de las apps instaladas y no redistribuye sus APK. [Créditos](NOTICE.md).

Proyecto independiente, sin afiliación con TCL, Google, Projectivy ni las plataformas de streaming.
