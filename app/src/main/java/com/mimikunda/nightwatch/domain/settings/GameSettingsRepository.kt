package com.mimikunda.nightwatch.domain.settings

import com.mimikunda.nightwatch.domain.model.RoleTeam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object GameSettingsRepository {

    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    fun toggleRole(roleId: String) {
        _settings.update { current ->
            current.copy(
                roles = current.roles.map { role ->
                    if (role.roleId == roleId) role.copy(isEnabled = !role.isEnabled) else role
                }
            )
        }
    }

    fun toggleEffect(effectId: String) {
        _settings.update { current ->
            current.copy(
                effects = current.effects.map { effect ->
                    if (effect.id == effectId) effect.copy(isEnabled = !effect.isEnabled) else effect
                }
            )
        }
    }

    fun addCustomRole(
        name: String,
        team: RoleTeam,
        colorArgb: Long,
        associatedEffect: CustomStatusEffect? = null
    ) {
        val roleId = "custom_role_${System.currentTimeMillis()}"
        val newRole = CustomRole(
            roleId = roleId,
            displayName = name,
            team = team,
            colorArgb = colorArgb,
            isEnabled = true,
            isCustom = true,
            associatedEffect = associatedEffect
        )

        _settings.update { current ->
            val updatedRoles = current.roles + newRole
            val updatedEffects = if (associatedEffect != null) {
                current.effects + associatedEffect
            } else {
                current.effects
            }
            current.copy(roles = updatedRoles, effects = updatedEffects)
        }
    }

    fun deleteCustomRole(roleId: String) {
        _settings.update { current ->
            val roleToDelete = current.roles.find { it.roleId == roleId }
            val effectToDeleteId = roleToDelete?.associatedEffect?.id
            current.copy(
                roles = current.roles.filterNot { it.roleId == roleId },
                effects = current.effects.filterNot { it.id == effectToDeleteId }
            )
        }
    }

    fun resetToDefaults() {
        _settings.value = GameSettings()
    }
}
