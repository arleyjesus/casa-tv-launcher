# Changelog

## [0.4.0] — pre-release, 2026-10-25

- Renamed the visible application to Casa Aura TV; retained package, certificate and preferences.
- Replaced the long settings menu with a nine-card control center, explicit D-pad focus links and original vector-style icons drawn natively.
- Centered secondary dialogs and sized them to content, with a capped scroll area.
- Grouped connections and Home recovery; only show the optional Projectivy shortcut when installed.
- Preserved Casa's wallpaper and warm palette; no live blur, network permission, additional artwork or background service.
- Published the C645/Guard procedure, read-only inspector and device results journal, including measurement limits and unresolved native panel stutter.

## [0.3.0] — pre-release, 2026-10-25

- Home accessibility event timeout reduced from 200 ms to zero.
- Duplicate-event suppression reduced from 1,200 ms to 150 ms, with a regression test for a subsequent Home action.
- Redirect requests avoid adding an activity transition animation.
- Added local timing logs for event delivery and focus acquisition; no screen contents are logged.
- No additional permissions, package disabling or native TV settings changes.
- On-device installation and service binding passed. A first measured redirect took 804 ms after event receipt; a faster end-to-end Home return has **not** been established.
- Native TCL settings still showed slow frames. This candidate does not fix proprietary panel rendering or claim a general system speedup.
- A later device-specific Guard memory exception survived reboot, with Home returning from YouTube and after suspend/resume. That TV policy change is separate from the APK and is not automatically applied by Casa. It does not establish faster native panel rendering or universal device compatibility.
- Further performance validation is required before calling this stable.

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
