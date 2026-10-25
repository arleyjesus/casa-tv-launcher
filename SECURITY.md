# Security

Casa is an early preview. Only the newest public preview is maintained; no response-time guarantee is offered.

Please use GitHub's **Report a vulnerability** / private advisory feature if available. Do not put credentials, signing material, a private TV dump or an exploit involving personal data in a public issue. If private reporting is unavailable, open a minimal issue asking for a private contact without disclosing the vulnerability.

The optional accessibility service has no window-content retrieval capability and filters to the Google TV and Projectivy packages. It does not grant itself permissions. The APK uses Internet for requested wallpaper downloads and game browsing; Home and Ambient do not load web pages. The game WebView has no native JavaScript bridge, file access or runtime media permissions. See docs/PRIVACY.md. Backups contain favorite package names and appearance preferences, so users should treat them as personal configuration.

The ADB export receiver is protected by android.permission.DUMP. It exports only Casa preferences into app-specific external storage. It does not accept arbitrary file paths or install/disable applications.

Do not publish the maintainer signing key. A checksum helps identify an exact download; Android's signing certificate is what controls whether an APK can update an existing installation.
