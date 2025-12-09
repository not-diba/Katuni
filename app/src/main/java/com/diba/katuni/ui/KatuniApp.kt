package com.diba.katuni.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.diba.katuni.ui.components.AppNavHost
import com.diba.katuni.ui.components.Destination
import com.diba.katuni.ui.components.FloatingBottomBar
import com.diba.katuni.ui.components.bottomBarDestinations
import com.diba.katuni.ui.screens.comic_viewer.Comic

@Composable
fun KatuniApp() {
    val navController = rememberNavController()
    val startDestination = Destination.ReadingNow
    var selectedDestination by remember { mutableStateOf<Destination>(startDestination) }
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenWidthDp = with(density) { windowInfo.containerSize.width.toDp() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isMainDestination = currentRoute?.let { route ->
        route.contains("ReadingNow") ||
                route.contains("Library") ||
                route.contains("Highlights") ||
                route.contains("Settings")
    } ?: false

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppNavHost(navController, startDestination, modifier = Modifier.fillMaxSize())

        if (isMainDestination) {
            FloatingBottomBar(
                destinations = bottomBarDestinations,
                selectedDestination = selectedDestination,
                onDestinationSelected = { item ->
                    navController.navigate(item.destination)
                    selectedDestination = item.destination
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 32.dp)
                    .shadow(8.dp, RoundedCornerShape(100.dp))
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.Black)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .align(Alignment.BottomCenter)
                    .width(0.8f * screenWidthDp)
            )
        }
    }
}