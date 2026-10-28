package com.mimikunda.nightwatch.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimikunda.nightwatch.R
import com.mimikunda.nightwatch.ui.notebook.NotebookScreen
import com.mimikunda.nightwatch.ui.settings.SettingsScreen
import com.mimikunda.nightwatch.ui.theme.PitchBlack

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

    // Predictive Back / System Back Handler for Main Navigation
    BackHandler(enabled = currentDestination != NavigationDestination.Notebook) {
        currentDestination = NavigationDestination.Notebook
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavigationDestination.Notebook -> {
                    NotebookScreen()
                }
                NavigationDestination.Director -> {
                    PlaceholderScreen(
                        title = stringResource(R.string.director_placeholder_title),
                        subtitle = stringResource(R.string.director_placeholder_subtitle)
                    )
                }
                NavigationDestination.Settings -> {
                    SettingsScreen()
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
