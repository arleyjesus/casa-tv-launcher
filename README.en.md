# Casa Aura TV

**A calmer home for your TV.** An independent Android TV / Google TV launcher with favorites, HDMI shortcuts and an ambient screensaver.

Developed and tested specifically on **TCL 50C645 / Android 11 / V652**. Previously called Casa TV Launcher; the repository, package and signing identity are retained for compatible updates. See the [C645 results journal](docs/RESULTADOS-C645.es.md) and [Guard protection guide](docs/TCL-C645-GUARD.es.md), currently in Spanish. The APK does not apply those TV policy changes automatically.

**0.4 pre-release:** a native card-based control center and compact centered dialogs, preserving Casa's existing artwork. Native TCL panels are unchanged; no TV-wide speedup is claimed.

[Download APK / releases](https://github.com/arleyjesus/casa-tv-launcher/releases) · [Español](README.md) · [Compatibility](docs/COMPATIBILITY.md)

![Casa running on a real TV](docs/images/home.png)

## Features

- An ad-free home interface, without Casa accounts or Internet permission.
- Up to six editable, reorderable favorites and an installed-app list.
- HDMI shortcuts and a panel linking to the TV's original settings.
- Static cinematic wallpaper, clock and optional focus animation.
- Local settings backups and restore.
- Ambient preview and an Android screensaver service, without a PIN.

Casa does **not** require Projectivy or root. Keep the original Google TV launcher enabled. It does not block advertising inside other apps or alter their interfaces.

## Early preview: v0.2.0

An additional **v0.3.0 pre-release** adjusts Home event handling and adds a regression test. It does not establish a general TV performance improvement or fix native TCL panel stutter. Version 0.2.0 remains the reference download.

Tested on **TCL 50C645, Android 11, firmware V8-R51MT05-LF1V652.021344**. The manifest supports API 26+, but other devices are unverified. HDMI/settings integration is TCL-specific and three HDMI buttons are currently fixed. The UI is currently Spanish-only.

Download the APK from [Releases](https://github.com/arleyjesus/casa-tv-launcher/releases), install and test it as a normal app first. Enable the optional **Casa · Botón Inicio** accessibility service only if you want Home redirection. See the [installation guide](docs/INSTALL.es.md) for ADB examples, TCL auto-start, screensaver configuration and rollback. No debloat list is required.

The accessibility service only receives window-state events for the Google TV and Projectivy home screens; it cannot read window contents or intercept keys. [Privacy](docs/PRIVACY.md).

## Build, report and share

[Build instructions](docs/BUILD.md) · [Contributing](CONTRIBUTING.md) · [Roadmap](ROADMAP.md) · [Changelog](CHANGELOG.md).

Report your device, firmware and reproduction steps in [Issues](https://github.com/arleyjesus/casa-tv-launcher/issues). Do not upload private TV dumps, accounts, IP addresses or credentials.

MIT licensed. Original artwork was created with AI assistance. Third-party names and icons remain their owners' property. No affiliation with TCL, Google, Projectivy or streaming services. The official release signing key is not included in this repository.
