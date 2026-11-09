package au.com.shiftyjelly.pocketcasts.podpod

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import au.com.shiftyjelly.pocketcasts.compose.ThemeColors
import au.com.shiftyjelly.pocketcasts.ui.theme.Theme
import androidx.compose.material.Shapes as Material2Shapes

/**
 * Pod-Pod: a Material 3 Expressive theme for every app theme, provided around the app's Material 2 theme in
 * [au.com.shiftyjelly.pocketcasts.compose.AppTheme]. Any material3 component gets the colour scheme (wallpaper
 * colours for the Pod-Pod Dynamic themes, the theme's own tokens otherwise), the expressive shape scale and
 * typography (the material3 defaults) and the expressive spring motion scheme.
 *
 * It sits outside the Material 2 theme so that Material 2's ripple and text selection colours still win for
 * the existing Material 2 screens. material3 components set their own ripple.
 */
@Composable
fun PodPodExpressiveTheme(
    themeType: Theme.ThemeType,
    colors: ThemeColors,
    content: @Composable () -> Unit,
) {
    val isDark = themeType.darkTheme
    val colorScheme = if (themeType.isPodPodDynamic) {
        rememberPodPodDynamicColorScheme(dark = isDark)
    } else {
        remember(colors, isDark) { colors.toMaterial3ColorScheme(isDark = isDark) }
    }
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}

/** Material 3 roles for the app's non-dynamic themes, taken from their design tokens. */
fun ThemeColors.toMaterial3ColorScheme(isDark: Boolean): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primaryInteractive01,
        onPrimary = primaryInteractive02,
        primaryContainer = primaryUi02Selected,
        onPrimaryContainer = primaryText01,
        inversePrimary = primaryInteractive01Active,
        secondary = primaryInteractive01,
        onSecondary = primaryInteractive02,
        secondaryContainer = primaryUi02Selected,
        onSecondaryContainer = primaryText01,
        tertiary = primaryInteractive01,
        onTertiary = primaryInteractive02,
        tertiaryContainer = primaryUi02Selected,
        onTertiaryContainer = primaryText01,
        background = primaryUi04,
        onBackground = primaryText01,
        surface = primaryUi01,
        onSurface = primaryText01,
        surfaceVariant = primaryField01,
        onSurfaceVariant = primaryText02,
        surfaceTint = primaryInteractive01,
        error = support05,
        outline = primaryField03,
        outlineVariant = primaryUi05,
        surfaceBright = primaryUi01,
        surfaceDim = primaryUi04,
        surfaceContainerLowest = primaryUi02,
        surfaceContainerLow = primaryUi02,
        surfaceContainer = primaryUi03,
        surfaceContainerHigh = primaryUi06,
        surfaceContainerHighest = primaryField01,
    )
}

/**
 * Material 2 shapes moved toward the Material 3 Expressive scale: small = M3 small (8dp), medium = between card and
 * dialog (16dp), large = M3 extra large (28dp, sheets).
 */
val PodPodMaterial2Shapes = Material2Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(28.dp),
)

/** Full-width call-to-action buttons are pills (the corner size is clamped to half the height). */
val PodPodRowButtonCornerRadius: Dp = 100.dp

/** Bottom sheets use the M3 extra large corner. */
val PodPodSheetCornerRadius: Dp = 28.dp
