# Changelog

All notable changes to TVScanIME are documented in this file.

This changelog follows Keep a Changelog, and the project uses Semantic Versioning.

## [Unreleased]

No unreleased changes.

## [1.3.0-dev] - 2026-11-19

### Changed
- Reworked IME remote-key handling: TVScanIME no longer consumes physical OK/Enter or D-pad events when no TV-side key grid is present.
- Moved LAN HTTP/WebSocket startup off the InputMethodService lifecycle thread.
- Kept TVScanIME's independently designed QR-first TV panel and phone browser UI.

### Fixed
- Corrected escaped newline characters in Android manifest feature declarations.

### Documentation
- Added a source review of TVRemoteIME and recorded the features intentionally excluded from TVScanIME.

### Validation
- GitHub Actions compilation is tracked per commit. Physical Android TV 14 testing remains pending.

## [1.2.1-dev] - 2026-11-18

### Fixed
- Added an initial attempt to forward remote OK/Enter to editor actions. Physical device testing showed this behavior was not satisfactory; it was replaced in 1.3.0-dev with pass-through handling.

## Release history

No stable release has been published. The current app build is a development version: 1.3.0-dev (version code 5).
