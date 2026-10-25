# Privacy / Privacidad

Casa v0.2.0 has no INTERNET permission, account system, analytics SDK, ad SDK or network updater.

The app stores favorite package names, appearance options, previous focus and a short-lived Home-redirection exception on the TV. Local backups include favorites and appearance only. They are not uploaded by Casa. Android or the user's backup/file-transfer tools may handle files separately.

## Accessibility

Casa · Botón Inicio is optional. It receives TYPE_WINDOW_STATE_CHANGED events filtered to `com.google.android.apps.tv.launcherx` and `com.spocky.projengmenu`, then checks their home activity names. Its configuration sets canRetrieveWindowContent=false. It does not request key filtering, gestures, screen capture or text retrieval. The system may start the service in the background when enabled.

Users can disable it at any time in Android Accessibility settings. No WRITE_SECURE_SETTINGS permission is requested by the APK. TCL-specific auto-start selection is performed outside the app and is documented as an optional firmware-specific operation.

## Application visibility and storage

Casa queries activities that advertise Android TV or standard launcher categories to populate its app list and retrieves their local labels/icons. It does not request QUERY_ALL_PACKAGES or general storage management. Backups use app-specific external storage; copying a backup elsewhere is the user's choice.

The shell export endpoint is protected by android.permission.DUMP and exports Casa settings only. It does not accept caller-provided output paths or execute shell commands.

Opening another app or the original TV settings transfers control to that app; their permissions and data practices are separate from Casa's.
