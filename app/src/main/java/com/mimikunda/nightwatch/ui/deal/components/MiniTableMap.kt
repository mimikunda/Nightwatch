package com.mimikunda.nightwatch.ui.deal.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.ui.notebook.geometry.SeatCoordinateSolver
import com.mimikunda.nightwatch.ui.theme.TableSurfaceBorder
import com.mimikunda.nightwatch.ui.theme.TableSurfaceCenter

@Composable
fun MiniTableMap(
    seats: List<Seat>,
    edgeCounts: Map<TableEdge, Int>,
    currentSeatIndex: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .width(180.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .border(1.dp, TableSurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val marginPx = 28.dp.toPx()

            // 1. Draw central table surface outline
            val insetX = canvasW * 0.20f
            val insetY = canvasH * 0.20f
            val tableW = canvasW - (insetX * 2f)
            val tableH = canvasH - (insetY * 2f)

            if (tableW > 0 && tableH > 0) {
                drawRoundRect(
                    color = TableSurfaceCenter,
                    topLeft = Offset(insetX, insetY),
                    size = Size(tableW, tableH),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
                drawRoundRect(
                    color = TableSurfaceBorder,
                    topLeft = Offset(insetX, insetY),
                    size = Size(tableW, tableH),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // 2. Draw seats around table
            seats.forEach { seat ->
                val center = SeatCoordinateSolver.resolveSeatCenter(
                    seat = seat,
                    edgeCounts = edgeCounts,
                    canvasWidth = canvasW,
                    canvasHeight = canvasH,
                    marginPx = marginPx
                )

                val isTarget = seat.index == currentSeatIndex

                if (isTarget) {
                    // Pulsing outer ring
                    drawCircle(
                        color = Color(0xFF4FC3F7).copy(alpha = pulseAlpha),
                        radius = 16.dp.toPx(),
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    // Inner filled node
                    drawCircle(
                        color = Color(0xFF4FC3F7),
                        radius = 9.dp.toPx(),
                        center = center
                    )
                } else {
                    drawCircle(
                        color = Color.DarkGray,
                        radius = 5.dp.toPx(),
                        center = center
                    )
                }
            }
        }
    }
}
