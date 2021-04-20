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
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.plastix.buzz.util.radians
import io.github.plastix.buzz.util.randomFloat
import io.github.plastix.buzz.util.randomInt
import io.github.plastix.buzz.util.remap
import kotlin.math.cos
import kotlin.math.sin

data class Projectile(val origin: Offset, val angle: Float, val speed: Float, val color: Color, val gravity: Float) {
    fun position(t: Float): Offset {
        val x = speed * t * cos(angle)
        val y = (speed * t * sin(angle)) - (0.5f * gravity * t * t)
        return origin + Offset(x, y)
    }
}

data class ConfettiState(
    val projectiles: List<Projectile>,
    val time: Float,
    val globalAlpha: Float,
    val emitter: Emitter,
    val particleInfo: ParticleInfo
)

data class Emitter(
    val width: Float,
    val height: Float,
    val angle: Float,
    val aperture: Float,
    val minSpeed: Float,
    val maxSpeed: Float
)

data class ParticleInfo(
    val colors: List<Color>,
    val width: Float,
    val lengthEpsilon: Float,
    val lifespan: Int,
    val gravity: Float = -60.8f
)

@Composable
fun rememberConfettiState(
    origin: Offset,
    emitter: Emitter,
    particleInfo: ParticleInfo,
    particleCount: Int = 50,
    delay: Int = 0
): ConfettiState {

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
//            animation = tween(particleInfo.lifespan, easing = FastOutLinearInEasing, delayMillis = delay),
            animation = keyframes {
                durationMillis = particleInfo.lifespan
                0.0f at 0
                0.0f at delay-1
                1.0f at delay
                0.0f at particleInfo.lifespan with FastOutLinearInEasing
            }

        )
    )

    val t by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(particleInfo.lifespan, easing = LinearEasing, delayMillis = delay),
        )
    )

    val projectiles = remember {
        List(particleCount) {
            Projectile(
                origin = origin + Offset(
                    randomFloat(-emitter.width, emitter.width),
                    randomFloat(-emitter.height, emitter.height)
                ),
                angle = randomFloat(
                    emitter.angle - emitter.aperture,
                    emitter.angle + emitter.aperture
                ).radians(),
                speed = randomFloat(emitter.minSpeed, emitter.maxSpeed),
                color = particleInfo.colors.random(),
                gravity = particleInfo.gravity
            )
        }
    }

    return ConfettiState(
        projectiles = projectiles,
        time = t,
        globalAlpha = alpha,
        particleInfo = particleInfo,
        emitter = emitter
    )
}


fun DrawScope.drawConfetti(state: ConfettiState) {
    state.projectiles.forEach {
        val prevPos = center + it.position(state.time - state.particleInfo.lengthEpsilon)
        val pos = center + it.position(state.time)
        drawLine(
            it.color.copy(
                alpha = state.globalAlpha
            ), prevPos, pos,
            strokeWidth = state.particleInfo.width
        )
    }
}

@Composable
fun ConfettiCanvas(trigger: Boolean) {
    if (!trigger) return

    val confettiColors = remember { listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow) }
    val confettiState = rememberConfettiState(
        Offset.Zero,
        emitter = Emitter(
            width = 500f, height = 10f,
            angle = 270f, aperture = 15f,
            minSpeed = 150f, maxSpeed = 300f
        ),
        particleInfo = ParticleInfo(
            colors = confettiColors,
            width = 8f,
            lengthEpsilon = 0.1f,
            lifespan = 1000
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawConfetti(confettiState)
    }
}

@Composable
fun FireworksCanvas(trigger: Boolean) {
    if (!trigger) return

    val confettiColors = remember { listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow) }
    val states = mutableListOf<ConfettiState>()
    val n = 10;

    for (i in 0..n) {
        states += rememberConfettiState(
            Offset(remap(i.toFloat(), 0f, n.toFloat(), -500f, 500f), -500f),
            emitter = Emitter(
                width = 0f, height = 0f,
                angle = 0f, aperture = 180f,
                minSpeed = 50f, maxSpeed = 70f
            ),
            particleInfo = ParticleInfo(
                colors = confettiColors,
                width = 5f,
                lengthEpsilon = 0.5f,
                lifespan = 1000,
                gravity=-10f
            ),
            particleCount = 10,
            delay=randomInt(0, 300)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        states.forEach {
            drawConfetti(it)
        }
    }
}
