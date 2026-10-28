package com.mimikunda.nightwatch.ui.deal.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.domain.model.Role
import com.mimikunda.nightwatch.domain.model.RoleTeam
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.ui.theme.CardSurfaceDark
import com.mimikunda.nightwatch.ui.theme.StatusProtected
import com.mimikunda.nightwatch.ui.theme.StatusWolfTarget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HoldToRevealCard(
    seat: Seat,
    role: Role?,
    hasPeekedRole: Boolean,
    onNameChanged: (String) -> Unit,
    onPeekRegistered: () -> Unit,
    onPassToNext: () -> Unit,
    isLastSeat: Boolean,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    var isHolding by remember { mutableStateOf(false) }

    val revealFraction by animateFloatAsState(
        targetValue = if (isHolding) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "revealMorph"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stranger Player Name Input
        OutlinedTextField(
            value = seat.name ?: "",
            onValueChange = onNameChanged,
            label = { Text(stringResource(R.string.name_input_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hold to Reveal Touch Area
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(180.dp)
                .scale(0.95f + (revealFraction * 0.08f))
                .clip(RoundedCornerShape(24.dp))
                .background(if (revealFraction >= 0.5f) CardSurfaceDark else Color(0xFF151821))
                .border(
                    width = if (revealFraction >= 0.5f) 2.dp else 1.dp,
                    color = if (revealFraction >= 0.5f) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(24.dp)
                )
                .pointerInput(seat.id) {
                    awaitEachGesture {
                        awaitFirstDown()
                        isHolding = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        val timerJob = coroutineScope.launch {
                            delay(500)
                            if (isHolding) {
                                onPeekRegistered()
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }

                        waitForUpOrCancellation()
                        isHolding = false
                        timerJob.cancel()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (revealFraction < 0.5f) {
                // Concealed Vault Card
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.hold_to_reveal_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stringResource(R.string.hold_to_reveal_subtitle),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // Revealed Secret Role View
                val teamColor = when (role?.team) {
                    RoleTeam.WEREWOLVES -> StatusWolfTarget
                    RoleTeam.VILLAGE -> StatusProtected
                    else -> MaterialTheme.colorScheme.tertiary
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Team Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(teamColor.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = role?.team?.name ?: RoleTeam.VILLAGE.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = teamColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = role?.displayName ?: Role.VILLAGER.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when (role?.team) {
                            RoleTeam.WEREWOLVES -> "Eliminate villagers without getting caught."
                            RoleTeam.VILLAGE -> "Find the wolves and vote them out."
                            else -> "Fulfill your secret win condition."
                        },
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Next Seat Gate Button
        val canPass = hasPeekedRole && !isHolding

        Button(
            onClick = onPassToNext,
            enabled = canPass,
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.DarkGray
            )
        ) {
            Text(
                text = when {
                    isHolding -> stringResource(R.string.pass_prompt_releasing)
                    !hasPeekedRole -> stringResource(R.string.pass_prompt_hold_first)
                    isLastSeat -> stringResource(R.string.pass_prompt_finish)
                    else -> stringResource(R.string.pass_prompt_lock_and_pass)
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}
