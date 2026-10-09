# Device verification

The app was installed and exercised in an Android TV emulator. Emulator behavior
does not establish vendor screensaver selection, physical display composition,
4K output, or sustained thermal performance.

| Device class | Model and OS/API | Resolution | Result |
|---|---|---|---|
| Android TV emulator | Google Android TV emulator, Android 12 / API 31 | 1920 x 1080 | App launch, animation, settings, and provider checks passed. DreamService system selection is not established by this check. |
| Fire TV physical device | Not tested; a device was unavailable | Not measured | Looking for a Fire TV owner to test installation, app preview, DreamService selection and idle activation, remote settings, exit behavior, and report results through [Issues](https://github.com/jeremykenedy/nebula-drift/issues). Please include the device model, Fire OS version and Android API, display resolution, exact behavior tested, and results. |
| Android TV physical device | Not tested; a device was unavailable | Not measured | Looking for an Android TV owner to test installation, app preview, DreamService selection and idle activation, remote settings, exit behavior, and report results through [Issues](https://github.com/jeremykenedy/nebula-drift/issues). Please include the device model, Android/Google TV OS version and API, display resolution, exact behavior tested, and results. |
| Google TV physical device | Not tested; a device was unavailable | Not measured | Looking for a Google TV owner to test installation, app preview, DreamService selection and idle activation, remote settings, exit behavior, and report results through [Issues](https://github.com/jeremykenedy/nebula-drift/issues). Please include the device model, Google TV OS version and Android API, display resolution, exact behavior tested, and results. |
| Native 4K display composition | Not tested; no physical 4K TV was available | 3840 x 2160 not verified | Looking for a Fire TV, Android TV, or Google TV owner with a physical 4K display to test scene rendering, visible output resolution, frame pacing, and sustained operation, then report the model, OS/API, resolution, behavior, and results through [Issues](https://github.com/jeremykenedy/nebula-drift/issues). |

## Emulator checks

Record runtime previews and screenshots only from a running build. Wait at least
40 seconds after app launch before capture so shader initialization and scene
motion have settled. `nebula-violet.png` is an unmodified ADB screenshot from
the Google Android TV API 31 emulator at 1920 x 1080, taken after the scene ran
for at least 40 seconds. `settings.png` is an ADB capture of the settings screen
on that same emulator. Neither image is a physical-device capture.

Remaining device work includes D-pad confirmation on multiple remotes, automatic
idle activation, returning from the dream, long-duration frame pacing and heat,
and measured output resolution on a physical 4K set. Please share those results
in an issue so the matrix can be updated with the exact model and software.
