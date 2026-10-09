# Releasing

Keep package identity and the signing key stable for updates. Before release:

1. Run `./test.sh` and all local quality checks.
2. Run `./build.sh` with the protected release key.
3. Verify package `com.jeremykenedy.nebuladrift`, version, DreamService
   metadata, permissions, and signing certificate with Android SDK tools.
4. Verify the APK on available Android TV hardware or emulator and update the
   device matrix with measured results.
5. Create a SemVer tag and attach `build/nebula-drift.apk` and
   `build/nebula-drift.apk.sha256` to the GitHub release.
6. Download both published assets and verify the SHA-256 checksum.

Release notes describe additions, fixes, device changes, commands, and the
upgrade path. Releases must not claim platform behavior beyond recorded tests.
