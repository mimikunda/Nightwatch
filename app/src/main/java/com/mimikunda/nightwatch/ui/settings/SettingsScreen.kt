package com.mimikunda.nightwatch.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TableBar
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
import com.mimikunda.nightwatch.ui.settings.components.TableShapePicker

enum class SettingsSubmenu {
    Main,
    RolesAndEffects,
    TableGeometry
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    notebookViewModel: NotebookViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    var currentSubmenu by remember { mutableStateOf(SettingsSubmenu.Main) }

    var isCreateRoleDialogOpen by remember { mutableStateOf(false) }
    var isResetGameDialogOpen by remember { mutableStateOf(false) }
    var isResetSettingsDialogOpen by remember { mutableStateOf(false) }

    // Predictive Back / System Back Handler for Submenus
    BackHandler(enabled = currentSubmenu != SettingsSubmenu.Main) {
        currentSubmenu = SettingsSubmenu.Main
    }

    AnimatedContent(
        targetState = currentSubmenu,
        label = "settingsSubmenuAnimation"
    ) { submenu ->
        when (submenu) {
            SettingsSubmenu.Main -> {
                MainSettingsHub(
                    activeRolesCount = settings.enabledRoles.size,
                    activeEffectsCount = settings.enabledEffects.size,
                    currentShapeName = settings.tableShape.name.lowercase().replaceFirstChar { it.uppercase() },
                    onNavigateToRoles = { currentSubmenu = SettingsSubmenu.RolesAndEffects },
                    onNavigateToTable = { currentSubmenu = SettingsSubmenu.TableGeometry },
                    onClearNightMarkers = notebookViewModel::onClearNightMarkers,
                    onOpenResetGame = { isResetGameDialogOpen = true },
                    onOpenResetSettings = { isResetSettingsDialogOpen = true }
                )
            }
            SettingsSubmenu.RolesAndEffects -> {
                RolesAndEffectsSubmenu(
                    roles = settings.roles,
                    effects = settings.effects,
                    onBack = { currentSubmenu = SettingsSubmenu.Main },
                    onToggleRole = viewModel::toggleRole,
                    onToggleEffect = viewModel::toggleEffect,
                    onDeleteCustomRole = viewModel::deleteCustomRole,
                    onOpenCreateRole = { isCreateRoleDialogOpen = true }
                )
            }
            SettingsSubmenu.TableGeometry -> {
                TableGeometrySubmenu(
                    selectedShape = settings.tableShape,
                    onBack = { currentSubmenu = SettingsSubmenu.Main },
                    onSelectShape = viewModel::updateTableShape
                )
            }
        }
    }

    // Dialogs
    if (isCreateRoleDialogOpen) {
        CustomRoleCreatorDialog(
            onAddRole = { name, team, colorArgb, associatedEffect ->
                viewModel.addCustomRole(name, team, colorArgb, associatedEffect)
                isCreateRoleDialogOpen = false
            },
            onDismiss = { isCreateRoleDialogOpen = false }
        )
    }

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
private fun MainSettingsHub(
    activeRolesCount: Int,
    activeEffectsCount: Int,
    currentShapeName: String,
    onNavigateToRoles: () -> Unit,
    onNavigateToTable: () -> Unit,
    onClearNightMarkers: () -> Unit,
    onOpenResetGame: () -> Unit,
    onOpenResetSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        // Submenu Card 1: Role & Effect Management
        item {
            SubmenuNavigationCard(
                icon = Icons.Default.Badge,
                title = stringResource(R.string.submenu_roles_title),
                subtitle = "$activeRolesCount Roles • $activeEffectsCount Effects Active",
                onClick = onNavigateToRoles
            )
        }

        // Submenu Card 2: Table Shape & Geometry
        item {
            SubmenuNavigationCard(
                icon = Icons.Default.TableBar,
                title = stringResource(R.string.submenu_table_title),
                subtitle = "Current Shape: $currentShapeName",
                onClick = onNavigateToTable
            )
        }

        // Danger Zone Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
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
                    OutlinedButton(
                        onClick = onClearNightMarkers,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(stringResource(R.string.btn_clear_night_markers))
                    }

                    Button(
                        onClick = onOpenResetGame,
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

                    Button(
                        onClick = onOpenResetSettings,
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
}

@Composable
private fun SubmenuNavigationCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun RolesAndEffectsSubmenu(
    roles: List<CustomRole>,
    effects: List<CustomStatusEffect>,
    onBack: () -> Unit,
    onToggleRole: (String) -> Unit,
    onToggleEffect: (String) -> Unit,
    onDeleteCustomRole: (String) -> Unit,
    onOpenCreateRole: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Submenu Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.submenu_roles_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.submenu_roles_desc),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Section 1: Active Roles & Add Custom Role
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Roles",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(onClick = onOpenCreateRole) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Add Role", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(roles) { role ->
            RoleToggleCard(
                role = role,
                onToggle = { onToggleRole(role.roleId) },
                onDelete = if (role.isCustom) {
                    { onDeleteCustomRole(role.roleId) }
                } else null
            )
        }

        // Section 2: Active Status Effects
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Active Status Effects",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(effects) { effect ->
            EffectToggleCard(
                effect = effect,
                onToggle = { onToggleEffect(effect.id) }
            )
        }
    }
}

@Composable
private fun TableGeometrySubmenu(
    selectedShape: com.mimikunda.nightwatch.domain.model.TableShape,
    onBack: () -> Unit,
    onSelectShape: (com.mimikunda.nightwatch.domain.model.TableShape) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(R.string.submenu_table_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.submenu_table_desc),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TableShapePicker(
            selectedShape = selectedShape,
            onSelectShape = onSelectShape
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
