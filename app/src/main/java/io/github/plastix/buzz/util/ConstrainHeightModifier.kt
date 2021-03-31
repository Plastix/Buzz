package io.github.plastix.buzz.util

import androidx.annotation.FloatRange
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * Constraints the height of view based on a fraction of the max height. This operates like
 * [fillMaxHeight] but does not fill the height to max.
 */
fun Modifier.constrainHeight(@FloatRange(from = 0.0, to = 1.0) fraction: Float = 1f) =
    this.then(ConstrainHeightModifier(fraction))

private class ConstrainHeightModifier(
    private val scale: Float
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val wrappedConstraints = Constraints(
            minWidth = constraints.minWidth,
            minHeight = constraints.minHeight,
            maxWidth = constraints.maxWidth,
            maxHeight = (constraints.maxHeight * scale).roundToInt()
                .coerceIn(constraints.minHeight, constraints.maxHeight)
        )
        val placeable = measurable.measure(wrappedConstraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}