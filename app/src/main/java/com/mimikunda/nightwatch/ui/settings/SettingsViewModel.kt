package com.mimikunda.nightwatch.ui.settings

import androidx.lifecycle.ViewModel
import com.mimikunda.nightwatch.domain.model.RoleTeam
import com.mimikunda.nightwatch.domain.settings.CustomStatusEffect
import com.mimikunda.nightwatch.domain.settings.GameSettings
import com.mimikunda.nightwatch.domain.settings.GameSettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    val settings: StateFlow<GameSettings> = GameSettingsRepository.settings

    fun toggleRole(roleId: String) {
        GameSettingsRepository.toggleRole(roleId)
    }

    fun toggleEffect(effectId: String) {
        GameSettingsRepository.toggleEffect(effectId)
    }

    fun addCustomRole(
        name: String,
        team: RoleTeam,
        colorArgb: Long,
        associatedEffect: CustomStatusEffect?
    ) {
        GameSettingsRepository.addCustomRole(name, team, colorArgb, associatedEffect)
    }

    fun deleteCustomRole(roleId: String) {
        GameSettingsRepository.deleteCustomRole(roleId)
    }

    fun resetToDefaults() {
        GameSettingsRepository.resetToDefaults()
    }
}
