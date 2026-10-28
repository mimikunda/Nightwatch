package com.mimikunda.nightwatch.domain.reconciliation

import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.StatusMarker

object DawnReconciler {

    /**
     * Pure function reconciling night actions and seat statuses into a DawnReport.
     *
     * Rules:
     * - Targeted + Protected = Survives (Saved)
     * - Targeted + SavedByPotion = Survives (Saved)
     * - Poisoned = Dies
     * - Targeted (unprotected & unsaved) = Dies
     * - Targeted + Saved + Poisoned = Dies (Poison takes effect)
     * - If an eliminated seat has linkedSeatIds, linked seats cascade-eliminate (Lover suicide).
     */
    fun reconcileNight(
        seats: List<Seat>,
        stagedActions: List<NightAction> = emptyList()
    ): DawnReport {
        // Step 1: Apply staged actions to seats to get effective status set for each seat
        val effectiveSeats = seats.map { seat ->
            val additionalStatuses = stagedActions
                .filter { it.targetSeatId == seat.id }
                .mapNotNull { action ->
                    when (action) {
                        is NightAction.TargetByWolves -> StatusMarker.WOLF_TARGET
                        is NightAction.ProtectByBodyguard -> StatusMarker.PROTECTED
                        is NightAction.SaveByPotion -> StatusMarker.SAVED_POTION
                        is NightAction.PoisonByWitch -> StatusMarker.POISONED
                        is NightAction.InspectBySeer -> StatusMarker.SEER_INSPECTED
                        is NightAction.ApplyStatus -> action.marker
                    }
                }
            seat.copy(activeStatuses = seat.activeStatuses + additionalStatuses)
        }

        val directEliminatedIds = mutableSetOf<String>()
        val savedIds = mutableSetOf<String>()
        val logs = mutableListOf<String>()

        // Step 2: Evaluate direct casualties for alive seats
        for (seat in effectiveSeats) {
            if (!seat.isAlive) continue

            val statuses = seat.activeStatuses
            val isTargeted = StatusMarker.WOLF_TARGET in statuses
            val isProtected = StatusMarker.PROTECTED in statuses
            val isSavedPotion = StatusMarker.SAVED_POTION in statuses
            val isPoisoned = StatusMarker.POISONED in statuses

            val isProtectedOrSaved = isProtected || isSavedPotion

            if (isPoisoned) {
                directEliminatedIds.add(seat.id)
                logs.add("Seat ${seat.name ?: seat.id} died from poison.")
            } else if (isTargeted && !isProtectedOrSaved) {
                directEliminatedIds.add(seat.id)
                logs.add("Seat ${seat.name ?: seat.id} was attacked and eliminated.")
            } else if (isTargeted) {
                savedIds.add(seat.id)
                logs.add("Seat ${seat.name ?: seat.id} was attacked but saved.")
            }
        }

        // Step 3: Cascade eliminations for linked seats (e.g. Lovers)
        val cascadeEliminatedIds = mutableSetOf<String>()
        val queue = ArrayDeque<String>(directEliminatedIds)
        val processedSeatsForCascade = mutableSetOf<String>()
        processedSeatsForCascade.addAll(directEliminatedIds)

        val seatMap = effectiveSeats.associateBy { it.id }

        while (queue.isNotEmpty()) {
            val dyingId = queue.removeFirst()
            val dyingSeat = seatMap[dyingId] ?: continue

            for (linkedId in dyingSeat.linkedSeatIds) {
                val linkedSeat = seatMap[linkedId]
                if (linkedSeat != null && linkedSeat.isAlive && linkedId !in processedSeatsForCascade) {
                    processedSeatsForCascade.add(linkedId)
                    cascadeEliminatedIds.add(linkedId)
                    queue.addLast(linkedId)
                    logs.add("Seat ${linkedSeat.name ?: linkedSeat.id} died of heartbreak (linked to ${dyingSeat.name ?: dyingSeat.id}).")
                }
            }
        }

        val totalEliminatedIds = directEliminatedIds + cascadeEliminatedIds

        // Step 4: Construct updated seat list with isAlive set to false for eliminated seats
        // and clearing transient night action statuses (WOLF_TARGET, PROTECTED, SAVED_POTION, POISONED)
        val transientStatuses = setOf(
            StatusMarker.WOLF_TARGET,
            StatusMarker.PROTECTED,
            StatusMarker.SAVED_POTION,
            StatusMarker.POISONED
        )

        val updatedSeats = effectiveSeats.map { seat ->
            if (seat.id in totalEliminatedIds) {
                seat.copy(
                    isAlive = false,
                    activeStatuses = seat.activeStatuses - transientStatuses
                )
            } else {
                seat.copy(
                    activeStatuses = seat.activeStatuses - transientStatuses
                )
            }
        }

        return DawnReport(
            eliminatedSeatIds = directEliminatedIds,
            savedSeatIds = savedIds,
            cascadeEliminatedSeatIds = cascadeEliminatedIds,
            updatedSeats = updatedSeats,
            logMessages = logs
        )
    }
}
