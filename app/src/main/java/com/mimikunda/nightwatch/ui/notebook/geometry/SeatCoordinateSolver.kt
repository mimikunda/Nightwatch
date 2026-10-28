package com.mimikunda.nightwatch.ui.notebook.geometry

import androidx.compose.ui.geometry.Offset
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.TableEdge

object SeatCoordinateSolver {

    /**
     * Computes the exact pixel center (Offset) for a Seat within the canvas.
     * Ensures strict clockwise topology:
     * TOP: L -> R | RIGHT: T -> B | BOTTOM: R -> L | LEFT: B -> T
     */
    fun resolveSeatCenter(
        seat: Seat,
        edgeCounts: Map<TableEdge, Int>,
        canvasWidth: Float,
        canvasHeight: Float,
        marginPx: Float
    ): Offset {
        val pos = seat.position
        if (pos.customNormalizedX != null && pos.customNormalizedY != null) {
            return Offset(
                x = (pos.customNormalizedX * canvasWidth).coerceIn(marginPx, canvasWidth - marginPx),
                y = (pos.customNormalizedY * canvasHeight).coerceIn(marginPx, canvasHeight - marginPx)
            )
        }

        val count = (edgeCounts[pos.edge] ?: 1).coerceAtLeast(1)
        val fraction = (pos.positionOnEdge + 1).toFloat() / (count + 1).toFloat()

        val leftX = marginPx
        val rightX = canvasWidth - marginPx
        val topY = marginPx
        val bottomY = canvasHeight - marginPx
        val usableW = (rightX - leftX).coerceAtLeast(1f)
        val usableH = (bottomY - topY).coerceAtLeast(1f)

        return when (pos.edge) {
            TableEdge.TOP -> Offset(
                x = leftX + usableW * fraction,
                y = topY
            )
            TableEdge.RIGHT -> Offset(
                x = rightX,
                y = topY + usableH * fraction
            )
            TableEdge.BOTTOM -> Offset(
                // Clockwise: index 0 on BOTTOM starts near the RIGHT corner and moves LEFT
                x = rightX - usableW * fraction,
                y = bottomY
            )
            TableEdge.LEFT -> Offset(
                // Clockwise: index 0 on LEFT starts near the BOTTOM corner and moves UP
                x = leftX,
                y = bottomY - usableH * fraction
            )
            TableEdge.FLOATING -> Offset(
                x = canvasWidth * 0.5f,
                y = bottomY - (marginPx * 0.8f)
            )
        }
    }
}
