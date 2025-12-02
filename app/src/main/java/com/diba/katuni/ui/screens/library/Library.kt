package com.diba.katuni.ui.screens.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diba.katuni.ui.screens.comic_viewer.ComicViewerScreen

enum class LibraryScreens {
    Library,
    ComicViewer
}

@Composable
fun Library(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LibraryScreens.Library.name,
        modifier = Modifier
    ) {
        composable(route = LibraryScreens.Library.name) {
            LibraryScreen(
                onComicClick = { comic ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "comicPath",
                        comic.path
                    )
                    navController.navigate(LibraryScreens.ComicViewer.name)
                }
            )
        }

        composable(route = LibraryScreens.ComicViewer.name) {
            val comicPath = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("comicPath") ?: ""

            ComicViewerScreen(
                comicPath = comicPath,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}