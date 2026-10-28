package com.mimikunda.nightwatch.domain.reconciliation

import com.mimikunda.nightwatch.domain.model.StatusMarker

sealed class NightAction {
    abstract val targetSeatId: String

    data class TargetByWolves(override val targetSeatId: String) : NightAction()
    data class ProtectByBodyguard(override val targetSeatId: String) : NightAction()
    data class SaveByPotion(override val targetSeatId: String) : NightAction()
    data class PoisonByWitch(override val targetSeatId: String) : NightAction()
    data class InspectBySeer(override val targetSeatId: String) : NightAction()
    data class ApplyStatus(override val targetSeatId: String, val marker: StatusMarker) : NightAction()
}
