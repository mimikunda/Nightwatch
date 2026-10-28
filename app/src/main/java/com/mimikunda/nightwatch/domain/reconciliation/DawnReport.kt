package com.mimikunda.nightwatch.domain.reconciliation

import com.mimikunda.nightwatch.domain.model.Seat

data class DawnReport(
    val eliminatedSeatIds: Set<String> = emptySet(),
    val savedSeatIds: Set<String> = emptySet(),
    val cascadeEliminatedSeatIds: Set<String> = emptySet(),
    val updatedSeats: List<Seat> = emptyList(),
    val logMessages: List<String> = emptyList()
)
