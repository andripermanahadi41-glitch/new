package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun RotaryKnob(
    label: String,
    value: Float, // 0f .. 100f
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = PedalOrange,
    trackColor: Color = PedalBorder
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .width(68.dp)
            .testTag("knob_${label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(46.dp)
                .pointerInput(label) {
                    detectDragGestures(
                        onDragStart = {
                            dragAccumulator = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Dragging upwards increases value, downwards decreases
                            val delta = -dragAmount.y * 0.75f + dragAmount.x * 0.25f
                            val newValue = (value + delta).coerceIn(0f, 100f)
                            currentOnValueChange(newValue)
                        }
                    )
                }
        ) {
            val strokeWidth = 4.5.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val arcTopLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2, radius * 2)

            // Start angle 135 degrees (bottom left), sweep 270 degrees around top to bottom right (45 degrees)
            val startAngle = 135f
            val maxSweepAngle = 270f
            val currentSweep = (value / 100f) * maxSweepAngle

            // Background inactive arc
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = maxSweepAngle,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active colored arc
            if (currentSweep > 0.5f) {
                drawArc(
                    color = accentColor,
                    startAngle = startAngle,
                    sweepAngle = currentSweep,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Inner knob body
            val innerRadius = radius * 0.72f
            drawCircle(
                color = Color(0xFF1B1D22),
                radius = innerRadius,
                center = center
            )
            drawCircle(
                color = Color(0xFF282B33),
                radius = innerRadius,
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Pointer tick line
            val pointerAngleDeg = startAngle + currentSweep
            val pointerAngleRad = Math.toRadians(pointerAngleDeg.toDouble())
            val pointerStart = Offset(
                x = (center.x + innerRadius * 0.35f * cos(pointerAngleRad)).toFloat(),
                y = (center.y + innerRadius * 0.35f * sin(pointerAngleRad)).toFloat()
            )
            val pointerEnd = Offset(
                x = (center.x + innerRadius * 0.88f * cos(pointerAngleRad)).toFloat(),
                y = (center.y + innerRadius * 0.88f * sin(pointerAngleRad)).toFloat()
            )

            drawLine(
                color = Color.White,
                start = pointerStart,
                end = pointerEnd,
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Text(
            text = label,
            fontSize = 9.sp,
            color = PedalTextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${value.roundToInt()}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            textAlign = TextAlign.Center
        )
    }
}
