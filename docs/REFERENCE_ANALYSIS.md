# TVRemoteIME source review

Review date: 2026-11-19

Reference repository: https://github.com/abai569/TVRemoteIME

## Scope reviewed

The review focused on the reference project's `IMEService`, `RemoteServer`, `InputRequestProcesser`, browser input page, and keyboard layout. The reference project contains a much larger remote-control suite (ADB, mouse/touchpad, application and file management, DLNA/video features). Those features are not part of TVScanIME's product scope and are intentionally excluded.

## Useful architectural observations

- The reference IME starts its HTTP server on a worker thread instead of doing potentially slow network initialization in `InputMethodService.onCreate()`. This reduces the risk of TV firmware timing out while enabling the IME.
- The phone browser communicates with a small set of explicit text/key endpoints. The IME forwards received text through the active `InputConnection`.
- Its IME only handles D-pad/OK keys when a TV-side keyboard control actually has focus. Otherwise, key events fall through to the normal framework/app handling path.
- The phone uses its own browser and system keyboard; no companion phone app is required.

## TVScanIME decisions

- Keep the TVScanIME name, Kotlin implementation, QR-first TV surface, and original phone browser design.
- Keep live text synchronization over the existing local WebSocket channel; the browser sends the complete current draft and the IME updates one composing region with `setComposingText`, avoiding per-keystroke append duplication.
- Move HTTP/WebSocket server binding off the IME lifecycle thread.
- Do not consume physical OK/Enter or D-pad events because TVScanIME has no on-screen TV key grid. The focused application must retain normal remote navigation and editor actions.
- Keep only cross-screen text input, clear/confirm, backspace, dismiss, and basic cursor movement. Do not import remote-control, ADB, mouse, file, app-management, DLNA, or video modules.

## License and source handling

The reference repository declares GNU General Public License version 2.0. TVScanIME uses it as an architectural study only: no reference Java source, HTML/CSS/JavaScript assets, layouts, images, or other code were copied into TVScanIME. The implementation and UI remain independently authored. If code from that GPL-2.0 project is ever incorporated in the future, licensing and corresponding-source obligations must be reviewed before distribution.

## Remaining validation

Compilation can verify source/build integrity, but it cannot establish correct behavior on the user's Android TV 14 firmware. Physical testing is still required for remote key pass-through, IME activation timing, Chinese/English input, composition editing, and phone reconnect behavior.
