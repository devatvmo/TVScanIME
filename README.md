# TVScanIME — Android TV QR-code input method

TVScanIME is a prototype Android input method for Android TV and Android TV 14 set-top boxes. It lets a phone act as the keyboard through a browser, without installing a companion phone app.

## Features
- Registers as an Android system input method (IME).
- Displays a QR code when the TV input method view is shown.
- Opens a mobile browser page from the QR code; the phone uses its own system keyboard, including Chinese/English input and voice dictation.
- Synchronizes text to the TV over the local network.
- Provides backspace, clear, confirm, dismiss, and cursor movement actions.
- Uses a random pairing token in the QR URL and WebSocket messages.

## Project status
This repository contains the first MVP source implementation. **It has not yet been confirmed to compile successfully, and it has not been validated on a physical Android TV 14 device.** The GitHub Actions workflow attempts a debug build on pushes and pull requests. A successful workflow run is required before considering the APK build verified.

## Requirements
- JDK 17
- Android SDK Platform 35 and Build Tools 35.0.0
- Gradle 8.7
- Android Gradle Plugin 8.6.1

Open the repository root in Android Studio, allow Gradle sync, and choose **Build → Build APK(s)**. Or run:

```bash
gradle --no-daemon assembleDebug
```

Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`.

The repository intentionally does not include a Gradle Wrapper JAR or prebuilt APK. GitHub Actions installs Gradle 8.7 and uploads a successful debug build as the `TVScanIME-debug-apk` workflow artifact (14-day retention).

## Install and enable
```bash
adb install -r app-debug.apk
adb shell ime list -s
adb shell ime enable com.example.tvscanime/.TvImeService
adb shell ime set com.example.tvscanime/.TvImeService
```
If using wireless debugging, use the IP and port displayed by the TV's Wireless debugging screen. Do not assume the port is always `5555`.

## Use
1. Connect the TV and phone to the same Wi-Fi network.
2. Enable and select TVScanIME in the TV's input method settings.
3. Focus a text field in an app on the TV; the input method panel should display a QR code.
4. Scan the QR code with the phone camera and open the page in a browser.
5. Type using the phone's normal keyboard. Text is synchronized to the TV; use **Send to TV** to finish composing.

## Network and security
- HTTP page: TCP port `8766`.
- WebSocket: TCP port `8765`.
- Traffic currently uses plain HTTP/WebSocket on the local network. The random pairing token reduces accidental connections but is **not** encryption or end-to-end security.
- Use only on a trusted private network. Do not forward these ports to the public internet.
- Some TV firmware may report an unusable LAN IP such as `0.0.0.0`; device-specific network adaptation may be needed.

## Known limitations
- Vendor firmware can customize IME panel sizing, lifecycle, network APIs, and background execution.
- The current code is a prototype; compatibility and input behavior must be tested on the target box.
- The displayed six-digit/manual pairing feature is not implemented; pairing is performed by scanning the QR code.

## Documentation
- [Project structure](docs/PROJECT_STRUCTURE.md)
- [Local network protocol](docs/NETWORK_PROTOCOL.md)
- [Device validation checklist](docs/TEST_PLAN.md)
