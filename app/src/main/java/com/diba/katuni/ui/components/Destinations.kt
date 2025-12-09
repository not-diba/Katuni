package com.diba.katuni.ui.components

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    data object ReadingNow : Destination()

    @Serializable
    data object Library : Destination()

    @Serializable
    data object Highlights : Destination()

    @Serializable
    data object Settings : Destination()
}