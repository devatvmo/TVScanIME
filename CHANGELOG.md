# Changelog

All notable changes to TVScanIME are documented in this file.

This changelog follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
