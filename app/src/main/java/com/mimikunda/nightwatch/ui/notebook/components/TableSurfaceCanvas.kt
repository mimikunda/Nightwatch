package com.mimikunda.nightwatch.ui.notebook.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.model.Role
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.model.TableShape
import com.mimikunda.nightwatch.domain.settings.GameSettingsRepository
import com.mimikunda.nightwatch.ui.theme.StatusPoisoned
import com.mimikunda.nightwatch.ui.theme.StatusProtected
import com.mimikunda.nightwatch.ui.theme.StatusSeerInspected
import com.mimikunda.nightwatch.ui.theme.StatusWolfTarget
import com.mimikunda.nightwatch.ui.theme.TableSurfaceBorder
import com.mimikunda.nightwatch.ui.theme.TableSurfaceCenter
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TableSurfaceCanvas(
    seats: List<Seat>,
    seatCenters: Map<String, Offset>,
    isLayoutEditUnlocked: Boolean,
    onToggleLayoutLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by GameSettingsRepository.settings.collectAsState()
    val tableShape = settings.tableShape

    Box(modifier = modifier.fillMaxSize()) {
        // Background Table Canvas & Link Paths
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw central table boundary matching tableShape
            val tableInsetX = canvasW * 0.18f
            val tableInsetY = canvasH * 0.18f
            val tableWidth = canvasW - (tableInsetX * 2f)
            val tableHeight = canvasH - (tableInsetY * 2f)

            if (tableWidth > 0 && tableHeight > 0) {
                when (tableShape) {
                    TableShape.RECTANGLE -> {
                        drawRoundRect(
                            color = TableSurfaceCenter,
                            topLeft = Offset(tableInsetX, tableInsetY),
                            size = Size(tableWidth, tableHeight),
                            cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx())
                        )
                        drawRoundRect(
                            color = TableSurfaceBorder,
                            topLeft = Offset(tableInsetX, tableInsetY),
                            size = Size(tableWidth, tableHeight),
                            cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    TableShape.CIRCLE, TableShape.OVAL -> {
                        drawOval(
                            color = TableSurfaceCenter,
                            topLeft = Offset(tableInsetX, tableInsetY),
                            size = Size(tableWidth, tableHeight)
                        )
                        drawOval(
                            color = TableSurfaceBorder,
                            topLeft = Offset(tableInsetX, tableInsetY),
                            size = Size(tableWidth, tableHeight),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    TableShape.HEXAGON, TableShape.OCTAGON -> {
                        val sides = if (tableShape == TableShape.HEXAGON) 6 else 8
                        val path = Path()
                        val rx = tableWidth / 2f
                        val ry = tableHeight / 2f
                        val cx = canvasW / 2f
                        val cy = canvasH / 2f

                        for (i in 0 until sides) {
                            val angle = (2.0 * Math.PI * i / sides) - (Math.PI / 2.0)
                            val x = (cx + rx * cos(angle)).toFloat()
                            val y = (cy + ry * sin(angle)).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()

                        drawPath(path, color = TableSurfaceCenter)
                        drawPath(path, color = TableSurfaceBorder, style = Stroke(width = 2.dp.toPx()))
                    }
                }
            }

            // 2. Draw dashed glowing link lines between linked seats (Lovers)
            val drawnLinks = mutableSetOf<Pair<String, String>>()
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)

            for (seat in seats) {
                val startOffset = seatCenters[seat.id] ?: continue
                for (linkedId in seat.linkedSeatIds) {
                    val endOffset = seatCenters[linkedId] ?: continue

                    val pairKey = if (seat.id < linkedId) Pair(seat.id, linkedId) else Pair(linkedId, seat.id)
                    if (pairKey in drawnLinks) continue
                    drawnLinks.add(pairKey)

                    val path = Path().apply {
                        moveTo(startOffset.x, startOffset.y)
                        lineTo(endOffset.x, endOffset.y)
                    }

                    // Glow line
                    drawPath(
                        path = path,
                        color = StatusProtected.copy(alpha = 0.3f),
                        style = Stroke(width = 6.dp.toPx(), pathEffect = dashEffect)
                    )
                    // Core line
                    drawPath(
                        path = path,
                        color = StatusProtected.copy(alpha = 0.85f),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect)
                    )
                }
            }
        }

        // Center HUD in table
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val aliveCount = seats.count { it.isAlive }
            val totalCount = seats.size
            val deadCount = totalCount - aliveCount
            val werewolfCount = seats.count { it.isAlive && it.roleId == Role.WEREWOLF.roleId }
            val unassignedCount = seats.count { it.roleId == null }

            val wolfTargetCount = seats.count { StatusMarker.WOLF_TARGET in it.activeStatuses }
            val protectedCount = seats.count { StatusMarker.PROTECTED in it.activeStatuses || StatusMarker.SAVED_POTION in it.activeStatuses }
            val poisonedCount = seats.count { StatusMarker.POISONED in it.activeStatuses }
            val seerInspectedCount = seats.count { StatusMarker.SEER_INSPECTED in it.activeStatuses }
            val loversCount = seats.count { it.linkedSeatIds.isNotEmpty() }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, TableSurfaceBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Row 1: Player Metrics with Icons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Alive
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Alive Players",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$aliveCount/$totalCount",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Dead
                    if (deadCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Dead Players",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$deadCount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    // Werewolves
                    if (werewolfCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = "Active Werewolves",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$werewolfCount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF5252)
                            )
                        }
                    }

                    // Unassigned Roles
                    if (unassignedCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Style,
                                contentDescription = "Unassigned Roles",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$unassignedCount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Row 2: Active Night Status Badges (if any active)
                if (wolfTargetCount > 0 || protectedCount > 0 || poisonedCount > 0 || seerInspectedCount > 0 || loversCount > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (wolfTargetCount > 0) {
                            StatusBadge(icon = Icons.Default.CrisisAlert, count = wolfTargetCount, color = StatusWolfTarget)
                        }
                        if (protectedCount > 0) {
                            StatusBadge(icon = Icons.Default.Shield, count = protectedCount, color = StatusProtected)
                        }
                        if (poisonedCount > 0) {
                            StatusBadge(icon = Icons.Default.Science, count = poisonedCount, color = StatusPoisoned)
                        }
                        if (seerInspectedCount > 0) {
                            StatusBadge(icon = Icons.Default.Visibility, count = seerInspectedCount, color = StatusSeerInspected)
                        }
                        if (loversCount > 0) {
                            StatusBadge(icon = Icons.Default.Favorite, count = loversCount / 2, color = StatusProtected)
                        }
                    }
                }

                // Row 3: M3 Layout Lock/Unlock Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isLayoutEditUnlocked) MaterialTheme.colorScheme.primaryContainer
                            else Color.White.copy(alpha = 0.08f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isLayoutEditUnlocked) MaterialTheme.colorScheme.primary else TableSurfaceBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onToggleLayoutLock() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isLayoutEditUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isLayoutEditUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLayoutEditUnlocked) stringResource(R.string.layout_edit_on)
                            else stringResource(R.string.layout_edit_locked),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLayoutEditUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "$count",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
