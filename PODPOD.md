# Pod-Pod

Pod-Pod is a personal fork of the open-source [Pocket Casts Android app](https://github.com/Automattic/pocket-casts-android)
(MPL-2.0). It is for personal use and is not affiliated with or endorsed by Automattic / Pocket Casts.
All upstream licence files are unchanged. Files modified here are still covered by the MPL-2.0.

What it changes:

- **Its own app.** Package `com.obamacka.podpod` (debug builds `com.obamacka.podpod.debug`),
  launcher label "Pod-Pod" and an original launcher icon, so it installs next to the real Pocket Casts.
- **Plus themes, folders and bookmarks work on the device without an account.**
- **Material 3 Expressive look.** A "Pod-Pod Dynamic" theme (light and dark) takes its colours from the
  wallpaper (Material You) on Android 12+ and is the default. Every theme also gets a Material 3 Expressive
  Compose theme, rounder shapes, an expressive navigation bar (Pod-Pod Dynamic only), a rounder mini player
  and a shape-morphing play button. See "Material 3 Expressive" below.

## Local only, no account

Pod-Pod is meant to be used **signed out**. While no Pocket Casts account is signed in, it unlocks three
Plus features that run entirely on the device: Plus themes, folders and bookmarks.

- No subscription is faked. `SignInState`, the cached subscription, purchase code and all
  server/sync calls are unchanged. The unlock only changes the UI gates for those three features.
- When you are signed out, sync does not run (`DataSyncProcess.sync()` returns early), so folders and
  bookmarks stay in the local database.
- If you sign in to a Pocket Casts account, upstream rules apply again. The features lock unless that
  account really has Plus. Local folders and bookmarks are marked "not synced", so a later sign-in
  may upload them like any other local change. Stay signed out if you want everything to stay local.
- Everything else keeps upstream gating: cloud files, web/desktop, partner offers, generated
  transcripts and chapters, episode chat, alternative app icons and Patron features.
- The debug Firebase config is a placeholder project (see below). The in-app analytics and
  crash-report switches under Settings > Privacy still work as in upstream. Turn them off if you want.
- One switch turns it all off: set `PodPod.unlockLocalPlusFeatures = false` in
  `modules/services/model/.../podpod/PodPod.kt`.

## Upstream files touched

New files (rebases never conflict on these):

| File | Why |
| --- | --- |
| `PODPOD.md` | This document |
| `.github/workflows/podpod-build.yml` | CI: builds `:app:assembleDebugProd`, uploads the APK, releases on `podpod-v*` tags |
| `app/src/main/res/drawable/ic_launcher_podpod_{background,foreground,monochrome}.xml` | Launcher icon layers. The monochrome layer is for Android 13+ themed icons |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher_podpod.xml` | Adaptive icon |
| `app/src/main/res/mipmap-anydpi/ic_launcher_podpod.xml` | Plain vector icon for API 24-25 |
| `modules/services/model/src/main/java/au/com/shiftyjelly/pocketcasts/podpod/PodPod.kt` | The switch, plus `SignInState.hasLocalPlusFeatures` |
| `modules/services/preferences/src/main/java/au/com/shiftyjelly/pocketcasts/podpod/PodPodSettings.kt` | `Settings.isLocalPlusAvailable()` (signed out = `Settings.getFullySignedOut()`) |
| `modules/services/ui/src/main/res/values/podpod_themes.xml` | `PodPodDynamicLight/Dark` view themes (token → M3 role mapping, shapes, expressive navigation bar style) |
| `modules/services/ui/src/main/res/values{,-v31,-v34}/podpod_m3_colors.xml` | M3 colour roles: static fallback (seed `#39D3A0`), API 31 tonal palettes, API 34 system roles |
| `modules/services/ui/src/main/res/values/podpod_strings.xml` | Theme names (not translated) |
| `modules/services/ui/src/main/res/drawable/ic_theme_podpod_dynamic_{light,dark}.xml` | Theme picker previews (drawn with the live M3 roles) |
| `modules/services/compose/src/main/java/au/com/shiftyjelly/pocketcasts/podpod/PodPodColorSchemes.kt` | Dynamic `ColorScheme` (+ fallback) and its mapping onto `ThemeColors` |
| `modules/services/compose/src/main/java/au/com/shiftyjelly/pocketcasts/podpod/PodPodExpressiveTheme.kt` | `MaterialExpressiveTheme` wrapper, M3 scheme for the other themes, M2 shapes, pill/sheet radii |
| `modules/services/compose/src/main/java/au/com/shiftyjelly/pocketcasts/podpod/PodPodExpressiveShapes.kt` | Morphing play/pause button shape |
| `app/src/main/res/values/podpod_overrides.xml` | Overrides library dimens by name: bottom sheet 28dp corners, mini player 20dp corners / 8dp margin / 8dp artwork corners |

Modified upstream files:

| File | Change |
| --- | --- |
| `app/build.gradle.kts` | `applicationId = "com.obamacka.podpod"` (phone app only; wear/automotive/tv keep upstream's id). `appIcon` placeholder → `@mipmap/ic_launcher_podpod` for every build type |
| `app/src/main/res/values/titles.xml`, `app/src/debugProd/res/values/titles.xml`, `app/src/debug/res/values/titles.xml` | `app_name` → "Pod-Pod" ("Pod-Pod Debug" for the staging `debug` build) |
| `app/src/debug{,Prod}/res/xml/shortcuts.xml` | Static launcher shortcuts target `com.obamacka.podpod.debug` |
| `app/src/debug{,Prod}/google-services.json` | Placeholder Firebase project for `com.obamacka.podpod.debug`. The google-services plugin fails the build if no client matches the package name, and this way the app does not report to Pocket Casts' Firebase project. Remote config falls back to in-app defaults |
| `modules/features/settings/.../AppearanceSettingsFragment.kt` | Themes: lock badge, click gate and upgrade banner use `hasLocalPlusFeatures` |
| `modules/features/podcasts/.../viewmodel/PodcastsViewModel.kt` | Folders: show folders in the podcast grid and the create-folder menu |
| `modules/features/podcasts/.../view/podcast/PodcastFragment.kt` | Folders: "add to folder" button. Bookmarks: bookmarks tab |
| `modules/features/podcasts/.../view/podcast/PodcastAdapter.kt` | Folders: folder icon in the podcast header |
| `modules/features/podcasts/.../view/folders/SuggestedFoldersFragment.kt`, `SuggestedFoldersViewModel.kt` | Folders: apply suggested folders or create a custom folder |
| `modules/services/repositories/.../playback/BrowseTreeProvider.kt` | Folders in Android Auto / media browser |
| `modules/features/player/.../viewmodel/BookmarksViewModel.kt` | Bookmarks: list instead of upsell (player and episode screens, bookmarks container) |
| `modules/features/player/.../viewmodel/ShelfSharedViewModel.kt` | Bookmarks: "Add bookmark" shelf action |
| `modules/features/settings/.../HeadphoneControlsSettingsPageViewModel.kt` | Bookmarks: "Add bookmark" headphone action |
| `modules/services/repositories/.../bookmark/BookmarkHelper.kt` | Bookmarks: headphone, notification and Auto "add bookmark" handling |
| `gradle/libs.versions.toml` | `compose-material3` pinned to `1.5.0-rc01` (BOM has 1.4.0, where the M3 Expressive APIs are internal) |
| `modules/services/compose/build.gradle.kts` | `implementation(libs.compose.material3)` |
| `modules/services/preferences/.../model/ThemeSetting.kt` | `PODPOD_DYNAMIC_LIGHT/DARK` entries (ids `podpodDynamicLight/Dark`; theme settings are not synced) |
| `modules/services/preferences/.../SettingsImpl.kt` | Default theme / preferred dark / preferred light → Pod-Pod Dynamic |
| `modules/services/ui/.../ui/theme/Theme.kt` | `ThemeType.PODPOD_DYNAMIC_LIGHT/DARK` + `fromThemeSetting` |
| `modules/services/ui/.../ui/theme/ThemeColor.kt` (generated) | Every `when`: `LIGHT ->` → `LIGHT, PODPOD_DYNAMIC_LIGHT ->`, same for `DARK` (sed below) |
| `modules/services/compose/.../compose/Theme.kt` | `themeTypeToColors` branches. `AppTheme` wraps M2 `MaterialTheme` in `PodPodExpressiveTheme` and passes M2 shapes |
| `modules/services/compose/.../compose/components/SparkleImage.kt`, `modules/features/profile/.../winback/WinbackOfferPage.kt` | Exhaustive `when` lists the new theme types |
| `modules/services/compose/.../compose/buttons/RowButton.kt`, `BaseRowButton.kt` | Default `cornerRadius` → `PodPodRowButtonCornerRadius` (pill) |
| `modules/services/compose/.../compose/bottomsheet/ModalBottomSheet.kt` | Sheet corners → `PodPodSheetCornerRadius` (28dp) |
| `modules/services/compose/.../compose/components/AnimatedPlayPauseButton.kt` | Optional `shape` parameter (default `CircleShape`) |
| `modules/features/player/.../nowplaying/PlayerControls.kt` | Passes `animatePodPodPlayButtonShape(...)` to the play/pause button |

The Plus unlock changes swap one expression each, `isSignedInAsPlusOrPatron` → `hasLocalPlusFeatures` or
`cachedSubscription.value != null` → `settings.isLocalPlusAvailable()`, and add one import line.

## Material 3 Expressive

- **Pod-Pod Dynamic Light / Dark** are extra entries in Settings > Appearance > Theme. With "use system theme"
  (on by default on Android 10+) the app switches between them. The views read the theme attrs from
  `PodPodDynamicLight/Dark` (`podpod_themes.xml`), which map the app's design tokens onto Material 3 roles:
  surfaces → `surface`/`surfaceContainer*`, text and icons → `onSurface`/`onSurfaceVariant`, accents → `primary`,
  selection → `secondaryContainer`, dividers → `outline(Variant)`, errors → `error`. Podcast, player, filter,
  category and gradient colours keep the Light/Dark values. Compose uses the same mapping
  (`toPodPodThemeColors`) on `dynamicLight/DarkColorScheme`. Below Android 12 both use a static scheme seeded from
  the icon mint. On Android 12-13 the views use the nearest shipped wallpaper tone (for example tone 99 instead
  of 98), so they can differ a little from Compose. On 14+ they match exactly.
- Kotlin colour lookups in `ThemeColor.kt` (nav bar background below API 35, player and podcast tints) use the
  Light/Dark values for the dynamic themes.
- `AppTheme` provides `MaterialExpressiveTheme` (expressive motion, the M3 expressive shape scale and typography)
  for any material3 component, for every app theme. Existing screens are Material 2. They pick up the new
  colours, the M2 shapes (8/16/28dp), pill row buttons and 28dp sheets.


## Building

    ./gradlew :app:assembleDebugProd     # app/build/outputs/apk/debugProd/app-debugProd.apk

`debugProd` uses the production servers. It is signed with the committed `debug.keystore`, so every
build, local or CI, can update the previous one. No secrets are needed. Without
`~/.configure/pocket-casts-android/secrets/secret.properties`, every secret falls back to an empty string
(no Sentry, no Google Sign-In). `release` builds are not supported: they need upstream's upload key.

## Rebasing onto upstream (weekly)

    git remote add upstream https://github.com/Automattic/pocket-casts-android.git   # once
    git fetch upstream
    git checkout pod-pod
    git rebase upstream/main            # one commit per concern, so conflicts are easy to place
    # If ThemeColor.kt was regenerated upstream, re-apply the dynamic-theme branches (idempotent):
    sed -i -e 's/^\(\s*\)Theme\.ThemeType\.LIGHT ->$/\1Theme.ThemeType.LIGHT, Theme.ThemeType.PODPOD_DYNAMIC_LIGHT ->/' \
           -e 's/^\(\s*\)Theme\.ThemeType\.DARK ->$/\1Theme.ThemeType.DARK, Theme.ThemeType.PODPOD_DYNAMIC_DARK ->/' \
           modules/services/ui/src/main/java/au/com/shiftyjelly/pocketcasts/ui/theme/ThemeColor.kt
    # Once the Compose BOM ships material3 >= 1.5.0, drop the version from compose-material3 in libs.versions.toml.
    # Conflicts: keep upstream's version of the line, then re-apply the one-expression swap.
    # Check for NEW Plus gates for themes/folders/bookmarks that upstream may have added:
    git grep -n "isSignedInAsPlusOrPatron\|cachedSubscription.value != null\|subscription != null" -- modules app
    git push --force-with-lease origin pod-pod

If upstream renames `SignInState.isSignedInAsPlusOrPatron`, `Settings.cachedSubscription` or
`Settings.getFullySignedOut()`, only `PodPod.kt` and `PodPodSettings.kt` need updating.
