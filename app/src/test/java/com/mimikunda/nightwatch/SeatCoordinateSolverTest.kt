package com.mimikunda.nightwatch

import com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable
import com.mimikunda.nightwatch.domain.model.TableShape
import com.mimikunda.nightwatch.ui.notebook.geometry.SeatCoordinateSolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeatCoordinateSolverTest {

    @Test
    fun `resolveSeatCenter generates valid offsets for CIRCLE and OVAL shapes`() {
        val table = PerimeterSeatingTable.build(topCount = 2, rightCount = 2, bottomCount = 2, leftCount = 2)
        val canvasW = 1000f
        val canvasH = 1000f
        val marginPx = 100f

        // Circle placement
        val circleCenter0 = SeatCoordinateSolver.resolveSeatCenter(
            seat = table.seats[0],
            totalSeats = table.seats.size,
            edgeCounts = emptyMap(),
            canvasWidth = canvasW,
            canvasHeight = canvasH,
            marginPx = marginPx,
            tableShape = TableShape.CIRCLE
        )

        // Top seat (index 0) should be at top center (x ~ 500, y < 500)
        assertEquals(500f, circleCenter0.x, 1f)
        assertTrue(circleCenter0.y < 500f)

        // Oval placement
        val ovalCenter0 = SeatCoordinateSolver.resolveSeatCenter(
            seat = table.seats[0],
            totalSeats = table.seats.size,
            edgeCounts = emptyMap(),
            canvasWidth = canvasW,
            canvasHeight = canvasH,
            marginPx = marginPx,
            tableShape = TableShape.OVAL
        )

        assertEquals(500f, ovalCenter0.x, 1f)
        assertTrue(ovalCenter0.y < 500f)
    }

    @Test
    fun `resolveSeatCenter generates valid offsets for HEXAGON and OCTAGON shapes`() {
        val table = PerimeterSeatingTable.build(topCount = 3, bottomCount = 3)
        val canvasW = 800f
        val canvasH = 800f
        val marginPx = 80f

        val hexCenter = SeatCoordinateSolver.resolveSeatCenter(
            seat = table.seats[0],
            totalSeats = table.seats.size,
            edgeCounts = emptyMap(),
            canvasWidth = canvasW,
            canvasHeight = canvasH,
            marginPx = marginPx,
            tableShape = TableShape.HEXAGON
        )

        val octCenter = SeatCoordinateSolver.resolveSeatCenter(
            seat = table.seats[0],
            totalSeats = table.seats.size,
            edgeCounts = emptyMap(),
            canvasWidth = canvasW,
            canvasHeight = canvasH,
            marginPx = marginPx,
            tableShape = TableShape.OCTAGON
        )

        assertEquals(400f, hexCenter.x, 1f)
        assertEquals(400f, octCenter.x, 1f)
    }
}
