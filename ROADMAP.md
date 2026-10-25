# Roadmap

These are planned priorities, not guarantees or a release schedule.

## Native stability — still under investigation

- Validate Home return under repeatable cold/warm and suspend/resume conditions.
- Keep the narrow Home event filter and test duplicate handling without suppressing subsequent Home actions.
- Separate launcher timing from native TCL settings/panel rendering; do not claim system improvements from shorter animations.
- Native panel stutter remains unresolved. Avoid blanket debloat, cache clearing and compilation of already precompiled vendor packages.

## Following versions — portability and accessibility

- Keep a versioned C645 results journal and distinguish measurements of Casa from Android/TCL panels.
- Extend Casa Aura's card-based control center while retaining the existing visual identity and low rendering cost.
- Validate the 0.4 redesign over longer sessions before promoting it from pre-release.
- Name HDMI inputs and remember the preferred gaming input, after physical-source testing.
- Consider a local library search and launcher-only recent apps; avoid network widgets or periodic background refresh.

- Extract device-specific behavior into adapters instead of assuming TCL packages and three HDMI ports.
- Detect available inputs; support custom names and gracefully handle devices without a tuner.
- Move strings into Android resources and add complete English/Spanish UI support.
- Test focus navigation, text scaling, TalkBack and all dialog/Home interactions.
- Add automated tests for backup validation, migration, favorites and package changes.
- Validate on additional Android/Google TV models with a documented test checklist.

## Distribution and maintenance

- Improve app icon and TV banner assets.
- Add release certificate fingerprints and a repeatable maintainer signing process.
- Add an update-information path that preserves the current offline privacy contract, or make any network access an explicit future design decision.
- Consider a standard Android Studio/Gradle project for easier community contributions.

## Later, if justified

- User-selected wallpapers and configurable card density.
- More robust screensaver configuration and user-controlled timing.
- Safer recovery UI for unavailable apps and disabled Home redirection.
- Longer performance tests: cold/warm launch, frame pacing, memory and suspend/resume.

## Outside the launcher

Universal debloat, root, firmware flashing, removal of the original Google launcher and replacement of proprietary picture/game processing are not project features. Device-specific tuning belongs in separately reviewed documentation and must remain reversible.
