package au.com.shiftyjelly.pocketcasts.podpod

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density

private const val CIRCLE_FRACTION = 0.5f
private const val ROUNDED_SQUARE_FRACTION = 0.3f

/**
 * Pod-Pod: Material 3 Expressive play/pause button shape. It is a circle while paused and morphs into a rounded
 * square while playing, using the theme's spatial spring (MotionScheme.expressive() under AppTheme).
 */
@Composable
fun animatePodPodPlayButtonShape(isPlaying: Boolean): Shape {
    val cornerFraction by animateFloatAsState(
        targetValue = if (isPlaying) ROUNDED_SQUARE_FRACTION else CIRCLE_FRACTION,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "podPodPlayButtonShape",
    )
    return RoundedCornerShape(FractionCornerSize(cornerFraction.coerceIn(0f, CIRCLE_FRACTION)))
}

/** A corner size as a fraction of the shape's smaller side. Unlike `CornerSize(percent: Int)` it animates smoothly. */
private data class FractionCornerSize(private val fraction: Float) : CornerSize {
    override fun toPx(shapeSize: Size, density: Density): Float = shapeSize.minDimension * fraction
}
