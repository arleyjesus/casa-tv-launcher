# Conectar una laptop para jugar en la TCL 50C645

## Puerto y resolución

Para la laptop usaría **HDMI 1**. HDMI 2 también es una opción para 4K a 60 Hz; no hemos medido una diferencia de latencia entre ambos. Dejaría **HDMI 3** libre si vas a usar una barra de sonido o receptor por ARC/eARC.

En el TV de referencia, Android identifica el puerto 3 como ARC y reporta soporte eARC. La [ficha oficial de la 50C645](https://www.tcl.com/content/dam/brandsite/region/es/product-documents/product-sheets/c645/11-18-43_2344_754_ExportPDFTCL_50C645_TECHSPECS_ES.pdf) coincide: eARC en HDMI 3 y resolución máxima HDMI de **2160p/60 Hz**. La ficha regional no sustituye comprobar qué modos ofrece tu unidad con la laptop conectada.

No se deduce 4K/120 Hz de la etiqueta HDMI 2.1 ni de la publicidad de otras pulgadas de la serie. El límite de la salida de la laptop y el cable también cuentan. Todavía no se ha probado una señal física de PC en el equipo de referencia.

## Primera conexión

1. Conecta la laptop directamente a HDMI 1.
2. En Windows pulsa **Win + P** y elige **Solo segunda pantalla** si vas a jugar exclusivamente en la TV.
3. En **Configuración > Sistema > Pantalla > Pantalla avanzada**, selecciona la TCL. Empieza por **1920 × 1080 a 60 Hz**; usa **3840 × 2160 a 60 Hz** si Windows lo ofrece y tu equipo mantiene el rendimiento del juego.
4. En Casa abre **Modo juego > tarjeta HDMI 1**.
5. Con la señal HDMI visible, abre los ajustes de imagen del TV y selecciona **Juego**, o revisa **Game Master** si aparece. Una laptop no tiene por qué activar ALLM automáticamente.
6. Comprueba que no se recortan los bordes del escritorio. Revisa el ajuste de tamaño/overscan del TV si ocurre.

La [ficha australiana de la 50C645](https://www.tcl.com/content/dam/brandsite/region/australia/AU_Mediacenter/download/50C645_Product_Specifications_Final.pdf) recoge modos Juego/PC y ALLM. El [soporte de TCL](https://support.tcl.com/en_US/troubleshooting-motion-issues-on-your-tcl-google-tv) explica que Juego reduce latencia de entrada y cambia el procesamiento de movimiento. No cambiamos estos ajustes mediante comandos internos del fabricante.

## Qué hace el botón de Casa

- **Tarjeta HDMI 1:** abre la entrada mediante el servicio original de TCL. Se comprobó la apertura de `com.tcl.tv/.TVActivity`; no se validó vídeo de un PC conectado.
- **Abrir panel de juego de TCL:** solicita la actividad que ofrece este firmware. Puede cerrarse o no mostrar controles si no hay una señal HDMI compatible activa.
- **Tarjetas HDMI:** permiten abrir HDMI 1, 2 o 3 desde Casa. Mantén OK para renombrar.

Casa no anuncia el modo como «activado», no cambia el modo de imagen ni fuerza resoluciones. Los controles efectivos siguen en el firmware de TCL.

Desde 0.9 el [Centro de Juegos](CENTRO-JUEGOS.es.md) añade estado HDMI publicado por Android, último uso desde Casa, acceso a accesorios, juegos instalados y navegador. ARC/eARC comparte la tarjeta HDMI 3.
