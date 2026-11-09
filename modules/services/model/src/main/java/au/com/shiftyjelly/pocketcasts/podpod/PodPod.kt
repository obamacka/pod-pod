package au.com.shiftyjelly.pocketcasts.podpod

import au.com.shiftyjelly.pocketcasts.models.type.SignInState

/**
 * Pod-Pod fork switches. See PODPOD.md in the repository root.
 *
 * Pod-Pod unlocks a small set of Plus features that work entirely on the device
 * (Plus themes, folders and bookmarks) for people who use the app WITHOUT a
 * Pocket Casts account. Nothing here fakes a subscription towards Pocket Casts
 * servers: the unlock only applies while signed out, when no sync runs, and
 * every other Plus/Patron feature (cloud files, web/desktop, partner offers,
 * generated transcripts, ...) keeps its upstream gating.
 */
object PodPod {
    /** Master switch for the local-only unlock. Set to false to get upstream behaviour back. */
    val unlockLocalPlusFeatures: Boolean = true

    /** True when the local-only Plus features should be available for a user in this sign-in state. */
    fun isLocalPlusUnlocked(isSignedOut: Boolean): Boolean = unlockLocalPlusFeatures && isSignedOut
}

/**
 * Drop-in replacement for [SignInState.isSignedInAsPlusOrPatron] at the call sites that gate
 * Plus themes, folders and bookmarks. Upstream behaviour for signed-in users is unchanged.
 */
val SignInState.hasLocalPlusFeatures: Boolean
    get() = isSignedInAsPlusOrPatron || PodPod.isLocalPlusUnlocked(isSignedOut = isSignedOut)
