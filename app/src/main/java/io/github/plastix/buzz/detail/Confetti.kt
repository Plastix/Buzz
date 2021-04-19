package io.github.plastix.buzz.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.plastix.buzz.util.radians
import io.github.plastix.buzz.util.randomFloat
import kotlin.math.cos
import kotlin.math.sin

data class Projectile(val origin: Offset, val angle: Float, val speed: Float, val color: Color) {
    val G = -60.8f;
    fun position(t: Float): Offset {
        val x = speed * t * cos(angle)
        val y = (speed * t * sin(angle)) - (0.5f * G * t * t)
        return origin + Offset(x, y)
    }
}

@Composable
fun ConfettiCanvas(trigger: Boolean) {
    if (!trigger) return;

    val animationDuration = 1000
    val confettiColors = remember { listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow) }

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = FastOutLinearInEasing),
        )
    )

    val t by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
        )
    )

    val xSpread = 100f
    val ySpread = 10f
    val angleSpread = 15f;

    val projectiles = remember {
        List(50) {
            Projectile(
                Offset(randomFloat(-xSpread, xSpread), randomFloat(-ySpread, ySpread)),
                randomFloat(270f - angleSpread, 270f + angleSpread).radians(),
                randomFloat(150f, 300f),
                confettiColors.random()
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        projectiles.forEach {
            val prevPos = center + it.position(t - 0.1f)
            val pos = center + it.position(t)
            drawLine(it.color.copy(alpha = alpha), prevPos, pos, strokeWidth = 8f)
        }
    }
}
