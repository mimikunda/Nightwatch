package com.mimikunda.nightwatch.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mimikunda.nightwatch.domain.model.RoleTeam
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.settings.CustomStatusEffect

val PitchBlack = Color(0xFF000000)
val TableSurfaceCenter = Color(0xFF111318)
val TableSurfaceBorder = Color(0xFF282C37)
val CardSurfaceDark = Color(0xFF1C1E24)
val CardSurfaceVariantDark = Color(0xFF2B2E38)

// Status Marker Semantic Palette
val StatusWolfTarget = Color(0xFFFF5449) // Crimson
val StatusProtected = Color(0xFF4FC3F7)  // Shield Cyan
val StatusPoisoned = Color(0xFFD05CE3)   // Venom Purple
val StatusSavedPotion = Color(0xFFFFD54F)// Amber Gold
val StatusSeerInspected = Color(0xFF9FA8DA) // Indigo
val StatusMayor = Color(0xFFFFB300)      // Crown Gold
val StatusSilenced = Color(0xFF8E8E93)   // Muted Grey

fun StatusMarker.toColor(): Color = when (this) {
    StatusMarker.WOLF_TARGET -> StatusWolfTarget
    StatusMarker.PROTECTED -> StatusProtected
    StatusMarker.POISONED -> StatusPoisoned
    StatusMarker.SAVED_POTION -> StatusSavedPotion
    StatusMarker.SEER_INSPECTED -> StatusSeerInspected
    StatusMarker.MAYOR -> StatusMayor
    StatusMarker.SILENCED -> StatusSilenced
}

fun StatusMarker.toIcon(): ImageVector = when (this) {
    StatusMarker.WOLF_TARGET -> Icons.Default.CrisisAlert
    StatusMarker.PROTECTED -> Icons.Default.Shield
    StatusMarker.SAVED_POTION -> Icons.Default.Favorite
    StatusMarker.POISONED -> Icons.Default.Warning
    StatusMarker.SEER_INSPECTED -> Icons.Default.Visibility
    StatusMarker.MAYOR -> Icons.Default.Star
    StatusMarker.SILENCED -> Icons.Default.MicOff
}

fun CustomStatusEffect.toIcon(): ImageVector = when (this.iconKey) {
    "CrisisAlert", "Target" -> Icons.Default.CrisisAlert
    "Shield" -> Icons.Default.Shield
    "Favorite", "Heart" -> Icons.Default.Favorite
    "Warning" -> Icons.Default.Warning
    "Visibility", "Eye" -> Icons.Default.Visibility
    "Star" -> Icons.Default.Star
    "MicOff" -> Icons.Default.MicOff
    "Flash" -> Icons.Default.FlashOn
    "Fire" -> Icons.Default.LocalFireDepartment
    else -> Icons.Default.Star
}

fun RoleTeam?.toCardBackgroundColor(): Color = when (this) {
    RoleTeam.WEREWOLVES -> Color(0xFF331416)
    RoleTeam.VILLAGE -> Color(0xFF131F2A)
    RoleTeam.NEUTRAL, RoleTeam.SOLO -> Color(0xFF2B2210)
    null -> CardSurfaceDark
}

fun RoleTeam?.toBorderColor(): Color = when (this) {
    RoleTeam.WEREWOLVES -> StatusWolfTarget.copy(alpha = 0.6f)
    RoleTeam.VILLAGE -> StatusProtected.copy(alpha = 0.5f)
    RoleTeam.NEUTRAL, RoleTeam.SOLO -> StatusMayor.copy(alpha = 0.6f)
    null -> TableSurfaceBorder
}

fun String?.toRoleCardBackgroundColor(team: RoleTeam?): Color = when (this) {
    "werewolf" -> Color(0xFF331416)   // Crimson
    "seer" -> Color(0xFF1B1733)       // Indigo
    "bodyguard" -> Color(0xFF102830)  // Shield Cyan
    "witch" -> Color(0xFF2C1233)      // Venom Purple
    "mayor" -> Color(0xFF332810)      // Crown Gold
    "villager" -> Color(0xFF131F2A)   // Dark Slate
    else -> team.toCardBackgroundColor()
}

fun String?.toRoleBorderColor(team: RoleTeam?): Color = when (this) {
    "werewolf" -> StatusWolfTarget.copy(alpha = 0.8f)
    "seer" -> StatusSeerInspected.copy(alpha = 0.8f)
    "bodyguard" -> StatusProtected.copy(alpha = 0.8f)
    "witch" -> StatusPoisoned.copy(alpha = 0.8f)
    "mayor" -> StatusMayor.copy(alpha = 0.8f)
    "villager" -> Color(0xFF37474F)
    else -> team.toBorderColor()
}
