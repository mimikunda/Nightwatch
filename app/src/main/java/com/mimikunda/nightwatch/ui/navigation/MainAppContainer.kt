package com.mimikunda.nightwatch.ui.navigation

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.ui.notebook.NotebookScreen
import com.mimikunda.nightwatch.ui.settings.SettingsScreen
import com.mimikunda.nightwatch.ui.theme.PitchBlack
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

enum class NavigationDestination(
    val titleRes: Int,
    val icon: ImageVector
) {
    Notebook(R.string.nav_notebook, Icons.Default.EditNote),
    Director(R.string.nav_director, Icons.Default.AutoMode),
    Settings(R.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun MainAppContainer() {
    var currentDestination by remember { mutableStateOf(NavigationDestination.Notebook) }
    var backProgress by remember { mutableFloatStateOf(0f) }

    // Live Predictive Back Handler for Main Navigation
    PredictiveBackHandler(enabled = currentDestination != NavigationDestination.Notebook) { progressFlow: Flow<BackEventCompat> ->
        try {
            progressFlow.collect { backEvent ->
                backProgress = backEvent.progress
            }
            currentDestination = NavigationDestination.Notebook
        } catch (_: CancellationException) {
            // User cancelled back gesture
        } finally {
            backProgress = 0f
        }
    }

    Scaffold(
        containerColor = PitchBlack,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = PitchBlack,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = stringResource(destination.titleRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.titleRes),
                                fontSize = 11.sp,
                                fontWeight = if (currentDestination == destination) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val screenWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

            // Background Layer: Primary Notebook Screen (ALWAYS composed beneath Director or Settings at full brightness)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val bgScale = if (currentDestination == NavigationDestination.Notebook) 1f else 0.95f + (backProgress * 0.05f)
                        scaleX = bgScale
                        scaleY = bgScale
                        alpha = 1f
                    }
            ) {
                NotebookScreen()
            }

            // Foreground Layer: Director or Settings Screen sliding off-screen to reveal NotebookScreen
            if (currentDestination != NavigationDestination.Notebook) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val translationX = backProgress * screenWidthPx
                            val fgScale = 1f - (backProgress * 0.05f)
                            scaleX = fgScale
                            scaleY = fgScale
                            this.translationX = translationX
                        }
                        .clip(RoundedCornerShape((20 * backProgress).dp))
                        .border(
                            width = if (backProgress > 0f) 1.dp else 0.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = backProgress),
                            shape = RoundedCornerShape((20 * backProgress).dp)
                        )
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentDestination) {
                        NavigationDestination.Director -> {
                            PlaceholderScreen(
                                title = stringResource(R.string.director_placeholder_title),
                                subtitle = stringResource(R.string.director_placeholder_subtitle)
                            )
                        }
                        NavigationDestination.Settings -> {
                            SettingsScreen()
                        }
                        NavigationDestination.Notebook -> { }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
