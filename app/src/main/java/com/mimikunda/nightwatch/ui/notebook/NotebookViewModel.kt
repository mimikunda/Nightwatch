package com.mimikunda.nightwatch.ui.notebook

import androidx.lifecycle.ViewModel
import com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable
import com.mimikunda.nightwatch.domain.model.RoleDeckConfig
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.domain.state.NotebookState
import com.mimikunda.nightwatch.ui.deal.DealFlowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NotebookUiState(
    val notebookState: NotebookState = NotebookState(
        seats = PerimeterSeatingTable.build(
            topCount = 2,
            rightCount = 1,
            bottomCount = 2,
            leftCount = 1,
            floatingCount = 0
        ).seats
    ),
    val selectedSeatId: String? = null,
    val isLinkSelectionMode: Boolean = false,
    val isLayoutEditUnlocked: Boolean = false,
    val isRolePickerOpen: Boolean = false,
    val isNamePickerOpen: Boolean = false,
    val dealFlowState: DealFlowState = DealFlowState.Inactive,
    val edgeCounts: Map<TableEdge, Int> = mapOf(
        TableEdge.TOP to 2,
        TableEdge.RIGHT to 1,
        TableEdge.BOTTOM to 2,
        TableEdge.LEFT to 1,
        TableEdge.FLOATING to 0
    )
)

class NotebookViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotebookUiState())
    val uiState: StateFlow<NotebookUiState> = _uiState.asStateFlow()

    fun onSeatClick(seatId: String) {
        _uiState.update { currentState ->
            if (currentState.isLinkSelectionMode && currentState.selectedSeatId != null) {
                val sourceId = currentState.selectedSeatId
                if (sourceId != seatId) {
                    val updatedState = currentState.notebookState.linkSeats(sourceId, seatId)
                    currentState.copy(
                        notebookState = updatedState,
                        isLinkSelectionMode = false
                    )
                } else {
                    currentState.copy(isLinkSelectionMode = false)
                }
            } else {
                currentState.copy(
                    selectedSeatId = if (currentState.selectedSeatId == seatId) null else seatId
                )
            }
        }
    }

    fun onQuickToggleAlive(seatId: String) {
        _uiState.update { currentState ->
            val seat = currentState.notebookState.seats.find { it.id == seatId }
            if (seat != null) {
                val updatedState = currentState.notebookState.setLifeStatus(seatId, !seat.isAlive)
                currentState.copy(notebookState = updatedState)
            } else {
                currentState
            }
        }
    }

    fun onToggleStatus(marker: StatusMarker) {
        _uiState.update { currentState ->
            val selectedId = currentState.selectedSeatId ?: return@update currentState
            val updatedState = currentState.notebookState.toggleStatus(selectedId, marker)
            currentState.copy(notebookState = updatedState)
        }
    }

    fun onToggleCustomStatus(effectId: String) {
        _uiState.update { currentState ->
            val selectedId = currentState.selectedSeatId ?: return@update currentState
            val standardMarker = StatusMarker.entries.find { it.name == effectId }
            val updatedState = if (standardMarker != null) {
                currentState.notebookState.toggleStatus(selectedId, standardMarker)
            } else {
                currentState.notebookState.toggleCustomStatus(selectedId, effectId)
            }
            currentState.copy(notebookState = updatedState)
        }
    }

    fun onToggleAliveSelected() {
        _uiState.update { currentState ->
            val selectedId = currentState.selectedSeatId ?: return@update currentState
            val seat = currentState.notebookState.seats.find { it.id == selectedId } ?: return@update currentState
            val updatedState = currentState.notebookState.setLifeStatus(selectedId, !seat.isAlive)
            currentState.copy(notebookState = updatedState)
        }
    }

    fun onUpdateSeatName(name: String?) {
        _uiState.update { currentState ->
            val selectedId = currentState.selectedSeatId ?: return@update currentState
            val updatedState = currentState.notebookState.updateSeatName(selectedId, name)
            currentState.copy(notebookState = updatedState, isNamePickerOpen = false)
        }
    }

    fun onUpdateSeatRole(roleId: String?) {
        _uiState.update { currentState ->
            val selectedId = currentState.selectedSeatId ?: return@update currentState
            val updatedState = currentState.notebookState.updateSeatRole(selectedId, roleId)
            currentState.copy(notebookState = updatedState, isRolePickerOpen = false)
        }
    }

    fun onSeatDrag(seatId: String, normX: Float, normY: Float) {
        _uiState.update { currentState ->
            val updatedState = currentState.notebookState.updateSeatCustomPosition(seatId, normX, normY)
            currentState.copy(notebookState = updatedState)
        }
    }

    fun onStartLinkMode() {
        _uiState.update { it.copy(isLinkSelectionMode = true) }
    }

    fun onCancelLinkMode() {
        _uiState.update { it.copy(isLinkSelectionMode = false) }
    }

    fun onToggleLayoutLock() {
        _uiState.update { it.copy(isLayoutEditUnlocked = !it.isLayoutEditUnlocked) }
    }

    fun onDeselectSeat() {
        _uiState.update { it.copy(selectedSeatId = null, isLinkSelectionMode = false) }
    }

    fun onClearNightMarkers() {
        _uiState.update { currentState ->
            val updatedState = currentState.notebookState.clearNightMarkers()
            currentState.copy(notebookState = updatedState)
        }
    }

    fun onUpdateEdgeCount(edge: TableEdge, newCount: Int) {
        _uiState.update { currentState ->
            val newEdgeCounts = currentState.edgeCounts.toMutableMap()
            newEdgeCounts[edge] = newCount
            val updatedState = currentState.notebookState.updateEdgeCounts(
                topCount = newEdgeCounts[TableEdge.TOP] ?: 0,
                rightCount = newEdgeCounts[TableEdge.RIGHT] ?: 0,
                bottomCount = newEdgeCounts[TableEdge.BOTTOM] ?: 0,
                leftCount = newEdgeCounts[TableEdge.LEFT] ?: 0,
                floatingCount = newEdgeCounts[TableEdge.FLOATING] ?: 0
            )
            currentState.copy(
                edgeCounts = newEdgeCounts,
                notebookState = updatedState
            )
        }
    }

    fun onOpenRolePicker() {
        _uiState.update { it.copy(isRolePickerOpen = true) }
    }

    fun onCloseRolePicker() {
        _uiState.update { it.copy(isRolePickerOpen = false) }
    }

    fun onOpenNamePicker() {
        _uiState.update { it.copy(isNamePickerOpen = true) }
    }

    fun onCloseNamePicker() {
        _uiState.update { it.copy(isNamePickerOpen = false) }
    }

    // Deal Flow Functions
    fun onOpenDealFlow() {
        _uiState.update { it.copy(dealFlowState = DealFlowState.ConfigureDeck()) }
    }

    fun onCloseDealFlow() {
        _uiState.update { it.copy(dealFlowState = DealFlowState.Inactive) }
    }

    fun onStartPassToPlayer(deckConfig: RoleDeckConfig) {
        _uiState.update { currentState ->
            val totalSeats = currentState.notebookState.seats.size
            val shuffled = deckConfig.buildShuffledDeck(totalSeats)
            currentState.copy(
                dealFlowState = DealFlowState.PassToPlayer(
                    currentSeatIndex = 0,
                    totalSeats = totalSeats,
                    hasPeekedRole = false,
                    shuffledRoleIds = shuffled
                )
            )
        }
    }

    fun onQuickShuffle(deckConfig: RoleDeckConfig) {
        _uiState.update { currentState ->
            val totalSeats = currentState.notebookState.seats.size
            val shuffled = deckConfig.buildShuffledDeck(totalSeats)
            val dealtState = currentState.notebookState.dealRoles(shuffled)
            currentState.copy(
                notebookState = dealtState,
                dealFlowState = DealFlowState.Inactive
            )
        }
    }

    fun onPeekRegistered() {
        _uiState.update { currentState ->
            val flow = currentState.dealFlowState
            if (flow is DealFlowState.PassToPlayer) {
                currentState.copy(dealFlowState = flow.copy(hasPeekedRole = true))
            } else {
                currentState
            }
        }
    }

    fun onPassNameChanged(newName: String) {
        _uiState.update { currentState ->
            val flow = currentState.dealFlowState
            if (flow is DealFlowState.PassToPlayer) {
                val seat = currentState.notebookState.seats.getOrNull(flow.currentSeatIndex)
                if (seat != null) {
                    val updatedNotebook = currentState.notebookState.updateSeatName(seat.id, newName.ifBlank { null })
                    currentState.copy(
                        notebookState = updatedNotebook,
                        dealFlowState = flow.copy(draftedName = newName)
                    )
                } else {
                    currentState
                }
            } else {
                currentState
            }
        }
    }

    fun onPassToNextSeat() {
        _uiState.update { currentState ->
            val flow = currentState.dealFlowState
            if (flow is DealFlowState.PassToPlayer) {
                if (flow.currentSeatIndex < flow.totalSeats - 1) {
                    currentState.copy(
                        dealFlowState = flow.copy(
                            currentSeatIndex = flow.currentSeatIndex + 1,
                            draftedName = "",
                            hasPeekedRole = false
                        )
                    )
                } else {
                    // All seats processed -> Apply dealt roles to notebookState
                    val dealtState = currentState.notebookState.dealRoles(flow.shuffledRoleIds)
                    currentState.copy(
                        notebookState = dealtState,
                        dealFlowState = DealFlowState.ReadyForModerator
                    )
                }
            } else {
                currentState
            }
        }
    }
}
