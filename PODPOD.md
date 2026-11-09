# Pod-Pod

Pod-Pod is a personal fork of the open-source [Pocket Casts Android app](https://github.com/Automattic/pocket-casts-android)
(MPL-2.0). It is for personal use and is not affiliated with or endorsed by Automattic / Pocket Casts.
All upstream licence files are unchanged. Files modified here are still covered by the MPL-2.0.

What it changes:

- **Its own app.** Package `com.obamacka.podpod` (debug builds `com.obamacka.podpod.debug`),
  launcher label "Pod-Pod" and an original launcher icon, so it installs next to the real Pocket Casts.
- **Plus themes, folders and bookmarks work on the device without an account.**

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

Every code change swaps one expression, `isSignedInAsPlusOrPatron` → `hasLocalPlusFeatures` or
`cachedSubscription.value != null` → `settings.isLocalPlusAvailable()`, and adds one import line.

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
    # Conflicts: keep upstream's version of the line, then re-apply the one-expression swap.
    # Check for NEW Plus gates for themes/folders/bookmarks that upstream may have added:
    git grep -n "isSignedInAsPlusOrPatron\|cachedSubscription.value != null\|subscription != null" -- modules app
    git push --force-with-lease origin pod-pod

If upstream renames `SignInState.isSignedInAsPlusOrPatron`, `Settings.cachedSubscription` or
`Settings.getFullySignedOut()`, only `PodPod.kt` and `PodPodSettings.kt` need updating.
