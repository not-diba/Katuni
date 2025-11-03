package com.diba.katuni.model

data class KatuniFile(
    val name: String,
    val modifiedAt: Long = 0,
    val size: Long = 0,
    val path: String = ""
)
