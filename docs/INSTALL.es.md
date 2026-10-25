# Instalación, actualización y recuperación

Desde 0.4 el nombre visible es **Casa Aura TV**. El paquete `tv.casa.launcher`, la firma y las preferencias se conservan. Las etiquetas de servicio antiguas «Casa · Botón Inicio» / «Casa · Ambiente» pasan a «Casa Aura · Botón Inicio» / «Casa Aura · Ambiente».

Para el dispositivo de referencia, consulta además [protección de memoria de TCL Guard](TCL-C645-GUARD.es.md) y [resultados publicados de la C645](RESULTADOS-C645.es.md). Son ajustes y pruebas del TV, no cambios que el APK aplique al instalarse.

## Antes de instalar

La versión actual se prueba en TCL 50C645, Android 11, V8-R51MT05-LF1V652.021344. No instales firmware de otro modelo para usar Casa. Guarda una vía para abrir los ajustes originales y conserva habilitados Google TV y los servicios TCL.

Descarga la APK y SHA256SUMS.txt desde [Releases](https://github.com/arleyjesus/casa-tv-launcher/releases). No hace falta Projectivy, root, una cuenta de Casa ni aplicar debloat.

## ADB desde Windows

Descarga [Platform-Tools oficiales](https://developer.android.com/tools/releases/platform-tools). Activa la depuración del TV y utiliza exclusivamente una red de confianza. Algunos firmwares usan ADB de red tradicional; otros presentan emparejamiento inalámbrico con dos puertos distintos. No asumas que todos usan 5555 ni abras puertos del router.

Ejemplo con la IP y el puerto que correspondan a tu televisor:

```powershell
./adb.exe connect "IP_DEL_TV:PUERTO_ADB"
./adb.exe devices -l
./adb.exe -s "IP_DEL_TV:PUERTO_ADB" install -r ./Casa-Aura-TV-v0.5.0.apk
./adb.exe -s "IP_DEL_TV:PUERTO_ADB" shell am start -n tv.casa.launcher/.MainActivity
```

Acepta la solicitud de autorización de tu PC en el TV. Si aparece unauthorized/offline, resuelve la conexión antes de instalar. Para emparejamiento moderno usa `adb pair` con el puerto de emparejamiento y después `adb connect` con el puerto de conexión. [Documentación de ADB](https://developer.android.com/tools/adb).

## Home: opcional

Prueba Casa como aplicación normal. Si quieres redirigir Home, entra en los ajustes de Accesibilidad y activa **Casa · Botón Inicio**. Su función es abrir Casa cuando aparece el inicio de Google TV/Projectivy. No lee su contenido ni intercepta las teclas.

Evita tener varios servicios intentando sustituir Inicio a la vez. Puedes desactivar el de Projectivy si usabas uno. **No reemplaces toda la lista de servicios de Accesibilidad mediante un comando genérico.**

El panel de Casa puede abrir temporalmente Google TV/Projectivy; la redirección se omite durante 60 segundos para permitir su uso.

### Autoarranque TCL: solo si tu firmware lo ofrece

En el equipo de referencia, TCL bloqueaba el arranque del servicio mediante la operación de fabricante APP_AUTO_START. Consulta primero y anota el valor:

```powershell
./adb.exe -s "IP_DEL_TV:PUERTO_ADB" shell cmd appops get tv.casa.launcher APP_AUTO_START
```

Solo si reconoce esa operación y has decidido permitir el autoarranque de Casa:

```powershell
./adb.exe -s "IP_DEL_TV:PUERTO_ADB" shell cmd appops set tv.casa.launcher APP_AUTO_START allow
```

Si responde Unknown operation, detente: no es un comando universal de Android. No desactives TCL Guard ni pruebes nombres/índices de operaciones al azar. Para revertir, restaura el valor que anotaste; en el dispositivo probado era `ignore`.

**Autoarranque no garantiza permanencia en memoria.** En la C645 se observó un cierre de Casa por Guard incluso con esa operación permitida. Se comprobó por separado una excepción individual en sus listas persistente y activa: [método, respaldo, límites y recuperación](TCL-C645-GUARD.es.md).

## Ambiente: opcional

Primero abre **Ambiente · vista previa** desde Casa. Si tu firmware ofrece un selector de salvapantallas, selecciona **Casa · Ambiente** y conserva los tiempos de reposo que prefieras.

Como alternativa para el firmware comprobado, consulta y guarda la selección anterior:

```powershell
./adb.exe -s "IP_DEL_TV:PUERTO_ADB" shell settings get secure screensaver_components
```

Para seleccionar el servicio de Casa:

```powershell
./adb.exe -s "IP_DEL_TV:PUERTO_ADB" shell settings put secure screensaver_components tv.casa.launcher/.CasaDream
```

Para revertir, restaura exactamente el componente que anotaste. El del equipo de prueba era `com.google.android.apps.tv.dreamx/.service.Backdrop`; puede ser diferente en tu TV. No cambies el tiempo de apagado si no necesitas hacerlo. El modo ambiente mantiene la pantalla encendida; no es suspensión ni bloqueo de seguridad.

## Actualizar

Instala una nueva APK oficial con `adb install -r`, conservando paquete y certificado de firma. Casa mantiene las preferencias. Comprueba que realmente estás instalando una versión más nueva. No desinstales para resolver un conflicto de firma sin copiar antes tus datos.

En Casa, **Ajustes > Copias de configuración** permite crear y restaurar favoritas/apariencia. Las copias están en el almacenamiento propio de la app; una desinstalación puede borrarlas. Para guardarlas fuera del TV con ADB:

```powershell
./adb.exe -s "IP_DEL_TV:PUERTO_ADB" pull /sdcard/Android/data/tv.casa.launcher/files/backups ./Casa-backups
```

No compartas esas copias sin revisarlas: incluyen los paquetes de tus favoritas.

## Volver al inicio anterior

1. Desactiva **Casa · Botón Inicio** desde Accesibilidad.
2. Restaura tu launcher/servicio previo si lo utilizabas.
3. Si seleccionaste Casa como salvapantallas, restaura el anterior.
4. Verifica Home, ajustes, entradas y reposo. Después puedes desinstalar Casa desde Aplicaciones.

Casa no necesita que se desactive `launcherx`, `setupwraith`, el actualizador, DRM, mando, entradas o ajustes del fabricante. No ejecutes listas de eliminación masiva como parte de esta instalación.

Al terminar con ADB, desactiva la depuración si ya no la necesitas. Desconectar el PC por sí solo no apaga la depuración del TV.
