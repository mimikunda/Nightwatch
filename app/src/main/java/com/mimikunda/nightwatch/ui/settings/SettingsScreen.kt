package com.mimikunda.nightwatch.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.settings.CustomRole
import com.mimikunda.nightwatch.domain.settings.CustomStatusEffect
import com.mimikunda.nightwatch.ui.notebook.NotebookViewModel
import com.mimikunda.nightwatch.ui.settings.components.CustomRoleCreatorDialog

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    notebookViewModel: NotebookViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    var isCreateRoleDialogOpen by remember { mutableStateOf(false) }
    var isResetGameDialogOpen by remember { mutableStateOf(false) }
    var isResetSettingsDialogOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = stringResource(R.string.settings_placeholder_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.settings_placeholder_subtitle),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Section 1: Role Toggles & Custom Roles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Roles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(onClick = { isCreateRoleDialogOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Add Role", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(settings.roles) { role ->
            RoleToggleCard(
                role = role,
                onToggle = { viewModel.toggleRole(role.roleId) },
                onDelete = if (role.isCustom) {
                    { viewModel.deleteCustomRole(role.roleId) }
                } else null
            )
        }

        // Section 2: Status Effect Toggles
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Active Status Effects",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(settings.effects) { effect ->
            EffectToggleCard(
                effect = effect,
                onToggle = { viewModel.toggleEffect(effect.id) }
            )
        }

        // Section 3: Danger Zone Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.danger_zone_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.danger_zone_subtitle),
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF221113))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Clear Night Markers Button
                    OutlinedButton(
                        onClick = { notebookViewModel.onClearNightMarkers() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(stringResource(R.string.btn_clear_night_markers))
                    }

                    // Reset Current Game Button
                    Button(
                        onClick = { isResetGameDialogOpen = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = stringResource(R.string.btn_reset_game),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reset Settings Button
                    Button(
                        onClick = { isResetSettingsDialogOpen = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = stringResource(R.string.btn_reset_settings),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Custom Role Creator Dialog
    if (isCreateRoleDialogOpen) {
        CustomRoleCreatorDialog(
            onAddRole = { name, team, colorArgb, associatedEffect ->
                viewModel.addCustomRole(name, team, colorArgb, associatedEffect)
                isCreateRoleDialogOpen = false
            },
            onDismiss = { isCreateRoleDialogOpen = false }
        )
    }

    // Reset Game Dialog
    if (isResetGameDialogOpen) {
        AlertDialog(
            onDismissRequest = { isResetGameDialogOpen = false },
            title = { Text(stringResource(R.string.dialog_reset_game_title)) },
            text = { Text(stringResource(R.string.dialog_reset_game_msg)) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        notebookViewModel.resetGame()
                        isResetGameDialogOpen = false
                    }
                ) {
                    Text(stringResource(R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { isResetGameDialogOpen = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    // Reset Settings Dialog
    if (isResetSettingsDialogOpen) {
        AlertDialog(
            onDismissRequest = { isResetSettingsDialogOpen = false },
            title = { Text(stringResource(R.string.dialog_reset_settings_title)) },
            text = { Text(stringResource(R.string.dialog_reset_settings_msg)) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        viewModel.resetToDefaults()
                        isResetSettingsDialogOpen = false
                    }
                ) {
                    Text(stringResource(R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { isResetSettingsDialogOpen = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun RoleToggleCard(
    role: CustomRole,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (role.isEnabled) Color.DarkGray.copy(alpha = 0.4f) else Color.DarkGray.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(role.colorArgb))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = role.displayName,
                        fontWeight = FontWeight.SemiBold,
                        color = if (role.isEnabled) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                    Text(
                        text = role.team.name,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Custom Role",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Switch(
                    checked = role.isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }
        }
    }
}

@Composable
private fun EffectToggleCard(
    effect: CustomStatusEffect,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (effect.isEnabled) Color.DarkGray.copy(alpha = 0.4f) else Color.DarkGray.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(effect.colorArgb))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = effect.name,
                    fontWeight = FontWeight.SemiBold,
                    color = if (effect.isEnabled) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
            }

            Switch(
                checked = effect.isEnabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
