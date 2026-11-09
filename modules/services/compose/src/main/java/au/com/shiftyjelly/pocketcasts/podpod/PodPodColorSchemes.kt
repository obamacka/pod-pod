package au.com.shiftyjelly.pocketcasts.podpod

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import au.com.shiftyjelly.pocketcasts.compose.ThemeColors
import au.com.shiftyjelly.pocketcasts.compose.ThemeDarkColors
import au.com.shiftyjelly.pocketcasts.compose.ThemeLightColors
import au.com.shiftyjelly.pocketcasts.ui.theme.Theme

/**
 * Pod-Pod: colours for the "Pod-Pod Dynamic" themes.
 *
 * On Android 12+ the Material 3 colour scheme comes from the wallpaper ([dynamicLightColorScheme] /
 * [dynamicDarkColorScheme]). Below that a static TonalSpot scheme seeded from the Pod-Pod icon mint (#39D3A0)
 * is used. The views side uses the same roles through `podpod_m3_colors.xml` / `podpod_themes.xml`.
 */
val Theme.ThemeType.isPodPodDynamic: Boolean
    get() = this == Theme.ThemeType.PODPOD_DYNAMIC_LIGHT || this == Theme.ThemeType.PODPOD_DYNAMIC_DARK

@Composable
fun rememberPodPodDynamicColorScheme(dark: Boolean): ColorScheme {
    val context = LocalContext.current
    return remember(context, dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (dark) PodPodFallbackDarkColorScheme else PodPodFallbackLightColorScheme
        }
    }
}

@Composable
fun rememberPodPodDynamicThemeColors(dark: Boolean): ThemeColors {
    val colorScheme = rememberPodPodDynamicColorScheme(dark)
    return remember(colorScheme, dark) {
        colorScheme.toPodPodThemeColors(base = if (dark) ThemeDarkColors else ThemeLightColors)
    }
}

/**
 * Maps Material 3 roles onto the app's design tokens. Tokens that are not listed (podcast, player, filter, category,
 * gradient and most support colours) keep their Light / Dark values.
 * Keep in sync with `podpod_themes.xml`.
 */
fun ColorScheme.toPodPodThemeColors(base: ThemeColors): ThemeColors = base.copy(
    primaryUi01 = surface,
    primaryUi01Active = surfaceContainer,
    primaryUi02 = surfaceContainerLow,
    primaryUi02Selected = secondaryContainer,
    primaryUi02Active = surfaceContainer,
    primaryUi03 = surfaceContainer,
    primaryUi04 = surface,
    primaryUi05 = outlineVariant,
    primaryUi05Selected = outline,
    primaryUi06 = surfaceContainerHigh,
    primaryIcon01 = primary,
    primaryIcon01Active = inversePrimary,
    primaryIcon02 = onSurfaceVariant,
    primaryIcon02Selected = primary,
    primaryIcon02Active = onSurfaceVariant,
    primaryIcon03 = onSurfaceVariant,
    primaryIcon03Active = primary,
    primaryText01 = onSurface,
    primaryText02 = onSurfaceVariant,
    primaryText02Selected = primary,
    primaryField01 = surfaceContainerHighest,
    primaryField01Active = surfaceVariant,
    primaryField02 = surfaceContainerLow,
    primaryField02Active = surfaceContainerLowest,
    primaryField03 = outline,
    primaryField03Active = primary,
    primaryInteractive01 = primary,
    primaryInteractive01Hover = primary,
    primaryInteractive01Active = inversePrimary,
    primaryInteractive01Disabled = outlineVariant,
    primaryInteractive02 = onPrimary,
    primaryInteractive02Hover = surfaceContainerHigh,
    primaryInteractive02Active = secondaryContainer,
    primaryInteractive03 = outlineVariant,
    secondaryUi01 = surface,
    secondaryUi02 = outlineVariant,
    secondaryIcon01 = onSurfaceVariant,
    secondaryIcon02 = outline,
    secondaryText01 = onSurface,
    secondaryText02 = onSurfaceVariant,
    secondaryField01 = surfaceContainerHighest,
    secondaryField01Active = surfaceVariant,
    secondaryInteractive01 = primary,
    secondaryInteractive01Hover = primary,
    secondaryInteractive01Active = inversePrimary,
    support01 = primary,
    support03 = primary,
    support05 = error,
)

// TonalSpot scheme for seed #39D3A0 (material-color-utilities). Keep in sync with values/podpod_m3_colors.xml.
val PodPodFallbackLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF1D6B50),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA7F2D0),
    onPrimaryContainer = Color(0xFF00513A),
    inversePrimary = Color(0xFF8BD5B5),
    secondary = Color(0xFF4C6358),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9DA),
    onSecondaryContainer = Color(0xFF354B41),
    tertiary = Color(0xFF3E6374),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC1E8FC),
    onTertiaryContainer = Color(0xFF254B5B),
    background = Color(0xFFF5FBF5),
    onBackground = Color(0xFF171D1A),
    surface = Color(0xFFF5FBF5),
    onSurface = Color(0xFF171D1A),
    surfaceVariant = Color(0xFFDBE5DE),
    onSurfaceVariant = Color(0xFF404944),
    surfaceTint = Color(0xFF1D6B50),
    inverseSurface = Color(0xFF2C322E),
    inverseOnSurface = Color(0xFFECF2ED),
    outline = Color(0xFF707973),
    outlineVariant = Color(0xFFBFC9C2),
    surfaceBright = Color(0xFFF5FBF5),
    surfaceContainer = Color(0xFFEAEFEA),
    surfaceContainerHigh = Color(0xFFE4EAE4),
    surfaceContainerHighest = Color(0xFFDEE4DF),
    surfaceContainerLow = Color(0xFFEFF5EF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFD6DBD6),
)

val PodPodFallbackDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF8BD5B5),
    onPrimary = Color(0xFF003827),
    primaryContainer = Color(0xFF00513A),
    onPrimaryContainer = Color(0xFFA7F2D0),
    inversePrimary = Color(0xFF1D6B50),
    secondary = Color(0xFFB3CCBE),
    onSecondary = Color(0xFF1F352B),
    secondaryContainer = Color(0xFF354B41),
    onSecondaryContainer = Color(0xFFCFE9DA),
    tertiary = Color(0xFFA6CCDF),
    onTertiary = Color(0xFF093544),
    tertiaryContainer = Color(0xFF254B5B),
    onTertiaryContainer = Color(0xFFC1E8FC),
    background = Color(0xFF0F1512),
    onBackground = Color(0xFFDEE4DF),
    surface = Color(0xFF0F1512),
    onSurface = Color(0xFFDEE4DF),
    surfaceVariant = Color(0xFF404944),
    onSurfaceVariant = Color(0xFFBFC9C2),
    surfaceTint = Color(0xFF8BD5B5),
    inverseSurface = Color(0xFFDEE4DF),
    inverseOnSurface = Color(0xFF2C322E),
    outline = Color(0xFF89938D),
    outlineVariant = Color(0xFF404944),
    surfaceBright = Color(0xFF353B37),
    surfaceContainer = Color(0xFF1B211E),
    surfaceContainerHigh = Color(0xFF252B28),
    surfaceContainerHighest = Color(0xFF303633),
    surfaceContainerLow = Color(0xFF171D1A),
    surfaceContainerLowest = Color(0xFF0A0F0D),
    surfaceDim = Color(0xFF0F1512),
)
