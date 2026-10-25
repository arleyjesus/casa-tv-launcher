# TCL C645: Inicio estable y protección frente a Guard

Caso comprobado: **TCL 50C645, Android 11, firmware V8-R51MT05-LF1V652.021344, Guard 1.01.10_fc74127_241126**. Otros tamaños, regiones y firmwares no están validados. Esta guía documenta una intervención en el televisor de referencia; **instalar Casa Aura TV no modifica Guard automáticamente**.

## Tres controles diferentes

| Control | Función comprobada o alcance |
| --- | --- |
| Accesibilidad: Casa / Casa Aura · Botón Inicio | Detecta la aparición de los inicios admitidos y abre Casa. No lee el contenido de la pantalla ni intercepta teclas. |
| TCL `APP_AUTO_START` | En este firmware, permitirlo conservó el servicio de Casa tras reiniciar. No impidió todos los cierres posteriores de Guard. |
| `TGuardMemoryManager / KillWhiteList` | Excluye aplicaciones seleccionadas de un mecanismo de cierre de procesos de Guard. No desactiva la gestión de memoria de Android. |

En los apartados de lanzamiento automático e inicio de relevancia, **Administrador automático: Abierto** deja a Guard decidir. No significa permitir todas las aplicaciones. Esos controles tampoco equivalen a la lista de protección de memoria.

## Inspección y respaldo antes de intervenir

El script incluido es **solo de lectura**. Con ADB autorizado, desde la raíz del proyecto:

```powershell
./scripts/inspect-tcl-guard.ps1 -AdbPath 'RUTA/adb.exe' -TvEndpoint 'IP_DEL_TV:PUERTO_ADB'
```

Guarda las respuestas originales en `build/guard-inspection/`, excluido de Git. Informa si Casa figura en las listas persistente y activa. No concede permisos, no crea entradas y no instala aplicaciones. Un proveedor ausente, una respuesta ambigua o un formato distinto impiden dar por confirmada la protección.

La consulta persistente usada en el equipo de referencia fue:

```powershell
./adb.exe -s 'IP_DEL_TV:PUERTO_ADB' shell content query --uri content://com.tcl.config.ConfigProvider/TGuardMemoryManager --projection project_id:config_content
```

La lista activa se obtuvo con:

```powershell
./adb.exe -s 'IP_DEL_TV:PUERTO_ADB' shell dumpsys activity service com.tcl.guard/.memorymanager.TGuardService
```

Estas consultas no son un procedimiento de instalación universal. No publiques las respuestas completas: pueden incluir las aplicaciones de tu TV.

## Intervención realizada en la C645 de referencia

1. Guardar el JSON original completo y todas las entradas únicas de la lista activa **antes de escribir**.
2. Validar que existe una única fila y una única entrada `KillWhiteList`, con una lista de paquetes legible.
3. Conservar claves desconocidas, tipos y entradas existentes; añadir exclusivamente `tv.casa.launcher`.
4. Actualizar la fila persistente mediante `ContentResolver` y leerla de nuevo. El comando `content --bind` de este firmware rechazó el JSON, por lo que no se distribuye una línea de escritura que pueda fallar o reemplazar indebidamente la política.
5. Añadir Casa a la lista activa del servicio y comprobar la unión completa. El comportamiento observado de este control no permite asumir que pasar la lista anterior elimine inmediatamente una entrada añadida.
6. Probar tres retornos desde YouTube, suspensión/reactivación y un reinicio real.
7. Confirmar que Guard vuelve a cargar Casa en su lista, que Inicio funciona y que Accesibilidad sigue vinculada.

Se utilizó una actividad auxiliar temporal sin Internet ni servicios residentes, con acceso restringido a la herramienta de depuración. Se retiró después. **El auxiliar y una automatización de escritura no forman parte de esta distribución pública**: la guía y el inspector no deben confundirse con un instalador de esa protección.

Resultado verificado: lista persistente de **13 → 14** entradas y lista activa de **100 → 101**; la única adición fue Casa. La protección se mantuvo tras reiniciar y el proceso se conservó durante la prueba de suspensión/reactivación. No se activó un monitor periódico ni se añadieron excepciones globales de batería.

## Recuperación

- Para volver a Google TV inmediatamente, desactiva el servicio de Inicio de Casa desde Accesibilidad. Conserva el launcher original instalado.
- Para deshacer la excepción de memoria, restaura **tu JSON original** después de comprobar que no sobrescribes cambios posteriores ajenos a Casa. Reinicia para recargar la política activa y vuelve a consultar ambas listas.
- No copies una lista de otro televisor ni borres `com.tcl.guard`, su proveedor, la búsqueda de Ajustes o servicios de entradas/imagen.
- Si no conservas el respaldo, retirar únicamente la entrada de Casa requiere revisar la política actual. No restaures valores de fábrica por conjetura.

La protección reduce una causa observada de recarga de Casa, pero no garantiza que Android nunca termine su proceso ni corrige el código de los paneles TCL.

Referencias del mecanismo: [TCL Guard Manager](https://github.com/eliotcougar/tcl-guard-manager), [protocolo del servicio](https://github.com/eliotcougar/tcl-guard-manager/blob/main/app/src/main/java/com/eliotcougar/tclcompanion/GuardProtocol.kt), [formato persistente](https://github.com/eliotcougar/tcl-guard-manager/blob/main/app/src/main/java/com/eliotcougar/tclcompanion/TclProtectionProtocol.kt). Usa interfaces no documentadas de TCL; una coincidencia de nombre comercial no prueba compatibilidad.
