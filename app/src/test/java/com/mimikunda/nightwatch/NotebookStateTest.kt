package com.mimikunda.nightwatch

import com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.state.NotebookState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotebookStateTest {

    @Test
    fun `toggleStatus adds and removes status markers correctly`() {
        val table = PerimeterSeatingTable.build(topCount = 2)
        var state = NotebookState(seats = table.seats)
        val targetSeatId = table.seats[0].id

        // Toggle ON
        state = state.toggleStatus(targetSeatId, StatusMarker.MAYOR)
        val seatAfterAdd = state.seats.first { it.id == targetSeatId }
        assertTrue(seatAfterAdd.activeStatuses.contains(StatusMarker.MAYOR))

        // Toggle OFF
        state = state.toggleStatus(targetSeatId, StatusMarker.MAYOR)
        val seatAfterRemove = state.seats.first { it.id == targetSeatId }
        assertFalse(seatAfterRemove.activeStatuses.contains(StatusMarker.MAYOR))
    }

    @Test
    fun `setLifeStatus updates seat vitality`() {
        val table = PerimeterSeatingTable.build(topCount = 2)
        var state = NotebookState(seats = table.seats)
        val targetSeatId = table.seats[0].id

        state = state.setLifeStatus(targetSeatId, false)
        assertFalse(state.seats.first { it.id == targetSeatId }.isAlive)

        state = state.setLifeStatus(targetSeatId, true)
        assertTrue(state.seats.first { it.id == targetSeatId }.isAlive)
    }

    @Test
    fun `linkSeats and unlinkSeats perform symmetric link mutations`() {
        val table = PerimeterSeatingTable.build(topCount = 3)
        var state = NotebookState(seats = table.seats)
        val seatA = table.seats[0].id
        val seatB = table.seats[1].id

        // Link
        state = state.linkSeats(seatA, seatB)
        assertTrue(state.seats.first { it.id == seatA }.linkedSeatIds.contains(seatB))
        assertTrue(state.seats.first { it.id == seatB }.linkedSeatIds.contains(seatA))

        // Unlink
        state = state.unlinkSeats(seatA, seatB)
        assertFalse(state.seats.first { it.id == seatA }.linkedSeatIds.contains(seatB))
        assertFalse(state.seats.first { it.id == seatB }.linkedSeatIds.contains(seatA))
    }

    @Test
    fun `updateSeatName and updateSeatRole update metadata`() {
        val table = PerimeterSeatingTable.build(topCount = 2)
        var state = NotebookState(seats = table.seats)
        val targetSeatId = table.seats[0].id

        state = state.updateSeatName(targetSeatId, "Alice")
        state = state.updateSeatRole(targetSeatId, "seer")

        val seat = state.seats.first { it.id == targetSeatId }
        assertEquals("Alice", seat.name)
        assertEquals("seer", seat.roleId)
    }

    @Test
    fun `updateSeatCustomPosition sets custom coordinates`() {
        val table = PerimeterSeatingTable.build(topCount = 2)
        var state = NotebookState(seats = table.seats)
        val targetSeatId = table.seats[0].id

        state = state.updateSeatCustomPosition(targetSeatId, 0.45f, 0.85f)

        val seat = state.seats.first { it.id == targetSeatId }
        assertEquals(0.45f, seat.position.customNormalizedX!!, 0.001f)
        assertEquals(0.85f, seat.position.customNormalizedY!!, 0.001f)
    }

    @Test
    fun `clearNightMarkers removes transient night statuses but retains MAYOR`() {
        val table = PerimeterSeatingTable.build(topCount = 2)
        var state = NotebookState(seats = table.seats)
        val targetSeatId = table.seats[0].id

        state = state.toggleStatus(targetSeatId, StatusMarker.WOLF_TARGET)
        state = state.toggleStatus(targetSeatId, StatusMarker.MAYOR)

        state = state.clearNightMarkers()

        val seat = state.seats.first { it.id == targetSeatId }
        assertFalse(seat.activeStatuses.contains(StatusMarker.WOLF_TARGET))
        assertTrue(seat.activeStatuses.contains(StatusMarker.MAYOR))
    }

    @Test
    fun `updateEdgeCounts resizes seating layout while preserving existing seat data`() {
        val table = PerimeterSeatingTable.build(topCount = 2)
        var state = NotebookState(seats = table.seats)
        state = state.updateSeatName(table.seats[0].id, "Bob")

        state = state.updateEdgeCounts(topCount = 3, rightCount = 1, bottomCount = 2, leftCount = 1, floatingCount = 0)

        assertEquals(7, state.seats.size)
        assertEquals("Bob", state.seats[0].name)
    }
}
