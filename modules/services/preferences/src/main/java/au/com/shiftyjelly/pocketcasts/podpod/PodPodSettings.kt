package au.com.shiftyjelly.pocketcasts.podpod

import au.com.shiftyjelly.pocketcasts.models.type.Subscription
import au.com.shiftyjelly.pocketcasts.preferences.Settings

/**
 * Drop-in replacement for `settings.cachedSubscription.value != null` at the call sites that gate
 * Pod-Pod's local-only Plus features (folders, bookmarks). Without an account
 * ([Settings.getFullySignedOut]) the features are unlocked locally; signed-in users keep the
 * upstream check. See [PodPod].
 */
fun Settings.isLocalPlusAvailable(subscription: Subscription? = cachedSubscription.value): Boolean {
    return subscription != null || PodPod.isLocalPlusUnlocked(isSignedOut = getFullySignedOut())
}
