package com.mimikunda.nightwatch.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.domain.model.RoleTeam
import com.mimikunda.nightwatch.domain.settings.CustomStatusEffect

val PresetColors = listOf(
    Color(0xFFFF5449), // Crimson
    Color(0xFF4FC3F7), // Shield Cyan
    Color(0xFFD05CE3), // Venom Purple
    Color(0xFFFFD54F), // Amber Gold
    Color(0xFF9FA8DA), // Indigo
    Color(0xFFFFB300), // Crown Gold
    Color(0xFFFF4081), // Pink/Heart
    Color(0xFFFF9800), // Orange
    Color(0xFF26A69A), // Teal
    Color(0xFF8E8E93)  // Muted Grey
)

val PresetIcons = listOf(
    "Star" to Icons.Default.Star,
    "Shield" to Icons.Default.Shield,
    "Heart" to Icons.Default.Favorite,
    "Flash" to Icons.Default.FlashOn,
    "Eye" to Icons.Default.Visibility,
    "Warning" to Icons.Default.Warning,
    "Target" to Icons.Default.CrisisAlert,
    "Fire" to Icons.Default.LocalFireDepartment
)

@Composable
fun CustomRoleCreatorDialog(
    onAddRole: (
        name: String,
        team: RoleTeam,
        colorArgb: Long,
        associatedEffect: CustomStatusEffect?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var nameText by remember { mutableStateOf("") }
    var selectedTeam by remember { mutableStateOf(RoleTeam.VILLAGE) }
    var selectedColor by remember { mutableStateOf(PresetColors[0]) }

    var createEffect by remember { mutableStateOf(false) }
    var effectNameText by remember { mutableStateOf("") }
    var selectedIconPair by remember { mutableStateOf(PresetIcons[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Role") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Role Name Input
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Role Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Team Selector Chips
                Text("Team Alignment:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RoleTeam.entries.forEach { team ->
                        FilterChip(
                            selected = selectedTeam == team,
                            onClick = { selectedTeam = team },
                            label = { Text(team.name, fontSize = 11.sp) }
                        )
                    }
                }

                // Accent Color Selector
                Text("Accent Color:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PresetColors.forEach { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else Modifier
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }

                // Optional Associated Effect
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { createEffect = !createEffect }
                ) {
                    Checkbox(
                        checked = createEffect,
                        onCheckedChange = { createEffect = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Associated Status Effect", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                if (createEffect) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = effectNameText,
                            onValueChange = { effectNameText = it },
                            label = { Text("Effect Name (e.g., In Love)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Effect Icon:", style = MaterialTheme.typography.labelSmall)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PresetIcons.forEach { iconPair ->
                                val isSelected = selectedIconPair.first == iconPair.first
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray.copy(alpha = 0.4f))
                                        .clickable { selectedIconPair = iconPair },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconPair.second,
                                        contentDescription = iconPair.first,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.LightGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = nameText.isNotBlank(),
                onClick = {
                    val associatedEffect = if (createEffect && effectNameText.isNotBlank()) {
                        CustomStatusEffect(
                            id = "custom_effect_${System.currentTimeMillis()}",
                            name = effectNameText,
                            colorArgb = selectedColor.toArgb().toLong(),
                            iconKey = selectedIconPair.first,
                            isCustom = true
                        )
                    } else null

                    onAddRole(
                        nameText,
                        selectedTeam,
                        selectedColor.toArgb().toLong(),
                        associatedEffect
                    )
                }
            ) {
                Text("Save Role")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
