# Changelog

## [0.8.0] — 2026-10-25

- Added an optional TCL adaptive-content panel with native Android permission flow.
- Reads current state, saves an undo value before changing and verifies the result; never retries a toggle automatically.
- Accepts only the validated enabled TCL row and a single trusted toggle action.
- Keeps original TV settings available and disables image changes when state cannot be verified.
- No vendor SDK, credentials, root, secure-settings grant or background image adjustment.
- Fixed About text to describe the manual network access introduced in 0.7.


## [0.7.0] — 2026-10-25

- Added a manually downloaded GitHub ambient gallery, disabled by default.
- Validates trusted HTTPS URLs, file sizes, SHA-256, PNG type and decoded dimensions before replacing a pack.
- Keeps the previous complete pack on failed/cancelled downloads; stages at most one replacement.
- Added downloaded/built-in selection and download removal. Ambient playback performs no network requests.
- Added INTERNET permission for this feature, blocked cleartext traffic and updated privacy documentation.
- Retained the three presets, integrated artwork, favorites and limited Home service.


## [0.6.0] — 2026-10-25

- Selectable Classic, Modern and Immersive home layouts with preserved favorites.
- Selection-driven miniature background blur and contextual app title in Immersive.
- Three bundled ambient landscapes, timed rotation, cut/fade, optional zoom and time-based first image.
- Shared preview/screensaver lifecycle; off-thread decoding and stopped work on exit.
- Optional design/ambient fields in schema-1 backups, retaining legacy backup support.
- Kept offline permissions and existing Home service scope. Remote gallery/video remain planned.


## [0.5.0] — 2026-10-25

- Four larger home cards with lighter outlines; grouped inputs, gaming, settings and library controls.
- Recycled six-column app library, created on demand and opened with Down. Back/Up returns to the prior home selection.
- Background app discovery, 24 cached icon thumbnails capped at 192 pixels, and direct opening without panel opacity or position animation.
- Gaming panel with HDMI 1, input selection and the original TCL game-panel entry point. No automatic picture-mode changes.
- Kept the six stored favorites and backup format; only the first four appear on Home.
- Added the laptop/HDMI guide and recorded device validation. Physical PC video and latency remain untested.

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
