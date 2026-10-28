package com.mimikunda.nightwatch.ui.notebook.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.model.Role
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.ui.theme.CardSurfaceVariantDark
import com.mimikunda.nightwatch.ui.theme.TableSurfaceBorder
import com.mimikunda.nightwatch.ui.theme.toColor
import com.mimikunda.nightwatch.ui.theme.toIcon
import com.mimikunda.nightwatch.ui.theme.toRoleBorderColor
import com.mimikunda.nightwatch.ui.theme.toRoleCardBackgroundColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeatNode(
    seat: Seat,
    isSelected: Boolean,
    isLayoutEditUnlocked: Boolean,
    currentCenterPx: Offset,
    onSelect: () -> Unit,
    onQuickToggleAlive: () -> Unit,
    onDrag: (normX: Float, normY: Float) -> Unit,
    canvasWidth: Float,
    canvasHeight: Float,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val currentCenterState = rememberUpdatedState(currentCenterPx)

    val roleObj = Role.ALL_ROLES.find { it.roleId == seat.roleId }
    val roleTeam = roleObj?.team

    val cornerRadius by animateDpAsState(
        targetValue = when {
            !seat.isAlive -> 8.dp
            isSelected -> 12.dp
            else -> 24.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "seatCornerRadius"
    )

    val scale by animateFloatAsState(
        targetValue = when {
            !seat.isAlive -> 0.88f
            isSelected -> 1.08f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "seatScale"
    )

    val alpha = if (seat.isAlive) 1.0f else 0.45f
    val shape = RoundedCornerShape(cornerRadius)

    val backgroundColor = if (isSelected) {
        CardSurfaceVariantDark
    } else {
        seat.roleId.toRoleCardBackgroundColor(roleTeam)
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else if (!seat.isAlive) {
        Color.Gray.copy(alpha = 0.5f)
    } else {
        seat.roleId.toRoleBorderColor(roleTeam)
    }

    val nodeModifier = modifier
        .scale(scale)
        .alpha(alpha)
        .clip(shape)
        .background(backgroundColor)
        .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
        .combinedClickable(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelect()
            },
            onDoubleClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onQuickToggleAlive()
            },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onQuickToggleAlive()
            }
        )
        .then(
            if (isLayoutEditUnlocked) {
                Modifier.pointerInput(canvasWidth, canvasHeight) {
                    var accumulatedX = currentCenterState.value.x
                    var accumulatedY = currentCenterState.value.y

                    detectDragGestures(
                        onDragStart = {
                            accumulatedX = currentCenterState.value.x
                            accumulatedY = currentCenterState.value.y
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            accumulatedX += dragAmount.x
                            accumulatedY += dragAmount.y
                            val normX = (accumulatedX / canvasWidth).coerceIn(0.05f, 0.95f)
                            val normY = (accumulatedY / canvasHeight).coerceIn(0.05f, 0.95f)
                            onDrag(normX, normY)
                        }
                    )
                }
            } else {
                Modifier
            }
        )
        .padding(horizontal = 10.dp, vertical = 8.dp)

    Box(modifier = Modifier.wrapContentSize()) {
        // Main Card
        Box(modifier = nodeModifier) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header Row: Badge + Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${seat.index + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = seat.name ?: stringResource(R.string.seat_default_name, seat.index + 1),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (seat.isAlive) MaterialTheme.colorScheme.onSurface else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Role label (if assigned)
                if (roleObj != null) {
                    Text(
                        text = roleObj.displayName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Strikethrough indicator if dead
                if (!seat.isAlive) {
                    Text(
                        text = stringResource(R.string.seat_status_dead),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Active Status Badges Overlay (Top-Right Corner)
        if (seat.activeStatuses.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .border(1.dp, TableSurfaceBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                seat.activeStatuses.take(3).forEach { status ->
                    Icon(
                        imageVector = status.toIcon(),
                        contentDescription = null,
                        tint = status.toColor(),
                        modifier = Modifier.size(11.dp)
                    )
                }
                if (seat.activeStatuses.size > 3) {
                    Text(
                        text = "+${seat.activeStatuses.size - 3}",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
