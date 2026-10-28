package com.mimikunda.nightwatch.ui.notebook.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.ui.theme.toColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedSeatBottomSheet(
    selectedSeat: Seat,
    onToggleStatus: (StatusMarker) -> Unit,
    onToggleAlive: () -> Unit,
    onOpenRolePicker: () -> Unit,
    onOpenNamePicker: () -> Unit,
    onStartLinkMode: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header Row: Seat Name & Vitality Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selectedSeat.name ?: stringResource(R.string.seat_default_name, selectedSeat.index + 1),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (selectedSeat.isAlive) stringResource(R.string.seat_status_alive)
                               else stringResource(R.string.seat_status_dead),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedSeat.isAlive) Color.Green else Color.Red
                    )
                }

                Button(
                    onClick = onToggleAlive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSeat.isAlive) MaterialTheme.colorScheme.error
                        else Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = if (selectedSeat.isAlive) Icons.Default.PersonOff else Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(if (selectedSeat.isAlive) stringResource(R.string.seat_action_kill) else stringResource(R.string.seat_action_revive))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Status Marker FilterChips
            Text(
                text = stringResource(R.string.status_flags_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusMarker.entries.forEach { marker ->
                    val isActive = selectedSeat.activeStatuses.contains(marker)
                    FilterChip(
                        selected = isActive,
                        onClick = { onToggleStatus(marker) },
                        label = {
                            Text(
                                text = marker.name.replace("_", " "),
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = marker.toColor().copy(alpha = 0.8f),
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Role, Name, Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenRolePicker,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(stringResource(R.string.seat_action_assign_role), fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onOpenNamePicker,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(stringResource(R.string.seat_action_edit_name), fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onStartLinkMode()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(stringResource(R.string.seat_action_link), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
