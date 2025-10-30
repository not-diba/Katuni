package com.diba.katuni.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding -> LibraryGrid(modifier = modifier, contentPadding = innerPadding) }
}

@Composable
fun LibraryGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    // TODO: Searchbar here
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(200) { item ->
            ComicItem()
        }
    }
}

@Composable
fun ComicItem() {
    Column {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(280.dp)
        ) { }
        Text("Comic Book", modifier = Modifier.padding(start = 8.dp, bottom = 12.dp))
    }
}