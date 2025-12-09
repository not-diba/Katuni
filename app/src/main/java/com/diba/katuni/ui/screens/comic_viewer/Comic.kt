package com.diba.katuni.ui.screens.comic_viewer

import kotlinx.serialization.Serializable

@Serializable
data class Comic(
    val comicPath: String,
    val comicName: String
)
