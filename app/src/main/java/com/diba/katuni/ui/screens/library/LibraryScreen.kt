package com.diba.katuni.ui.screens.library

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.diba.katuni.R
import com.diba.katuni.model.KatuniFile


@Composable
fun LibraryScreen(
    onComicClick: (KatuniFile) -> Unit,
    viewModel: LibraryScreenViewModel = viewModel(
        factory = LibraryViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current



    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                uiState.error != null -> Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier.padding(16.dp)
                )

                uiState.comics.isEmpty() && !uiState.hasFolder -> Text(
                    text = "Select a folder to view your comics",
                    modifier = Modifier.padding(16.dp)
                )

                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.refreshFiles(context) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LibraryGrid(
                            comics = uiState.filteredComics,
                            onComicClick = onComicClick,
                        )
                    }
                }
            }

            SearchBarAndFolderSelect(
                context, uiState, viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
                    .align(Alignment.TopCenter)
                    .background(Color.Transparent)
                    .zIndex(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarAndFolderSelect(
    context: Context,
    uiState: LibraryScreenUiState,
    viewModel: LibraryScreenViewModel,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        val colors1 = SearchBarDefaults.colors()
        var dropDownExpanded by remember { mutableStateOf(false) }
        val folderPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            if (uri != null) {
                viewModel.loadFilesFromUri(context, uri)
            }
        }

        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.updateSearchQuery(uiState.searchQuery) },
                    expanded = false,
                    onExpandedChange = { },
                    placeholder = { Text("Search comics") },
                    colors = colors1.inputFieldColors,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = "Search comics"
                        )
                    }
                )
            },
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier
                .weight(1f)
                .semantics { isTraversalGroup = false },
            shape = SearchBarDefaults.inputFieldShape,
            colors = colors1,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
            content = { },
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box {
            IconButton(
                onClick = { dropDownExpanded = !dropDownExpanded },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.filter_icon),
                    contentDescription = "List of available options",
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = dropDownExpanded,
                onDismissRequest = { dropDownExpanded = false },
                offset = DpOffset(x = (-8).dp, y = 8.dp),
                modifier = Modifier.padding(end = 24.dp)
            ) {
                DropdownMenuItem(
                    text = { Text(if (uiState.hasFolder) "Change folder" else "Select folder") },
                    onClick = { folderPicker.launch(null) }
                )
                DropdownMenuItem(
                    text = { Text("Clear Folder") },
                    onClick = { viewModel.clearFolder() }
                )
            }
        }
    }
}

@Composable
fun ComicItem(comic: KatuniFile, onClick: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(comic)
                    .memoryCacheKey(comic.path)
                    .diskCacheKey(comic.path)
                    .placeholderMemoryCacheKey(comic.path)
                    .crossfade(200)
                    .build(),
                contentDescription = comic.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Text(
            text = comic.name,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun LibraryGrid(
    comics: List<KatuniFile>,
    modifier: Modifier = Modifier,
    onComicClick: (KatuniFile) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 100.dp, bottom = 150.dp),
        modifier = modifier,

        ) {
        items(comics, key = { it.path }, contentType = { "grid_item" }) { comic ->
            ComicItem(comic = comic, onClick = { onComicClick(comic) })
        }
    }
}


