package com.diba.katuni.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diba.katuni.ui.screens.HighlightsScreen
import com.diba.katuni.ui.screens.library.LibraryScreen
import com.diba.katuni.ui.screens.ReadingNowScreen
import com.diba.katuni.ui.screens.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.READING_NOW -> ReadingNowScreen()
                    Destination.LIBRARY -> LibraryScreen()
                    Destination.HIGHLIGHTS -> HighlightsScreen()
                    Destination.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}