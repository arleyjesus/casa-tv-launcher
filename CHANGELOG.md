# Changelog

## [0.2.0] — 2026-10-24

First public preview.

### Added
- Favorite ordering, title/clock visibility and optional focus animation.
- Local settings export/restore with schema validation and a pre-restore backup.
- Home-service status and accessibility settings shortcut.
- Spanish and English project documentation, compatibility report and MIT license.

### Improved
- Reuses wallpaper and bounded icon cache; editing favorites updates cards rather than rebuilding the background.
- Queries the initial app list off the UI thread and invalidates cached metadata on package changes.
- Clock updates at minute boundaries and stops while Home is hidden.
- Remembers the previous selection. Home dismisses the main panel.
- Panel button focus no longer scales against clipped edges.

### Verified on the reference TCL
- Allowing the vendor-specific APP_AUTO_START operation for Casa preserved its accessibility service across a reboot.
- CasaDream worked as a system screensaver, including activation after inactivity.
- Settings changes, export, favorite ordering and restore were exercised on-device.

### Known limits
- Only the TCL 50C645 / Android 11 / V652 reference configuration has been tested.
- Spanish-only application strings and fixed three-HDMI layout.
- Original TCL panels retain their design; not every vendor exposes every shortcut.
- Automatic updates are not implemented. Install official releases with the same signing certificate.
- No general TV speedup, ad blocking inside apps or universal debloat is claimed.

## [0.1.0] — 2026-10-24

Private prototype: native home screen, six favorites, application list, TCL HDMI/settings links, ambient preview and optional Home redirection. Not distributed as a public release.
