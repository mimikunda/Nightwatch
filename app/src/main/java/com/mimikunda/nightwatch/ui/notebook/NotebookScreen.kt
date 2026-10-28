package com.mimikunda.nightwatch.ui.notebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.model.Role
import com.mimikunda.nightwatch.ui.deal.DealFlowDialog
import com.mimikunda.nightwatch.ui.notebook.components.ModeratorActionDock
import com.mimikunda.nightwatch.ui.notebook.components.SeatNode
import com.mimikunda.nightwatch.ui.notebook.components.SelectedSeatBottomSheet
import com.mimikunda.nightwatch.ui.notebook.components.TableSurfaceCanvas
import com.mimikunda.nightwatch.ui.notebook.geometry.SeatCoordinateSolver
import com.mimikunda.nightwatch.ui.theme.PitchBlack
import kotlin.math.roundToInt

@Composable
fun NotebookScreen(
    viewModel: NotebookViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val seats = uiState.notebookState.seats
    val selectedSeat = seats.find { it.id == uiState.selectedSeatId }

    Scaffold(
        containerColor = PitchBlack,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Spatial Table Canvas Box
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val canvasWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
                val canvasHeightPx = with(LocalDensity.current) { maxHeight.toPx() }
                val marginPx = with(LocalDensity.current) { 56.dp.toPx() }

                // Compute seat center Offsets
                val seatCenters = remember(seats, uiState.edgeCounts, canvasWidthPx, canvasHeightPx) {
                    seats.associate { seat ->
                        seat.id to SeatCoordinateSolver.resolveSeatCenter(
                            seat = seat,
                            edgeCounts = uiState.edgeCounts,
                            canvasWidth = canvasWidthPx,
                            canvasHeight = canvasHeightPx,
                            marginPx = marginPx
                        )
                    }
                }

                // Background Canvas with Table & Links
                TableSurfaceCanvas(
                    seats = seats,
                    seatCenters = seatCenters,
                    isLayoutEditUnlocked = uiState.isLayoutEditUnlocked,
                    onToggleLayoutLock = viewModel::onToggleLayoutLock
                )

                // Seat Overlay Nodes
                seats.forEach { seat ->
                    val center = seatCenters[seat.id] ?: Offset.Zero

                    val nodeWidthPx = with(LocalDensity.current) { 90.dp.toPx() }
                    val nodeHeightPx = with(LocalDensity.current) { 55.dp.toPx() }

                    val xOffsetPx = (center.x - nodeWidthPx / 2f).roundToInt()
                    val yOffsetPx = (center.y - nodeHeightPx / 2f).roundToInt()

                    Box(
                        modifier = Modifier.offset { IntOffset(xOffsetPx, yOffsetPx) }
                    ) {
                        SeatNode(
                            seat = seat,
                            isSelected = seat.id == uiState.selectedSeatId,
                            isLayoutEditUnlocked = uiState.isLayoutEditUnlocked,
                            currentCenterPx = center,
                            onSelect = { viewModel.onSeatClick(seat.id) },
                            onQuickToggleAlive = { viewModel.onQuickToggleAlive(seat.id) },
                            onDrag = { normX, normY -> viewModel.onSeatDrag(seat.id, normX, normY) },
                            canvasWidth = canvasWidthPx,
                            canvasHeight = canvasHeightPx
                        )
                    }
                }
            }

            // Bottom Moderator Dock (Table Setup & Quick Actions)
            ModeratorActionDock(
                selectedSeat = selectedSeat,
                isLinkSelectionMode = uiState.isLinkSelectionMode,
                isLayoutEditUnlocked = uiState.isLayoutEditUnlocked,
                edgeCounts = uiState.edgeCounts,
                onCancelLinkMode = viewModel::onCancelLinkMode,
                onClearNightMarkers = viewModel::onClearNightMarkers,
                onOpenDealFlow = viewModel::onOpenDealFlow,
                onUpdateEdgeCount = viewModel::onUpdateEdgeCount
            )
        }
    }

    // Dismissible Selected Person Modal Bottom Sheet
    if (selectedSeat != null && !uiState.isLinkSelectionMode) {
        SelectedSeatBottomSheet(
            selectedSeat = selectedSeat,
            onToggleStatus = viewModel::onToggleStatus,
            onToggleAlive = viewModel::onToggleAliveSelected,
            onOpenRolePicker = viewModel::onOpenRolePicker,
            onOpenNamePicker = viewModel::onOpenNamePicker,
            onStartLinkMode = viewModel::onStartLinkMode,
            onDismiss = viewModel::onDeselectSeat
        )
    }

    // Role Deck Builder & Cardless Deal Flow Dialog
    DealFlowDialog(
        dealFlowState = uiState.dealFlowState,
        seats = seats,
        edgeCounts = uiState.edgeCounts,
        onStartPassToPlayer = viewModel::onStartPassToPlayer,
        onQuickShuffle = viewModel::onQuickShuffle,
        onPeekRegistered = viewModel::onPeekRegistered,
        onNameChanged = viewModel::onPassNameChanged,
        onPassToNextSeat = viewModel::onPassToNextSeat,
        onDismiss = viewModel::onCloseDealFlow
    )

    // Role Picker Dialog
    if (uiState.isRolePickerOpen && selectedSeat != null) {
        RolePickerDialog(
            currentRoleId = selectedSeat.roleId,
            onSelectRole = viewModel::onUpdateSeatRole,
            onDismiss = viewModel::onCloseRolePicker
        )
    }

    // Name Picker Dialog
    if (uiState.isNamePickerOpen && selectedSeat != null) {
        NameEditDialog(
            currentName = selectedSeat.name ?: "",
            onSaveName = viewModel::onUpdateSeatName,
            onDismiss = viewModel::onCloseNamePicker
        )
    }
}

@Composable
private fun RolePickerDialog(
    currentRoleId: String?,
    onSelectRole: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_select_role_title)) },
        text = {
            LazyColumn {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRole(null) }
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentRoleId == null) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_role_unassigned),
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                items(Role.ALL_ROLES) { role ->
                    val isSelected = currentRoleId == role.roleId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRole(role.roleId) }
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = role.displayName, fontWeight = FontWeight.SemiBold)
                            Text(text = role.team.name, fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}

@Composable
private fun NameEditDialog(
    currentName: String,
    onSaveName: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var nameText by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_name_title)) },
        text = {
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text(stringResource(R.string.dialog_player_name_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSaveName(nameText.ifBlank { null })
                }
            ) {
                Text(stringResource(R.string.btn_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}
