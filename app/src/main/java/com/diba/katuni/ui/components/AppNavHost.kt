package com.diba.katuni.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.diba.katuni.ui.screens.HighlightsScreen
import com.diba.katuni.ui.screens.ReadingNowScreen
import com.diba.katuni.ui.screens.SettingsScreen
import com.diba.katuni.ui.screens.comic_viewer.Comic
import com.diba.katuni.ui.screens.comic_viewer.ComicViewerScreen
import com.diba.katuni.ui.screens.library.LibraryScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.READING_NOW -> ReadingNowScreen()
                    Destination.LIBRARY -> LibraryScreen(
                        onComicClick = { comic ->
                            navController.navigate(
                                route = Comic(
                                    comicName = comic.name,
                                    comicPath = comic.path
                                )
                            )
                        }
                    )

                    Destination.HIGHLIGHTS -> HighlightsScreen()
                    Destination.SETTINGS -> SettingsScreen()
                }
            }
            composable<Comic> { backStackEntry ->
                val comic: Comic = backStackEntry.toRoute()
                ComicViewerScreen(
                    comicName = comic.comicName,
                    comicPath = comic.comicPath,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}