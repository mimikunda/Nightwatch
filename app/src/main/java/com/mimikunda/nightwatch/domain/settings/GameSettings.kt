package com.mimikunda.nightwatch.domain.settings

data class GameSettings(
    val roles: List<CustomRole> = CustomRole.DEFAULT_ROLES,
    val effects: List<CustomStatusEffect> = CustomStatusEffect.DEFAULT_EFFECTS
) {
    val enabledRoles: List<CustomRole>
        get() = roles.filter { it.isEnabled }

    val enabledEffects: List<CustomStatusEffect>
        get() = effects.filter { it.isEnabled }
}
