# Pruebas temporales en dispositivo

`Checks.java` es una instrumentación de Android separada de la aplicación distribuida. Debe firmarse con el certificado autorizado del launcher. No necesita permisos adicionales en Velira ni una compilación debuggable.

Desde PowerShell, con Android SDK/JDK y las variables de firma habituales ya configuradas:

```powershell
./scripts/test-device.ps1 -AdbPath 'RUTA/adb.exe' -TvEndpoint 'IP:5555' -TargetApkPath 'RUTA/Velira-TV.apk' -AndroidSdk 'RUTA/Android/Sdk' -JavaHome 'RUTA/JDK21'
```

El script actualiza el launcher con `install -r`, instala el auxiliar, ejecuta las pruebas y retira únicamente `tv.casa.launcher.check` al terminar. No desinstala el launcher ni borra sus datos. El paquete, los componentes y el certificado del launcher deben conservarse. No incluyas claves ni sus contraseñas en el repositorio.

La prueba utiliza el vídeo que ya esté elegido en Ambiente; debe ser compatible y accesible por la red permitida. No activa el vídeo automático ni cambia la selección. Interrumpe visualmente el uso del televisor durante el ensayo. Hace una descarga real de la primera imagen del catálogo a un archivo temporal, valida tamaño/SHA y lo elimina. Crea un archivo temporal dañado para comprobar el respaldo y su crédito; no daña las imágenes del usuario.

Comprueba cancelación/reentrada a la misma instancia del panel mediante callbacks de ciclo de vida controlados, conserva la pestaña y exige controles habilitados sin reiniciar la tarea. No sustituye una prueba de suspensión real. También prueba dos escenas simultáneas y veinte entradas/salidas de la vista previa, incluidas preparación cancelada y dos pérdidas de superficie después de recibir vídeo.

Los resultados y registros quedan dentro de `build/`, excluido de Git. Los registros multimedia contienen sesión, estado, rango HTTP, tiempos y bytes; no imprimen URLs. El proceso conserva una cola multimedia compartida y colas de cancelación con un trabajador cada una; tener estos hilos residentes no equivale a mantener un decodificador activo.

Estas son regresiones breves. No certifican sesiones de 60 minutos, HDR, todas las redes, formatos, firmwares ni la pulsación física del mando.

El modo -Mode deadline ejecuta únicamente la prueba de 15 segundos: bloquea la cola multimedia mediante una barrera controlada, comprueba el respaldo y la desbloquea en finally. No simula por sí solo todas las formas de bloqueo de I/O. Una tanda rápida en C645/V652 coincidió con un SIGSEGV de system_server; no se considera aprobada y no se debe usar este auxiliar como certificación de estabilidad. La prueba incluye una pausa entre ventanas, pero no se afirma que eso resuelva el fallo del firmware.
