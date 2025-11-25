package com.diba.katuni.ui.screens.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diba.katuni.model.KatuniFile

@Preview(showBackground = true)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryScreenViewModel = viewModel(factory = LibraryViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            viewModel.loadFilesFromUri(context, uri)
        }
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            Button(onClick = { folderPicker.launch(null) }) {
                Text("Select Comics Folder")
            }

            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text("Error: ${uiState.error}")
                else -> LibraryGrid(
                    comics = uiState.comics,
                    contentPadding = PaddingValues(8.dp)
                )
            }
        }
    }
}

@Composable
fun LibraryGrid(
    comics: List<KatuniFile>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(comics) { comic ->
            ComicItem(name = comic.name)
        }
    }
}

@Composable
fun ComicItem(name: String) {
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
        Text(
            text = name,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )
    }
}
