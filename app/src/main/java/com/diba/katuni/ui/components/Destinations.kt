package com.diba.katuni.ui.components

import com.diba.katuni.R

enum class Destination(
    val route: String,
    val label: String,
    val iconRes: Int,
    val contentDescription: String
) {
    READING_NOW(
        "reading_now",
        "Reading Now",
        R.drawable.twotone_reading_now,
        "Reading now"
    ),
    LIBRARY("library", "Library", R.drawable.dashboard, "Library"),
    HIGHLIGHTS("highlights", "Highlights", R.drawable.twotone_book, "Highlights"),
    SETTINGS("settings", "Settings", R.drawable.twotone_person, "Settings"),
}