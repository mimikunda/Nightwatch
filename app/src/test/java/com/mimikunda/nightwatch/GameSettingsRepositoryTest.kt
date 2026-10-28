package com.mimikunda.nightwatch

import com.mimikunda.nightwatch.domain.model.RoleTeam
import com.mimikunda.nightwatch.domain.settings.CustomStatusEffect
import com.mimikunda.nightwatch.domain.settings.GameSettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameSettingsRepositoryTest {

    @Before
    fun setUp() {
        GameSettingsRepository.resetToDefaults()
    }

    @Test
    fun `toggleRole disables and enables roles`() {
        val seerId = "seer"

        GameSettingsRepository.toggleRole(seerId)
        var settings = GameSettingsRepository.settings.value
        assertFalse(settings.roles.first { it.roleId == seerId }.isEnabled)
        assertFalse(settings.enabledRoles.any { it.roleId == seerId })

        GameSettingsRepository.toggleRole(seerId)
        settings = GameSettingsRepository.settings.value
        assertTrue(settings.roles.first { it.roleId == seerId }.isEnabled)
        assertTrue(settings.enabledRoles.any { it.roleId == seerId })
    }

    @Test
    fun `toggleEffect disables and enables status effects`() {
        val effectId = "PROTECTED"

        GameSettingsRepository.toggleEffect(effectId)
        var settings = GameSettingsRepository.settings.value
        assertFalse(settings.effects.first { it.id == effectId }.isEnabled)
        assertFalse(settings.enabledEffects.any { it.id == effectId })

        GameSettingsRepository.toggleEffect(effectId)
        settings = GameSettingsRepository.settings.value
        assertTrue(settings.effects.first { it.id == effectId }.isEnabled)
        assertTrue(settings.enabledEffects.any { it.id == effectId })
    }

    @Test
    fun `addCustomRole adds new role and optional associated effect`() {
        val customEffect = CustomStatusEffect("EFFECT_CUPID", "In Love", 0xFFFF4081, "Heart", isCustom = true)

        GameSettingsRepository.addCustomRole(
            name = "Cupid",
            team = RoleTeam.NEUTRAL,
            colorArgb = 0xFFFF4081,
            associatedEffect = customEffect
        )

        val settings = GameSettingsRepository.settings.value
        val addedRole = settings.roles.find { it.displayName == "Cupid" }

        assertNotNull(addedRole)
        assertTrue(addedRole!!.isCustom)
        assertEquals(RoleTeam.NEUTRAL, addedRole.team)

        val addedEffect = settings.effects.find { it.id == "EFFECT_CUPID" }
        assertNotNull(addedEffect)
        assertEquals("In Love", addedEffect!!.name)
    }

    @Test
    fun `deleteCustomRole removes role and associated effect`() {
        val customEffect = CustomStatusEffect("EFFECT_HUNTER", "Targeted by Hunter", 0xFFFF9800, "Target", isCustom = true)

        GameSettingsRepository.addCustomRole(
            name = "Hunter",
            team = RoleTeam.VILLAGE,
            colorArgb = 0xFFFF9800,
            associatedEffect = customEffect
        )

        var settings = GameSettingsRepository.settings.value
        val hunterRole = settings.roles.find { it.displayName == "Hunter" }
        assertNotNull(hunterRole)

        GameSettingsRepository.deleteCustomRole(hunterRole!!.roleId)

        settings = GameSettingsRepository.settings.value
        assertFalse(settings.roles.any { it.displayName == "Hunter" })
        assertFalse(settings.effects.any { it.id == "EFFECT_HUNTER" })
    }
}
