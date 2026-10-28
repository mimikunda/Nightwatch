package com.mimikunda.nightwatch.domain.model

enum class RoleTeam {
    VILLAGE,
    WEREWOLVES,
    SOLO,
    NEUTRAL
}

data class Role(
    val roleId: String,
    val displayName: String,
    val team: RoleTeam,
    val nightActionOrder: Int? = null,
    val defaultCountLimit: Int = 1
) {
    companion object {
        val VILLAGER = Role(
            roleId = "villager",
            displayName = "Villager",
            team = RoleTeam.VILLAGE,
            defaultCountLimit = 10
        )
        val WEREWOLF = Role(
            roleId = "werewolf",
            displayName = "Werewolf",
            team = RoleTeam.WEREWOLVES,
            nightActionOrder = 10,
            defaultCountLimit = 4
        )
        val SEER = Role(
            roleId = "seer",
            displayName = "Seer",
            team = RoleTeam.VILLAGE,
            nightActionOrder = 20,
            defaultCountLimit = 1
        )
        val BODYGUARD = Role(
            roleId = "bodyguard",
            displayName = "Bodyguard",
            team = RoleTeam.VILLAGE,
            nightActionOrder = 30,
            defaultCountLimit = 1
        )
        val WITCH = Role(
            roleId = "witch",
            displayName = "Witch",
            team = RoleTeam.VILLAGE,
            nightActionOrder = 40,
            defaultCountLimit = 1
        )
        val MAYOR = Role(
            roleId = "mayor",
            displayName = "Mayor",
            team = RoleTeam.VILLAGE,
            defaultCountLimit = 1
        )

        val ALL_ROLES = listOf(VILLAGER, WEREWOLF, SEER, BODYGUARD, WITCH, MAYOR)
    }
}
