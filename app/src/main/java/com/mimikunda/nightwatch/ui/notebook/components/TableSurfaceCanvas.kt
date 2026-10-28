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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mimikunda.nightwatch.ui.theme.StatusProtected
import com.mimikunda.nightwatch.ui.theme.TableSurfaceBorder
import com.mimikunda.nightwatch.ui.theme.TableSurfaceCenter

@Composable
fun TableSurfaceCanvas(
    seats: List<Seat>,
    seatCenters: Map<String, Offset>,
    isLayoutEditUnlocked: Boolean,
    onToggleLayoutLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background Table Canvas & Link Paths
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw central table boundary
            val tableInsetX = canvasW * 0.18f
            val tableInsetY = canvasH * 0.18f
            val tableWidth = canvasW - (tableInsetX * 2f)
            val tableHeight = canvasH - (tableInsetY * 2f)

            if (tableWidth > 0 && tableHeight > 0) {
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
            val werewolfCount = seats.count { it.isAlive && it.roleId == Role.WEREWOLF.roleId }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .border(1.dp, TableSurfaceBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.hud_alive, aliveCount, totalCount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (werewolfCount > 0) {
                        Text(
                            text = stringResource(R.string.hud_werewolves, werewolfCount),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Layout Lock / Unlock Toggle Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isLayoutEditUnlocked) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .clickable { onToggleLayoutLock() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isLayoutEditUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isLayoutEditUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLayoutEditUnlocked) stringResource(R.string.layout_edit_on)
                        else stringResource(R.string.layout_edit_locked),
                        fontSize = 10.sp,
                        color = if (isLayoutEditUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray
                    )
                }
            }
        }
    }
}
