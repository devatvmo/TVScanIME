# Device validation checklist

The source has not yet been verified by a successful Android build or on a physical Android TV 14 device. Run these checks before treating a release as stable.

## Build
- [ ] Run `gradle --no-daemon assembleDebug` with JDK 17 and Android SDK 35, or use GitHub Actions.
- [ ] Confirm `app/build/outputs/apk/debug/app-debug.apk` is produced.
- [ ] Install the APK on the target TV box.

## Input method
- [ ] Confirm the IME appears in Android input method settings.
- [ ] Enable and select TVScanIME as the current input method.
- [ ] Focus text fields in multiple apps and confirm the IME panel appears.
- [ ] Confirm the QR code remains readable at normal TV viewing distance.

## Phone pairing and input
- [ ] Connect the TV and phone to the same Wi-Fi/LAN.
- [ ] Scan the QR code and confirm the phone browser opens the input page.
- [ ] Confirm connection status changes to connected.
- [ ] Test Chinese composition, English, punctuation, emoji, and voice dictation.
- [ ] Test live updates, backspace, clear, confirm, dismiss, and cursor movement.
- [ ] Test reconnect behavior after temporarily disabling phone Wi-Fi.

## Compatibility and security
- [ ] Test on the intended Android TV 14 firmware and another Android TV app.
- [ ] Check LAN IP discovery; some vendor ROMs may report `0.0.0.0`.
- [ ] Check whether vendor power management stops the listeners.
- [ ] Keep ports 8765 and 8766 on a trusted private network only.
