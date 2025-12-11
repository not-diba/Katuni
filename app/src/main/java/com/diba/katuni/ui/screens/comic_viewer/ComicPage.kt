package com.diba.katuni.ui.screens.comic_viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toUri
import com.diba.katuni.R
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun ComicPage(
    pagePath: String?,
    pageNumber: Int,
    onRetry: () -> Unit = {}
) {
    var isPageReady by remember(pagePath) { mutableStateOf(false) }
    val context = LocalContext.current

    // Check if page file exists (for PDFs being rendered progressively)
    LaunchedEffect(pagePath) {
        if (!pagePath.isNullOrEmpty()) {
            // Poll until file exists (for PDF pages being rendered)
            while (!File(pagePath).exists()) {
                delay(100) // Check every 100ms
            }
            isPageReady = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        when {
            pagePath.isNullOrEmpty() -> {
                // Page path not provided yet
                LoadingIndicator(pageNumber)
            }

            !isPageReady -> {
                // Page is being rendered
                LoadingIndicator(pageNumber, "Rendering page $pageNumber...")
            }

            else -> {
                // Page is ready to display
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(pagePath))
                        .memoryCacheKey(pagePath)
                        .diskCacheKey(pagePath)
                        .placeholderMemoryCacheKey(pagePath)
                        .crossfade(200)
                        .build(),
                    contentDescription = "Page $pageNumber",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    loading = {
                        LoadingIndicator(pageNumber)
                    },
                    error = {
                        ErrorDisplay(pageNumber, onRetry)
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    pageNumber: Int,
    message: String = "Loading page $pageNumber..."
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = Color.White
        )
    }
}

@Composable
private fun ErrorDisplay(
    pageNumber: Int,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.twotone_clear),
            contentDescription = "Failed to load",
            modifier = Modifier.size(48.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Failed to load page $pageNumber",
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("Retry")
        }
    }
}