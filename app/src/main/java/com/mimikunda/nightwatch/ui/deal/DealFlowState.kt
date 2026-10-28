package com.mimikunda.nightwatch.ui.deal

import com.mimikunda.nightwatch.domain.model.RoleDeckConfig

sealed interface DealFlowState {
    data object Inactive : DealFlowState
    data class ConfigureDeck(val deckConfig: RoleDeckConfig = RoleDeckConfig()) : DealFlowState
    data class PassToPlayer(
        val currentSeatIndex: Int,
        val totalSeats: Int,
        val draftedName: String = "",
        val hasPeekedRole: Boolean = false,
        val shuffledRoleIds: List<String> = emptyList()
    ) : DealFlowState
    data object ReadyForModerator : DealFlowState
}
