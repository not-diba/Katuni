package com.diba.katuni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.diba.katuni.ui.theme.KatuniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KatuniTheme {
                KatuniApp()
            }
        }
    }
}

@Composable
fun ReadingNowScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Reading Now Screen")
    }
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Library Screen")
    }
}

@Composable
fun HighlightsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Highlights Screen")
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Settings Screen")
    }
}

enum class Destination(
    val route: String,
    val label: String,
    val iconRes: Int,
    val contentDescription: String
) {
    READING_NOW(
        "reading_now",
        "Reading Now",
        R.drawable.twotone_auto_stories_24,
        "Reading now"
    ),
    LIBRARY("library", "Library", R.drawable.round_dashboard_24, "Library"),
    HIGHLIGHTS("highlights", "Highlights", R.drawable.twotone_book_24, "Highlights"),
    SETTINGS("settings", "Settings", R.drawable.baseline_person_4_24, "Settings"),
}

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

@Composable
fun KatuniApp(modifier: Modifier = Modifier) {
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

@Composable
fun FloatingBottomBar(
    destinations: List<Destination>,
    selectedIndex: Int,
    onDestinationSelected: (Int) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEachIndexed { index, destination ->
                val isSelected = index == selectedIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable { onDestinationSelected(index) }
                        // TODO: This does like a bounce effect it looks fun am not sure if I will keep it
                        .animateContentSize()
                ) {
                    Icon(
                        painter = painterResource(destination.iconRes),
                        contentDescription = destination.contentDescription,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    if (isSelected) {
                        Text(
                            text = destination.label,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KatuniTheme {
        KatuniApp()
    }
}