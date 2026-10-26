# Contributing

Bug reports, translations, device testing and small focused pull requests are welcome.

1. Read [compatibility](docs/COMPATIBILITY.md) and [build instructions](docs/BUILD.md).
2. Describe the concrete problem and intended behavior before a large change.
3. Keep fixes focused and explain how they were tested on a TV, emulator or build pipeline.
4. Do not add vendor-wide disable lists or network permissions without an explicit design discussion.
5. Never commit signing keys, credentials, local settings backups, private device dumps or accounts.
6. Respect remote-only navigation, accessibility and the ability to return to the original launcher.

Use the issue forms for device reports. Redact screenshots and logs. For suspected vulnerabilities, follow [SECURITY.md](SECURITY.md) rather than publishing sensitive details.

Contributions are provided under the project's MIT license. Third-party assets require a compatible license and attribution.

## Capturas por versión

Al preparar una release, guarda las capturas nuevas en docs/images con la versión en su nombre. No sobrescribas ni borres las anteriores. Añade la versión y sus imágenes a docs/history/releases.json y ejecuta scripts/update-visual-history.ps1. El historial enlaza las imágenes de la etiqueta correspondiente; el README muestra la versión actual. Incluye el enlace al historial en las notas de la release.
