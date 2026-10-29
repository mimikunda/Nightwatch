package com.mimikunda.nightwatch.ui.notebook.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.domain.model.TableShape
import com.mimikunda.nightwatch.domain.settings.GameSettingsRepository
import com.mimikunda.nightwatch.ui.theme.StatusMayor
import com.mimikunda.nightwatch.ui.theme.StatusPoisoned
import com.mimikunda.nightwatch.ui.theme.StatusProtected
import com.mimikunda.nightwatch.ui.theme.StatusSeerInspected
import com.mimikunda.nightwatch.ui.theme.StatusSilenced
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
    edgeCounts: Map<TableEdge, Int>,
    onToggleLayoutLock: () -> Unit,
    onOpenDealFlow: () -> Unit,
    onUpdateEdgeCount: (TableEdge, Int) -> Unit,
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
            val mayorCount = seats.count { StatusMarker.MAYOR in it.activeStatuses }
            val silencedCount = seats.count { StatusMarker.SILENCED in it.activeStatuses }
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

                // Row 2: Colour-Coded Active Night Status Badges
                if (wolfTargetCount > 0 || protectedCount > 0 || poisonedCount > 0 || seerInspectedCount > 0 || mayorCount > 0 || silencedCount > 0 || loversCount > 0) {
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
                            StatusBadge(icon = Icons.Default.Cancel, count = seerInspectedCount, color = StatusSeerInspected)
                        }
                        if (mayorCount > 0) {
                            StatusBadge(icon = Icons.Default.Shield, count = mayorCount, color = StatusMayor)
                        }
                        if (silencedCount > 0) {
                            StatusBadge(icon = Icons.Default.MicOff, count = silencedCount, color = StatusSilenced)
                        }
                        if (loversCount > 0) {
                            StatusBadge(icon = Icons.Default.Favorite, count = loversCount / 2, color = StatusProtected)
                        }
                    }
                }

                // Row 3: Deal Roles Button
                Button(
                    onClick = onOpenDealFlow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.deal_roles_button),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Row 4: Floating Seat Stepper Pill (when layout edit is unlocked)
                AnimatedVisibility(visible = isLayoutEditUnlocked) {
                    FloatingEdgeStepperPill(
                        currentCount = edgeCounts[TableEdge.FLOATING] ?: 0,
                        onUpdateEdgeCount = onUpdateEdgeCount
                    )
                }

                // Row 5: M3 Layout Lock/Unlock Toggle Button
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
private fun FloatingEdgeStepperPill(
    currentCount: Int,
    onUpdateEdgeCount: (TableEdge, Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, TableSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, end = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BlurOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.edge_float),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = { onUpdateEdgeCount(TableEdge.FLOATING, (currentCount - 1).coerceAtLeast(0)) },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease Floating Seats",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp)
            )
        }

        Text(
            text = "$currentCount",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
            onClick = { onUpdateEdgeCount(TableEdge.FLOATING, currentCount + 1) },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase Floating Seats",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
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
            .background(color.copy(alpha = 0.25f))
            .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
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
