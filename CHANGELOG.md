# Changelog

All notable changes to TVScanIME are documented in this file.

This changelog follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Refined the IME lifecycle and remote-key handling after reviewing TVRemoteIME's cross-screen input architecture.
- Moved LAN HTTP/WebSocket startup off the InputMethodService lifecycle thread.

### Fixed
- TVScanIME no longer consumes physical TV OK/Enter or D-pad events when it has no on-screen TV key grid; these keys are passed to the focused app/framework.
- Corrected escaped newline characters in the Android manifest feature declarations.

### Compatibility note
- TVRemoteIME is GPL-2.0. Its source code, web assets, and UI were not copied; TVScanIME retains its independently implemented Kotlin/HTML interface and its own protocol.

## [1.3.0-dev] - 2026-11-19

### Changed
- Reworked remote-key handling and moved LAN server startup to a background thread.



### Added
- Air-mouse hover feedback for launcher actions.
- Explicit support guidance for both D-pad remotes and pointer-based air mice.

### Changed
- TV launcher uses a landscape two-column layout on wide screens, with a compact vertical fallback.
- D-pad focus feedback, OK/Enter activation, IME confirmation, and Back dismissal are documented.

### Fixed
- TV remote OK/Enter now executes the focused editor action (Search/Go/Next/Done/Send) and falls back to forwarding Enter for plain text fields.
- Prevented a held OK/Enter key from submitting repeatedly.
- Preserved the standard InputMethodService Back-key dismissal behavior.
- Declared touchscreen hardware optional for TV devices.

### Validation
- Added device checks for OK/Enter on action-based and plain text fields.
- GitHub Actions compilation is tracked per commit; physical Android TV verification remains pending until completed and recorded in `docs/TEST_PLAN.md`.

## Release history

No stable release has been published yet. The current app build is a development version: `1.2.1-dev` (version code `4`).
