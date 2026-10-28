package com.mimikunda.nightwatch.ui.notebook.geometry

import androidx.compose.ui.geometry.Offset
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.domain.model.TableShape
import kotlin.math.cos
import kotlin.math.sin

object SeatCoordinateSolver {

    /**
     * Computes the exact pixel center (Offset) for a Seat within the canvas.
     * Supports RECTANGLE, CIRCLE, OVAL, HEXAGON, and OCTAGON geometries.
     */
    fun resolveSeatCenter(
        seat: Seat,
        totalSeats: Int = 8,
        edgeCounts: Map<TableEdge, Int> = emptyMap(),
        canvasWidth: Float,
        canvasHeight: Float,
        marginPx: Float,
        tableShape: TableShape = TableShape.RECTANGLE
    ): Offset {
        val pos = seat.position
        if (pos.customNormalizedX != null && pos.customNormalizedY != null) {
            return Offset(
                x = (pos.customNormalizedX * canvasWidth).coerceIn(marginPx, canvasWidth - marginPx),
                y = (pos.customNormalizedY * canvasHeight).coerceIn(marginPx, canvasHeight - marginPx)
            )
        }

        if (pos.edge == TableEdge.FLOATING) {
            return Offset(
                x = canvasWidth * 0.5f,
                y = (canvasHeight - marginPx * 0.8f).coerceIn(marginPx, canvasHeight - marginPx)
            )
        }

        val leftX = marginPx
        val rightX = canvasWidth - marginPx
        val topY = marginPx
        val bottomY = canvasHeight - marginPx
        val usableW = (rightX - leftX).coerceAtLeast(1f)
        val usableH = (bottomY - topY).coerceAtLeast(1f)
        val centerX = canvasWidth * 0.5f
        val centerY = canvasHeight * 0.5f

        val totalPerimeterSeats = (totalSeats - (edgeCounts[TableEdge.FLOATING] ?: 0)).coerceAtLeast(1)

        return when (tableShape) {
            TableShape.RECTANGLE -> {
                resolveRectangleCenter(seat, edgeCounts, leftX, rightX, topY, bottomY, usableW, usableH)
            }
            TableShape.CIRCLE -> {
                val radius = (minOf(usableW, usableH) * 0.42f).coerceAtLeast(10f)
                val angle = (2.0 * Math.PI * seat.index / totalPerimeterSeats) - (Math.PI / 2.0)
                Offset(
                    x = (centerX + radius * cos(angle)).toFloat(),
                    y = (centerY + radius * sin(angle)).toFloat()
                )
            }
            TableShape.OVAL -> {
                val rx = (usableW * 0.42f).coerceAtLeast(10f)
                val ry = (usableH * 0.42f).coerceAtLeast(10f)
                val angle = (2.0 * Math.PI * seat.index / totalPerimeterSeats) - (Math.PI / 2.0)
                Offset(
                    x = (centerX + rx * cos(angle)).toFloat(),
                    y = (centerY + ry * sin(angle)).toFloat()
                )
            }
            TableShape.HEXAGON -> {
                resolveRegularPolygonCenter(seat.index, totalPerimeterSeats, centerX, centerY, usableW * 0.40f, usableH * 0.40f)
            }
            TableShape.OCTAGON -> {
                resolveRegularPolygonCenter(seat.index, totalPerimeterSeats, centerX, centerY, usableW * 0.42f, usableH * 0.42f)
            }
        }
    }

    private fun resolveRectangleCenter(
        seat: Seat,
        edgeCounts: Map<TableEdge, Int>,
        leftX: Float,
        rightX: Float,
        topY: Float,
        bottomY: Float,
        usableW: Float,
        usableH: Float
    ): Offset {
        val pos = seat.position
        val count = (edgeCounts[pos.edge] ?: 1).coerceAtLeast(1)
        val fraction = (pos.positionOnEdge + 1).toFloat() / (count + 1).toFloat()

        return when (pos.edge) {
            TableEdge.TOP -> Offset(x = leftX + usableW * fraction, y = topY)
            TableEdge.RIGHT -> Offset(x = rightX, y = topY + usableH * fraction)
            TableEdge.BOTTOM -> Offset(x = rightX - usableW * fraction, y = bottomY)
            TableEdge.LEFT -> Offset(x = leftX, y = bottomY - usableH * fraction)
            TableEdge.FLOATING -> Offset(x = (leftX + rightX) * 0.5f, y = bottomY)
        }
    }

    private fun resolveRegularPolygonCenter(
        seatIndex: Int,
        totalSeats: Int,
        centerX: Float,
        centerY: Float,
        radiusX: Float,
        radiusY: Float
    ): Offset {
        val angle = (2.0 * Math.PI * seatIndex / totalSeats) - (Math.PI / 2.0)
        return Offset(
            x = (centerX + radiusX * cos(angle)).toFloat(),
            y = (centerY + radiusY * sin(angle)).toFloat()
        )
    }
}
