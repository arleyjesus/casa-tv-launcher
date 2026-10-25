# Privacy / Privacidad

Versions through 0.6 have no INTERNET permission. Starting with 0.7, INTERNET is declared for the optional gallery; 0.9 also uses it for web pages explicitly opened from the game hub. There is no account system, analytics SDK, ad SDK or network APK updater.

Opening Home, Ambient or the gallery screen does not contact GitHub. Only pressing Download/Update starts HTTPS requests to raw.githubusercontent.com for the public arleyjesus/casa-tv-launcher catalog and images. GitHub receives the connecting IP and normal HTTP request metadata (including a Casa version User-Agent). Casa does not send favorites, installed-app lists, accessibility events or device identifiers. Requests stop on leaving the gallery; socket timeouts are 10 seconds and no periodic work is scheduled.

Downloaded images and their catalog use app-private internal storage. The active pack is capped at three PNGs, 4 MiB each, with at most one replacement pack staged during update (24 MiB image data in total, plus small catalogs/filesystem overhead). A successful update removes the previous pack. Clear removes downloaded packs and selects built-in images. Gallery use is off by default. The remote-image preference is included in settings backups; image files are not. With a missing pack the built-in gallery is used.

The app stores favorite package names, appearance options, previous focus and a short-lived Home-redirection exception on the TV. Local backups include favorites and appearance only. They are not uploaded by Casa. Android or the user's backup/file-transfer tools may handle files separately.

## Accessibility

Casa · Botón Inicio is optional. It receives TYPE_WINDOW_STATE_CHANGED events filtered to `com.google.android.apps.tv.launcherx` and `com.spocky.projengmenu`, then checks their home activity names. Its configuration sets canRetrieveWindowContent=false. It does not request key filtering, gestures, screen capture or text retrieval. The system may start the service in the background when enabled.

Users can disable it at any time in Android Accessibility settings. No WRITE_SECURE_SETTINGS permission is requested by the APK. TCL-specific auto-start selection is performed outside the app and is documented as an optional firmware-specific operation.

## Application visibility and storage

Casa queries activities that advertise Android TV or standard launcher categories to populate its app list and retrieves their local labels/icons. It does not request QUERY_ALL_PACKAGES or general storage management. Backups use app-specific external storage; copying a backup elsewhere is the user's choice.

The shell export endpoint is protected by android.permission.DUMP and exports Casa settings only. It does not accept caller-provided output paths or execute shell commands.

Opening another app or the original TV settings transfers control to that app; their permissions and data practices are separate from Casa's.

## Optional TCL picture controls (0.8+)

The image panel uses the installed TCL Settings Slice provider. Android asks whether Casa may display and act on Configuración fragments. Do not select access to any app. This is separate from Accessibility and does not grant screen capture or access to video frames. Casa requests this permission only after pressing its permission button.

The module reads the one validated adaptive-content row and sends its original TCL action only after an explicit image-control button press. It stores the preceding boolean and time locally for Undo. These device-specific values are excluded from configuration exports. No image states or picture data are uploaded. No extra service or automatic image switching is introduced. Permission survives app updates; uninstalling Casa removes its access.

## Game hub and web browser (0.9+)

The hub reads Android TV input states and game-controller input devices while visible; no Bluetooth discovery service or new permission is added. HDMI aliases, Casa-only opening times, controller detection times and up to eight web favorites/eight recent pages remain local, outside launcher backups. Clearing web lists does not clear WebView cookies or site storage.

The on-demand WebView connects to the chosen third-party sites and their resources. Those sites receive the IP and normal browser data and can store cookies/site data. JavaScript and DOM storage are enabled for games; there is no Android JavaScript bridge. File/content access, insecure mixed content, uploads, downloads, popups, camera, microphone and geolocation are disabled. Main-frame navigation requires HTTPS and certificate errors are cancelled. The browser pauses on leaving and is destroyed on exit; Android may retain cached WebView processes. No web page is loaded by Home or Ambient.
