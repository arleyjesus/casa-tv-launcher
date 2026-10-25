# Maintainer release checklist

Work locally until a version's changes and focused checks are complete. Publish the finished commit, matching version tag and Release together; do not upload intermediate UI experiments. Write short release notes describing what changed, what was tested and any remaining limits. Keep existing release assets unchanged.

1. Update manifest versionName and monotonically increase versionCode.
2. Update CHANGELOG, compatibility evidence and release notes. Mark experimental versions as pre-releases.
3. Compile from a clean checkout using the documented SDK/JDK and run scripts/verify-apk.ps1.
4. Test installation, favorite persistence, Home, menus, suspend/resume and screensaver on a documented device. Test updates from the previous official APK.
5. Sign locally with the existing private release key. Do not add keys/passwords to Git, issue attachments or a public CI artifact.
6. Verify the certificate fingerprint against docs/BUILD.md. Record the exact APK SHA-256 in SHA256SUMS.txt.
7. Review tracked files and screenshots for private data. Include no personal backups or device logs.
8. Create a version tag (for example v0.2.0) at the tested source commit.
9. Publish a GitHub Release for that tag with the signed APK, SHA256SUMS.txt and clear notes. GitHub provides source archives from the tag.
10. Verify the release is publicly readable and its downloaded APK matches the checksum. Update the next-version roadmap without changing already published release assets silently.

If a signing key is lost, a different key cannot update existing installs under the same package. Communicate a migration plan rather than replacing a published APK with an incompatible signature.
