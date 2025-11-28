package com.diba.katuni.model

import kotlinx.serialization.Serializable

@Serializable
data class KatuniFile(
    val name: String,
    val modifiedAt: Long = 0,
    val size: Long = 0,
    val path: String = "",
    val mimeType: String? = null
)
