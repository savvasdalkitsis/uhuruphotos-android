# UhuruPhotos

Android client for [LibrePhotos](https://github.com/LibrePhotos/librephotos): Kotlin, Compose, Hilt, SQLDelight. Module layout: `feature-structure.md`.

## Rules

- Fix a bug test first: add a test that fails because of the bug, then fix it, and commit both together. The test keeps the bug from coming back.
- Keep every function you add or change at a CRAP score below 30, from JaCoCo unit-test coverage: complexity² × (1 − coverage)³ + complexity. Composables count too. The Compose compiler inflates their complexity, so split large ones and cover them with Robolectric Compose tests.
- Keep every file you add or change at 300 lines or fewer.
- Write no code comments; name things so the code reads on its own. New files still need the license header from `LICENSE-HEADER` (Konsist checks it).
- Commit atomically: one change per commit, each one building on its own. The subject is one line in the -ing form, like "Fixing toolbar insets", with no body, no trailers and no co-author or AI attribution.

## Commands

- `./gradlew testDebugUnitTest assembleDebug` passes before you push. It needs JDK 21.
