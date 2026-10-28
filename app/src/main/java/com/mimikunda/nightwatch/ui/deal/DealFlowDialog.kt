package com.mimikunda.nightwatch.ui.deal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.model.Role
import com.mimikunda.nightwatch.domain.model.RoleDeckConfig
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.domain.settings.GameSettingsRepository
import com.mimikunda.nightwatch.ui.deal.components.HoldToRevealCard
import com.mimikunda.nightwatch.ui.deal.components.MiniTableMap

@Composable
fun DealFlowDialog(
    dealFlowState: DealFlowState,
    seats: List<Seat>,
    edgeCounts: Map<TableEdge, Int>,
    onStartPassToPlayer: (RoleDeckConfig) -> Unit,
    onQuickShuffle: (RoleDeckConfig) -> Unit,
    onPeekRegistered: () -> Unit,
    onNameChanged: (String) -> Unit,
    onPassToNextSeat: () -> Unit,
    onDismiss: () -> Unit
) {
    if (dealFlowState is DealFlowState.Inactive) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            when (dealFlowState) {
                is DealFlowState.ConfigureDeck -> {
                    ConfigureDeckContent(
                        totalSeats = seats.size,
                        initialConfig = dealFlowState.deckConfig,
                        onStartPass = onStartPassToPlayer,
                        onQuickShuffle = onQuickShuffle,
                        onDismiss = onDismiss
                    )
                }
                is DealFlowState.PassToPlayer -> {
                    val currentSeat = seats.getOrNull(dealFlowState.currentSeatIndex)
                    if (currentSeat != null) {
                        val currentRoleId = dealFlowState.shuffledRoleIds.getOrNull(dealFlowState.currentSeatIndex)
                        val settings by GameSettingsRepository.settings.collectAsState()
                        val customRole = settings.roles.find { it.roleId == currentRoleId }
                        val role = Role.ALL_ROLES.find { it.roleId == currentRoleId }
                            ?: customRole?.let { Role(it.roleId, it.displayName, it.team) }

                        PassToPlayerContent(
                            seat = currentSeat,
                            role = role,
                            totalSeats = dealFlowState.totalSeats,
                            hasPeekedRole = dealFlowState.hasPeekedRole,
                            seats = seats,
                            edgeCounts = edgeCounts,
                            onNameChanged = onNameChanged,
                            onPeekRegistered = onPeekRegistered,
                            onPassToNext = onPassToNextSeat
                        )
                    }
                }
                is DealFlowState.ReadyForModerator -> {
                    ReadyForModeratorContent(onDismiss = onDismiss)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ConfigureDeckContent(
    totalSeats: Int,
    initialConfig: RoleDeckConfig,
    onStartPass: (RoleDeckConfig) -> Unit,
    onQuickShuffle: (RoleDeckConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val settings by GameSettingsRepository.settings.collectAsState()
    val enabledSpecialRoles = settings.enabledRoles.filter { it.roleId != "villager" }

    val counts = remember(enabledSpecialRoles) {
        mutableStateMapOf<String, Int>().apply {
            enabledSpecialRoles.forEach { role ->
                val defaultVal = when (role.roleId) {
                    "werewolf" -> 2
                    else -> 1
                }
                put(role.roleId, initialConfig.specialRoleCounts[role.roleId] ?: defaultVal)
            }
        }
    }

    val totalSpecial = counts.values.sum()
    val totalVillagers = (totalSeats - totalSpecial).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.deck_builder_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.deck_builder_subtitle, totalSeats, totalVillagers),
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        enabledSpecialRoles.forEach { role ->
            RoleStepperRow(role.displayName, counts[role.roleId] ?: 1) { newCount ->
                counts[role.roleId] = newCount
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val finalConfig = RoleDeckConfig(specialRoleCounts = counts.toMap())

        Button(
            onClick = { onStartPass(finalConfig) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.pass_phone_button), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onQuickShuffle(finalConfig) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.quick_shuffle_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.btn_cancel))
        }
    }
}

@Composable
private fun RoleStepperRow(
    label: String,
    currentCount: Int,
    onUpdateCount: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = { onUpdateCount((currentCount - 1).coerceAtLeast(0)) },
                    modifier = Modifier.width(32.dp)
                ) {
                    Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "$currentCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                TextButton(
                    onClick = { onUpdateCount(currentCount + 1) },
                    modifier = Modifier.width(32.dp)
                ) {
                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PassToPlayerContent(
    seat: Seat,
    role: Role?,
    totalSeats: Int,
    hasPeekedRole: Boolean,
    seats: List<Seat>,
    edgeCounts: Map<TableEdge, Int>,
    onNameChanged: (String) -> Unit,
    onPeekRegistered: () -> Unit,
    onPassToNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.pass_phone_header, seat.index + 1),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Spatial Mini Table Map
        MiniTableMap(
            seats = seats,
            edgeCounts = edgeCounts,
            currentSeatIndex = seat.index
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hold-to-Reveal Card Component
        HoldToRevealCard(
            seat = seat,
            role = role,
            hasPeekedRole = hasPeekedRole,
            onNameChanged = onNameChanged,
            onPeekRegistered = onPeekRegistered,
            onPassToNext = onPassToNext,
            isLastSeat = seat.index == totalSeats - 1
        )
    }
}

@Composable
private fun ReadyForModeratorContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.deal_complete_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.deal_complete_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.enter_notebook_button))
        }
    }
}
