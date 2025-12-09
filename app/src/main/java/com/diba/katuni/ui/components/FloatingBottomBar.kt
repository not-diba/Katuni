package com.diba.katuni.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.diba.katuni.R

data class BottomBarItem(
    val destination: Destination,
    val label: String,
    val iconRes: Int,
    val contentDescription: String
)

val bottomBarDestinations = listOf(
    BottomBarItem(
        destination = Destination.ReadingNow,
        label = "Reading Now",
        iconRes = R.drawable.twotone_reading_now,
        contentDescription = "Reading now"
    ),
    BottomBarItem(
        destination = Destination.Library,
        label = "Library",
        iconRes = R.drawable.dashboard,
        contentDescription = "Library"
    ),
    BottomBarItem(
        destination = Destination.Highlights,
        label = "Highlights",
        iconRes = R.drawable.twotone_book,
        contentDescription = "Highlights"
    ),
    BottomBarItem(
        destination = Destination.Settings,
        label = "Settings",
        iconRes = R.drawable.twotone_person,
        contentDescription = "Settings"
    ),
)

@Composable
fun FloatingBottomBar(
    destinations: List<BottomBarItem>,
    selectedDestination: Destination,
    onDestinationSelected: (BottomBarItem) -> Unit,
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
            destinations.forEach { item ->
                val isSelected = item.destination == selectedDestination

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable { onDestinationSelected(item) }
                        .animateContentSize()
                ) {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.contentDescription,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    if (isSelected) {
                        Text(
                            text = item.label,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
