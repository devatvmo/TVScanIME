# Release process

TVScanIME uses Semantic Versioning (`MAJOR.MINOR.PATCH`) and a monotonically increasing Android `versionCode`.

## Version rules

- **MAJOR**: incompatible behavior or protocol changes.
- **MINOR**: backward-compatible features and meaningful UI/input improvements.
- **PATCH**: backward-compatible bug fixes and small refinements.
- Development builds use a suffix such as `1.2.0-dev`; stable builds omit the suffix (for example, `1.2.0`).
- Increase `versionCode` for every distributed APK, including test candidates. Never reuse a version code.

## Required records for every change

1. Use a descriptive commit subject; keep unrelated changes separate.
2. Add user-visible changes to the `[Unreleased]` section of `CHANGELOG.md` under Added, Changed, Fixed, or Removed.
3. Update `docs/TEST_PLAN.md` when validation steps or expected behavior change.
4. Keep README feature and limitation statements consistent with the code.
5. Record the exact commit SHA and GitHub Actions run for each candidate build.

## Stable release checklist

1. Complete the relevant checks in `docs/TEST_PLAN.md` on a physical Android TV device, including both D-pad and air-mouse operation.
2. Confirm the GitHub Actions build for the exact release commit succeeds.
3. Set `versionName` to the stable SemVer value and increment `versionCode`.
4. Move the completed entries from `[Unreleased]` to a dated version heading in `CHANGELOG.md`.
5. Create an annotated Git tag named `vMAJOR.MINOR.PATCH` on the exact release commit.
6. Publish a GitHub Release using that tag. Attach the tested APK and include a concise summary plus known limitations.
7. Retain the release APK in GitHub Releases. Actions artifacts are temporary CI outputs and are not the permanent release archive.

## Build and artifact policy

- GitHub Actions must build the debug APK for pushes and pull requests.
- CI artifacts are for testing only; record their workflow run URL and commit SHA.
- A build passing CI proves compilation, not real-device compatibility.
- Do not call a build a stable release until the physical-device checklist is complete.
