# Build and signing

## Requirements

- Windows with PowerShell 5.1 or PowerShell 7.
- JDK 21, with JAVA_HOME set to its root.
- Official Android SDK: build-tools 36.0.0 and an Android platform API 30 or newer. The published preview was compiled locally against android-37.0.
- Set ANDROID_HOME (or ANDROID_SDK_ROOT) to the SDK root.

The current lightweight build uses aapt2, javac, d8, jar and zipalign, without external application dependencies or Gradle. Install the tools through Android Studio / the official SDK manager. Review any vendor license yourself; the scripts do not accept SDK licenses automatically.

## Compile without signing

```powershell
./scripts/build.ps1
./scripts/verify-apk.ps1 -ApkPath ./build/Casa-TV-Launcher-v0.2.0-unsigned.apk
```

You can pass `-AndroidSdk`, `-JavaHome`, `-AndroidPlatform` and `-BuildToolsVersion` explicitly. The default platform is the newest installed numeric SDK platform; the manifest still controls minSdk/targetSdk. No device is contacted by the build script.

An unsigned APK is a build-validation artifact, not an installable release.

## Sign your own build

Keep your keystore outside the repository. Set CASA_KEYSTORE, CASA_KEY_ALIAS, CASA_STORE_PASSWORD and CASA_KEY_PASSWORD in your local process environment, then run:

```powershell
./scripts/build.ps1 -Sign
```

The signing command reads passwords from environment variables rather than embedding them in this repository or its command arguments. Do not post them in an issue or commit them to Git. Never share the official release key.

A build signed with another key cannot update an installed official build. For a fork, use your own application ID and signing identity. Uninstalling a previous build deletes its app data and app-specific backups; export/copy what you need first.

## Official preview identity

- Package: `tv.casa.launcher`
- Version: `0.2.0`, versionCode `2`
- Signing certificate SHA-256: `f9c91bf6e4098273d0d86bb58f28cfa503c0a145b7704dae6aed8804700d2032`

The repository does not contain the private key, its password, user backups or device logs.

## CI and releases

GitHub Actions compiles with the SDK already installed on its Windows runner and runs APK contract checks. Runner SDK availability may change; if the required tools are missing, install them explicitly after reviewing the official licensing/setup process.

CI does not receive signing secrets and does not automatically publish a signed APK. Official release signing remains a separate maintainer operation. A future release process can improve reproducibility without putting private keys in the repository.

See [release checklist](RELEASING.md).
