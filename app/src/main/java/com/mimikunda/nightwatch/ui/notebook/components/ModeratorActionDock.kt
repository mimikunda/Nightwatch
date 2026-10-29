package com.mimikunda.nightwatch.ui.notebook.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.ui.theme.CardSurfaceDark
import com.mimikunda.nightwatch.ui.theme.TableSurfaceBorder

@Composable
fun ModeratorActionDock(
    selectedSeat: Seat?,
    isLinkSelectionMode: Boolean,
    isLayoutEditUnlocked: Boolean,
    edgeCounts: Map<TableEdge, Int>,
    onCancelLinkMode: () -> Unit,
    onOpenDealFlow: () -> Unit,
    onUpdateEdgeCount: (TableEdge, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSeats = edgeCounts.values.sum()

    AnimatedVisibility(
        visible = isLinkSelectionMode || isLayoutEditUnlocked,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(CardSurfaceDark)
                .border(1.dp, TableSurfaceBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (isLinkSelectionMode) {
                // Link Selection Mode Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.link_prompt,
                            selectedSeat?.name ?: stringResource(R.string.seat_default_name, (selectedSeat?.index ?: 0) + 1)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = onCancelLinkMode) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                }
            } else {
                // Layout Edit Mode Header (Deal Roles + Total Seats Badge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onOpenDealFlow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(stringResource(R.string.deal_roles_button), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Total Seats Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, TableSurfaceBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$totalSeats Seats Total",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ergonomic Horizontal Edge Stepper Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EdgeStepperPill(stringResource(R.string.edge_top), TableEdge.TOP, Icons.Default.North, edgeCounts[TableEdge.TOP] ?: 0, onUpdateEdgeCount)
                    EdgeStepperPill(stringResource(R.string.edge_right), TableEdge.RIGHT, Icons.Default.East, edgeCounts[TableEdge.RIGHT] ?: 0, onUpdateEdgeCount)
                    EdgeStepperPill(stringResource(R.string.edge_bottom), TableEdge.BOTTOM, Icons.Default.South, edgeCounts[TableEdge.BOTTOM] ?: 0, onUpdateEdgeCount)
                    EdgeStepperPill(stringResource(R.string.edge_left), TableEdge.LEFT, Icons.Default.West, edgeCounts[TableEdge.LEFT] ?: 0, onUpdateEdgeCount)
                    EdgeStepperPill(stringResource(R.string.edge_float), TableEdge.FLOATING, Icons.Default.BlurOn, edgeCounts[TableEdge.FLOATING] ?: 0, onUpdateEdgeCount)
                }
            }
        }
    }
}

@Composable
private fun EdgeStepperPill(
    label: String,
    edge: TableEdge,
    icon: ImageVector,
    currentCount: Int,
    onUpdateEdgeCount: (TableEdge, Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .border(1.dp, TableSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, end = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = { onUpdateEdgeCount(edge, (currentCount - 1).coerceAtLeast(0)) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = "$currentCount",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
            onClick = { onUpdateEdgeCount(edge, currentCount + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
