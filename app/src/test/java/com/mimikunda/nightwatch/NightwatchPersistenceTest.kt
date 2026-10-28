package com.mimikunda.nightwatch

import com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable
import com.mimikunda.nightwatch.domain.model.RoleTeam
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.persistence.NightwatchPersistence
import com.mimikunda.nightwatch.domain.settings.CustomRole
import com.mimikunda.nightwatch.domain.settings.CustomStatusEffect
import com.mimikunda.nightwatch.domain.settings.GameSettings
import com.mimikunda.nightwatch.domain.state.NotebookState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NightwatchPersistenceTest {

    @Test
    fun `settingsToJson and jsonToSettings preserves custom roles and effects`() {
        val customEffect = CustomStatusEffect("EFFECT_LOVERS", "In Love", 0xFFFF4081, "Heart", isCustom = true)
        val initialSettings = GameSettings()
        val customRoleSettings = initialSettings.copy(
            roles = initialSettings.roles + CustomRole(
                roleId = "custom_cupid",
                displayName = "Cupid",
                team = RoleTeam.NEUTRAL,
                colorArgb = 0xFFFF4081,
                isCustom = true,
                associatedEffect = customEffect
            ),
            effects = initialSettings.effects + customEffect
        )

        val json = NightwatchPersistence.settingsToJson(customRoleSettings)
        val loadedSettings = NightwatchPersistence.jsonToSettings(json)

        assertNotNull(loadedSettings)
        assertEquals(customRoleSettings.roles.size, loadedSettings.roles.size)
        val cupid = loadedSettings.roles.find { it.roleId == "custom_cupid" }
        assertNotNull(cupid)
        assertEquals("Cupid", cupid!!.displayName)
        assertEquals("In Love", cupid.associatedEffect?.name)
    }

    @Test
    fun `notebookStateToJson and jsonToNotebookState preserves seats custom drag coordinates and statuses`() {
        val table = PerimeterSeatingTable.build(topCount = 2, rightCount = 1)
        var state = NotebookState(seats = table.seats)
        val seatId = table.seats[0].id

        state = state.updateSeatName(seatId, "Alice")
        state = state.updateSeatRole(seatId, "seer")
        state = state.updateSeatCustomPosition(seatId, 0.35f, 0.75f)
        state = state.addStatus(seatId, StatusMarker.MAYOR)

        val json = NightwatchPersistence.notebookStateToJson(state)
        val loadedState = NightwatchPersistence.jsonToNotebookState(json)

        assertNotNull(loadedState)
        assertEquals(3, loadedState!!.seats.size)

        val seat0 = loadedState.seats.first { it.id == seatId }
        assertEquals("Alice", seat0.name)
        assertEquals("seer", seat0.roleId)
        assertEquals(0.35f, seat0.position.customNormalizedX!!, 0.001f)
        assertEquals(0.75f, seat0.position.customNormalizedY!!, 0.001f)
        assertTrue(seat0.activeStatuses.contains(StatusMarker.MAYOR))
    }
}
