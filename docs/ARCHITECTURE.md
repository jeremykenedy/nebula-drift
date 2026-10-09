# Architecture

`NebulaDreamService` owns the Android DreamService lifecycle. `NebulaSceneView`
hosts an OpenGL ES 2.0 surface and requests frames at roughly 30 per second only
while visible. `NebulaRenderer` draws the full scene in one fragment shader;
the cloud field, stars, twinkle, and meteor trails are procedural and bundled
with the APK. No media decoder, network client, image assets, background service,
or wake lock is used.

`NebulaOptions` validates visual values and resolves random selections for one
showing. `NebulaPreferences` persists those values. `SettingsActivity` exposes
them through remote-operable controls, while `NebulaSettingsProvider` provides
the stable host-application interface described in
[settings provider](SETTINGS_PROVIDER.md).

The app preview and DreamService share the same renderer. The drawing surface
uses the size supplied by Android; this project does not force a 4K buffer.
Physical device frame pacing, thermal behavior, and native 4K composition still
need measurements and are recorded in [verification](VERIFICATION.md).
