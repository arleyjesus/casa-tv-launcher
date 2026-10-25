# Compatibility and test evidence

## Confirmed reference configuration

| Item | Tested value |
| --- | --- |
| TV | TCL 50C645 |
| Android | 11 / API 30 |
| Firmware | V8-R51MT05-LF1V652.021344 |
| Platform reported by TV | G03_4K_GB / BeyondTV4 / Smart TV Pro |
| Casa | Casa Aura TV 0.5; earlier releases remain available |

The manifest minSdk is 26. This is an installation floor, not proof of compatibility with every Android 8+ TV. No Fire TV, other manufacturer, other C645 size, alternate region or newer Android build has been validated by the maintainer yet.

## Exercised

- Installation/update with the original signing key, remote navigation and application list.
- Opening YouTube from its favorite card.
- Favorite ordering, appearance changes, export and restore to the prior settings.
- Home dismissing the main panel and returning to Casa through the limited accessibility service.
- Restart with TCL APP_AUTO_START set to allow: accessibility remained enabled and Casa opened.
- HDMI 1 shortcut opened the expected original TCL input URI. The actual video/audio path with a physical source still requires user testing.
- General TCL settings and network settings access.
- CasaDream connected as a system screensaver. Automatic inactivity entry was tested with a temporary 30-second timeout, then the original timeout was restored.

## Known limits

Device-specific Guard persistence and the later panel/memory observations are recorded in the [C645 results journal](RESULTADOS-C645.es.md) and [Guard guide](TCL-C645-GUARD.es.md). Neither policy changes nor removal of apps are performed by Casa itself.

- Three HDMI entries in the input menu; automatic input enumeration and custom names are not implemented.
- Picture/sound controls open original TCL settings. Casa does not reskin their firmware panels.
- TV/game functions remain managed by original TCL components.
- Bluetooth shortcut and every physical remote button require more device testing.
- Screensaver selection and automatic behavior can differ by firmware.
- Home/temporary bypass behavior and accessibility-service recovery need longer testing and broader coverage.
- The app UI is currently Spanish-only.
- No automatic update checker; use Releases.

## Rendimiento

Small controlled remote-navigation samples on the reference TV:

| Sample | Frames | Janky frames | Median | P95 |
| --- | ---: | ---: | ---: | ---: |
| Private 0.1.0, settled | 175 | 0 | 5 ms | 6 ms |
| 0.2.0, soon after reboot | 175 | 5 | 5 ms | 8 ms |
| 0.2.0, settled repeat | 171 | 0 | 5 ms | 6 ms |

These samples are not a device benchmark and do not isolate all background activity. They show no sustained navigation regression in the repeat, but do not prove faster TV-wide performance, lower total RAM use or faster streaming. Independent firmware tuning performed on the reference TV is not part of this application and is not required for installation.

When reporting another device, distinguish observed success from untested features and include Casa, Android and firmware versions without private dumps.
