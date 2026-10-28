package com.mimikunda.nightwatch

import com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable
import com.mimikunda.nightwatch.domain.model.RoleDeckConfig
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.state.NotebookState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoleDeckConfigTest {

    @Test
    fun `buildShuffledDeck fills remaining slots with villager`() {
        val config = RoleDeckConfig(
            specialRoleCounts = mapOf(
                "werewolf" to 2,
                "seer" to 1
            )
        )

        val totalSeats = 8
        val deck = config.buildShuffledDeck(totalSeats)

        assertEquals(8, deck.size)
        assertEquals(2, deck.count { it == "werewolf" })
        assertEquals(1, deck.count { it == "seer" })
        assertEquals(5, deck.count { it == "villager" })
    }

    @Test
    fun `buildShuffledDeck truncates when special roles exceed total seats`() {
        val config = RoleDeckConfig(
            specialRoleCounts = mapOf(
                "werewolf" to 4,
                "seer" to 2,
                "bodyguard" to 2
            )
        )

        val totalSeats = 5
        val deck = config.buildShuffledDeck(totalSeats)

        assertEquals(5, deck.size)
    }

    @Test
    fun `NotebookState dealRoles assigns roles and resets seat vitality and statuses`() {
        val table = PerimeterSeatingTable.build(topCount = 3)
        var state = NotebookState(seats = table.seats)

        // Kill seat 0 and add status
        state = state.setLifeStatus(table.seats[0].id, false)
        state = state.addStatus(table.seats[0].id, StatusMarker.WOLF_TARGET)

        val shuffled = listOf("werewolf", "seer", "villager")
        state = state.dealRoles(shuffled)

        assertEquals(3, state.seats.size)
        assertEquals("werewolf", state.seats[0].roleId)
        assertEquals("seer", state.seats[1].roleId)
        assertEquals("villager", state.seats[2].roleId)

        // Verify vitality reset & status clearing
        assertTrue(state.seats[0].isAlive)
        assertTrue(state.seats[0].activeStatuses.isEmpty())
    }
}
