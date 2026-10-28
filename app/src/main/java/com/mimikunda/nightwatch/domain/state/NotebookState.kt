package com.mimikunda.nightwatch.domain.state

import com.mimikunda.nightwatch.domain.model.GameMode
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.StatusMarker

data class NotebookState(
    val seats: List<Seat> = emptyList(),
    val gameMode: GameMode = GameMode.NOTEBOOK
) {
    /**
     * Toggles a status marker on a seat. If present, removes it; if absent, adds it.
     */
    fun toggleStatus(seatId: String, marker: StatusMarker): NotebookState {
        return copy(
            seats = seats.map { seat ->
                if (seat.id == seatId) {
                    val newStatuses = if (seat.activeStatuses.contains(marker)) {
                        seat.activeStatuses - marker
                    } else {
                        seat.activeStatuses + marker
                    }
                    seat.copy(activeStatuses = newStatuses)
                } else {
                    seat
                }
            }
        )
    }

    /**
     * Explicitly adds a status marker to a seat.
     */
    fun addStatus(seatId: String, marker: StatusMarker): NotebookState {
        return copy(
            seats = seats.map { seat ->
                if (seat.id == seatId) {
                    seat.copy(activeStatuses = seat.activeStatuses + marker)
                } else {
                    seat
                }
            }
        )
    }

    /**
     * Explicitly removes a status marker from a seat.
     */
    fun removeStatus(seatId: String, marker: StatusMarker): NotebookState {
        return copy(
            seats = seats.map { seat ->
                if (seat.id == seatId) {
                    seat.copy(activeStatuses = seat.activeStatuses - marker)
                } else {
                    seat
                }
            }
        )
    }

    /**
     * Sets the alive status of a seat.
     */
    fun setLifeStatus(seatId: String, isAlive: Boolean): NotebookState {
        return copy(
            seats = seats.map { seat ->
                if (seat.id == seatId) {
                    seat.copy(isAlive = isAlive)
                } else {
                    seat
                }
            }
        )
    }

    /**
     * Establishes a symmetric link between seatA and seatB (e.g. Lovers, Siblings).
     */
    fun linkSeats(seatAId: String, seatBId: String): NotebookState {
        if (seatAId == seatBId) return this
        return copy(
            seats = seats.map { seat ->
                when (seat.id) {
                    seatAId -> seat.copy(linkedSeatIds = seat.linkedSeatIds + seatBId)
                    seatBId -> seat.copy(linkedSeatIds = seat.linkedSeatIds + seatAId)
                    else -> seat
                }
            }
        )
    }

    /**
     * Removes the symmetric link between seatA and seatB.
     */
    fun unlinkSeats(seatAId: String, seatBId: String): NotebookState {
        return copy(
            seats = seats.map { seat ->
                when (seat.id) {
                    seatAId -> seat.copy(linkedSeatIds = seat.linkedSeatIds - seatBId)
                    seatBId -> seat.copy(linkedSeatIds = seat.linkedSeatIds - seatAId)
                    else -> seat
                }
            }
        )
    }

    /**
     * Updates seat name.
     */
    fun updateSeatName(seatId: String, name: String?): NotebookState {
        return copy(
            seats = seats.map { seat ->
                if (seat.id == seatId) {
                    seat.copy(name = name)
                } else {
                    seat
                }
            }
        )
    }

    /**
     * Updates seat role ID.
     */
    fun updateSeatRole(seatId: String, roleId: String?): NotebookState {
        return copy(
            seats = seats.map { seat ->
                if (seat.id == seatId) {
                    seat.copy(roleId = roleId)
                } else {
                    seat
                }
            }
        )
    }

    /**
     * Deals a list of role IDs to seats in sequential index order,
     * resetting vitality to true and clearing active status markers.
     */
    fun dealRoles(shuffledRoleIds: List<String>): NotebookState {
        return copy(
            seats = seats.mapIndexed { index, seat ->
                val assignedRoleId = shuffledRoleIds.getOrNull(index) ?: seat.roleId
                seat.copy(
                    roleId = assignedRoleId,
                    isAlive = true,
                    activeStatuses = emptySet()
                )
            }
        )
    }

    /**
     * Updates seat custom normalized 2D coordinates for chair dragging/nudging.
     */
    fun updateSeatCustomPosition(seatId: String, normX: Float?, normY: Float?): NotebookState {
        return copy(
            seats = seats.map { seat ->
                if (seat.id == seatId) {
                    seat.copy(
                        position = seat.position.copy(
                            customNormalizedX = normX,
                            customNormalizedY = normY
                        )
                    )
                } else {
                    seat
                }
            }
        )
    }

    /**
     * Strips transient night markers while preserving MAYOR, roles, and alive status.
     */
    fun clearNightMarkers(): NotebookState {
        val nightMarkers = setOf(
            StatusMarker.WOLF_TARGET,
            StatusMarker.PROTECTED,
            StatusMarker.SAVED_POTION,
            StatusMarker.POISONED,
            StatusMarker.SEER_INSPECTED,
            StatusMarker.SILENCED
        )
        return copy(
            seats = seats.map { seat ->
                seat.copy(activeStatuses = seat.activeStatuses - nightMarkers)
            }
        )
    }

    /**
     * Rebuilds perimeter seating with new edge counts while preserving seat data.
     */
    fun updateEdgeCounts(
        topCount: Int,
        rightCount: Int,
        bottomCount: Int,
        leftCount: Int,
        floatingCount: Int
    ): NotebookState {
        val newTable = com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable.build(
            topCount = topCount,
            rightCount = rightCount,
            bottomCount = bottomCount,
            leftCount = leftCount,
            floatingCount = floatingCount
        )

        val updatedSeats = newTable.seats.map { newSeat ->
            val existing = seats.getOrNull(newSeat.index)
            if (existing != null) {
                newSeat.copy(
                    name = existing.name,
                    roleId = existing.roleId,
                    isAlive = existing.isAlive,
                    activeStatuses = existing.activeStatuses,
                    linkedSeatIds = existing.linkedSeatIds,
                    position = newSeat.position.copy(
                        customNormalizedX = existing.position.customNormalizedX,
                        customNormalizedY = existing.position.customNormalizedY
                    )
                )
            } else {
                newSeat
            }
        }

        return copy(seats = updatedSeats)
    }
}
