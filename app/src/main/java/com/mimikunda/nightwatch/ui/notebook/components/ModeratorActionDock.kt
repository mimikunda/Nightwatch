package com.mimikunda.nightwatch.ui.notebook.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onClearNightMarkers: () -> Unit,
    onOpenDealFlow: () -> Unit,
    onUpdateEdgeCount: (TableEdge, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(CardSurfaceDark)
            .border(1.dp, TableSurfaceBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(16.dp)
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
            // Unselected Table Actions
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

                Button(
                    onClick = onClearNightMarkers,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(stringResource(R.string.clear_markers_button), fontSize = 11.sp)
                }
            }

            // Edge Count Steppers: Show ONLY when Layout Edit is unlocked
            if (isLayoutEditUnlocked) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EdgeStepper(stringResource(R.string.edge_top), TableEdge.TOP, edgeCounts[TableEdge.TOP] ?: 0, onUpdateEdgeCount)
                    EdgeStepper(stringResource(R.string.edge_right), TableEdge.RIGHT, edgeCounts[TableEdge.RIGHT] ?: 0, onUpdateEdgeCount)
                    EdgeStepper(stringResource(R.string.edge_bottom), TableEdge.BOTTOM, edgeCounts[TableEdge.BOTTOM] ?: 0, onUpdateEdgeCount)
                    EdgeStepper(stringResource(R.string.edge_left), TableEdge.LEFT, edgeCounts[TableEdge.LEFT] ?: 0, onUpdateEdgeCount)
                    EdgeStepper(stringResource(R.string.edge_float), TableEdge.FLOATING, edgeCounts[TableEdge.FLOATING] ?: 0, onUpdateEdgeCount)
                }
            }
        }
    }
}

@Composable
private fun EdgeStepper(
    label: String,
    edge: TableEdge,
    currentCount: Int,
    onUpdateEdgeCount: (TableEdge, Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = { onUpdateEdgeCount(edge, (currentCount - 1).coerceAtLeast(0)) },
                modifier = Modifier.width(28.dp)
            ) {
                Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "$currentCount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(
                onClick = { onUpdateEdgeCount(edge, currentCount + 1) },
                modifier = Modifier.width(28.dp)
            ) {
                Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
