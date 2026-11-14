package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.WarningAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RiskGauge(
    score: Int, // 0 to 100
    modifier: Modifier = Modifier
) {
    val animatedScore = remember { Animatable(0f) }

    LaunchedEffect(score) {
        animatedScore.animateTo(
            targetValue = score.toFloat(),
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp, 150.dp)) {
            val strokeWidth = 24.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, (size.height * 2) - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Background track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.25f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Multi-colored gradient arc
            val gradientBrush = Brush.sweepGradient(
                0.5f to SafetyGreen,
                0.75f to WarningAmber,
                1.0f to DangerRed,
                center = Offset(size.width / 2, size.height)
            )

            drawArc(
                brush = gradientBrush,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Needle indicator
            val currentProgress = (animatedScore.value / 100f).coerceIn(0f, 1f)
            val needleAngleRad = (180f + currentProgress * 180f) * (PI / 180f).toFloat()

            val center = Offset(size.width / 2, size.height - 10.dp.toPx())
            val needleLength = size.width * 0.38f
            val endX = center.x + needleLength * cos(needleAngleRad)
            val endY = center.y + needleLength * sin(needleAngleRad)

            // Draw needle center pivot
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 10.dp.toPx(),
                center = center
            )
            drawCircle(
                color = DangerRed,
                radius = 5.dp.toPx(),
                center = center
            )

            // Draw needle pointer
            drawLine(
                color = Color(0xFF1E293B),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Score text inside/below gauge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "${animatedScore.value.toInt()}/100",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    score >= 70 -> DangerRed
                    score >= 40 -> WarningAmber
                    else -> SafetyGreen
                }
            )
            Text(
                text = "Mức độ rủi ro",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
