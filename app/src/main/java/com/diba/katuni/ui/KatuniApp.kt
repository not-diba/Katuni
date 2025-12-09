package com.diba.katuni.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diba.katuni.ui.components.AppNavHost
import com.diba.katuni.ui.components.Destination
import com.diba.katuni.ui.components.FloatingBottomBar

@Composable
fun KatuniApp() {
    val navController = rememberNavController()
    val startDestination = Destination.READING_NOW
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenWidthDp = with(density) { windowInfo.containerSize.width.toDp() }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppNavHost(navController, startDestination, modifier = Modifier.fillMaxSize())

        FloatingBottomBar(
            destinations = Destination.entries,
            selectedIndex = selectedDestination,
            onDestinationSelected = { index ->
                navController.navigate(Destination.entries[index].route)
                selectedDestination = index
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
