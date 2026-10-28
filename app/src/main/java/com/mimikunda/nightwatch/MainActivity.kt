package com.mimikunda.nightwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mimikunda.nightwatch.domain.settings.GameSettingsRepository
import com.mimikunda.nightwatch.ui.navigation.MainAppContainer
import com.mimikunda.nightwatch.ui.theme.NightwatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameSettingsRepository.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            NightwatchTheme {
                MainAppContainer()
            }
        }
    }
}
