# Device validation checklist

The source has not yet been validated on a physical Android TV 14 device. Run these checks before treating a release as stable.

## Build
- [ ] Run `gradle --no-daemon assembleDebug` with JDK 17 and Android SDK 35, or use GitHub Actions.
- [ ] Confirm `app/build/outputs/apk/debug/app-debug.apk` is produced.
- [ ] Install the APK on the target TV box.

## TV layout and remote control
- [ ] Verify the launcher uses a two-column landscape layout on a 16:9 TV.
- [ ] Verify all primary actions are reachable with Up/Down and selectable with OK/Enter.
- [ ] Verify the focused action has a clear high-contrast outline/background.
- [ ] Verify the first useful action is focused when the launcher opens.
- [ ] Verify Back exits the launcher normally and does not trap the user.
- [ ] Verify the QR code and address stay inside the TV safe area without scrolling.
- [ ] Verify the IME panel is readable from a normal viewing distance and Back dismisses it.
- [ ] In a Search/Go/Next/Done/Send field, press remote OK/Enter and verify the focused app receives the event and performs its normal editor action.
- [ ] In a plain text field, press remote OK/Enter and verify the app receives the key normally.
- [ ] Verify Up/Down/Left/Right navigation continues to work in the app while TVScanIME is visible.

## Input method
- [ ] Confirm the IME appears in Android input method settings.
- [ ] Enable and select TVScanIME as the current input method.
- [ ] Focus text fields in multiple apps and confirm the IME panel appears.
- [ ] Confirm the QR code remains readable at normal TV viewing distance.

## LAN address and phone input
- [ ] Connect the TV and phone to the same Wi-Fi/LAN.
- [ ] Scan the QR code and confirm the phone browser opens the input page.
- [ ] Confirm connection status changes to connected.
- [ ] Test Chinese composition, English, punctuation, emoji, and voice dictation.
- [ ] Type several characters, edit in the middle, delete, then confirm; verify no duplicated or missing characters.
- [ ] Test live updates, backspace, clear, confirm, dismiss, and cursor movement.
- [ ] Test reconnect behavior after temporarily disabling phone Wi-Fi.

## Compatibility and security
- [ ] Test on the intended Android TV 14 firmware and another Android TV app.
- [ ] Check LAN IP discovery; some vendor ROMs may report `127.0.0.1` when no active IPv4 interface is found.
- [ ] Verify HTTP/WebSocket listeners start successfully without delaying IME activation.
- [ ] Check whether vendor power management stops the listeners.
- [ ] Keep ports 8765 and 8766 on a trusted private network only.

## Air-mouse validation
- [ ] Move the pointer over each launcher action and confirm a visible hover highlight appears.
- [ ] Click each action with the air mouse and confirm it activates exactly once.
- [ ] Move the pointer away and confirm hover styling clears unless the control still has D-pad focus.
- [ ] Confirm pointer use does not prevent D-pad navigation or OK/Enter activation.
