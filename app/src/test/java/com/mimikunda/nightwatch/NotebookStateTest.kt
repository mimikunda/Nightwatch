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
}
