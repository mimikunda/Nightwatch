package com.mimikunda.nightwatch.domain.settings

import com.mimikunda.nightwatch.domain.model.RoleTeam

data class CustomRole(
    val roleId: String,
    val displayName: String,
    val team: RoleTeam,
    val colorArgb: Long,
    val isEnabled: Boolean = true,
    val isCustom: Boolean = false,
    val associatedEffect: CustomStatusEffect? = null
) {
    companion object {
        val VILLAGER = CustomRole("villager", "Villager", RoleTeam.VILLAGE, 0xFF455A64)
        val WEREWOLVES = CustomRole("werewolf", "Werewolf", RoleTeam.WEREWOLVES, 0xFFFF5449)
        val SEER = CustomRole("seer", "Seer", RoleTeam.VILLAGE, 0xFF9FA8DA)
        val BODYGUARD = CustomRole("bodyguard", "Bodyguard", RoleTeam.VILLAGE, 0xFF4FC3F7)
        val WITCH = CustomRole("witch", "Witch", RoleTeam.VILLAGE, 0xFFD05CE3)
        val MAYOR = CustomRole("mayor", "Mayor", RoleTeam.VILLAGE, 0xFFFFB300)

        val DEFAULT_ROLES = listOf(VILLAGER, WEREWOLVES, SEER, BODYGUARD, WITCH, MAYOR)
    }
}
