/** Claude 3.7 Sonnet - Início
 * Faça que quando esteja chovendo, o WeaherCardComponent tenha um efeito de chuva
 */

package br.com.wheatherApp.components
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

@Composable
fun RainEffect(
    modifier: Modifier = Modifier,
    color: Color = Color(0x885D9CEC),
    density: Float = 0.3f,
    speed: Float = 1.2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rain")

    // Animação do movimento da chuva - invertida para cair para baixo
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1000 / speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainfall"
    )

    // Criação das posições aleatórias das gotas
    val raindrops = remember {
        val count = (150 * density).toInt()
        List(count) {
            val x = Random.nextFloat()
            val y = Random.nextFloat()
            val length = 0.05f + Random.nextFloat() * 0.07f  // Gotas maiores
            val thickness = 1.5f + Random.nextFloat() * 2f   // Mais grossas
            RainDrop(x, y, length, thickness)
        }
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        raindrops.forEach { drop ->
            // Calculando a posição ajustada com o progresso da animação
            // Aqui invertemos a direção para que a chuva caia para baixo
            val startY = (drop.y + animationProgress) % 1f
            val endY = startY + drop.length

            // Desenha a gota somente se estiver dentro dos limites do canvas
            if (startY <= 1f) {
                // Convertendo as coordenadas normalizadas para pixels
                val startX = drop.x * canvasWidth
                val startYPx = startY * canvasHeight
                val endYPx = endY * canvasHeight

                rotate(0f) {  // Mudando a direção da inclinação
                    drawLine(
                        color = color,
                        start = Offset(startX, startYPx),
                        end = Offset(startX, endYPx),
                        strokeWidth = drop.thickness
                    )
                }
            }
        }
    }
}

private data class RainDrop(
    val x: Float,
    val y: Float,
    val length: Float,
    val thickness: Float
)

/** Claude 3.7 Sonnet - Fim */