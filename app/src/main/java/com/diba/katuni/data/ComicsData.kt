package com.diba.katuni.data

import com.diba.katuni.model.KatuniFile

val allComics: Set<KatuniFile> = setOf(
    KatuniFile(
        name = "Avengers #1",
        modifiedAt = 1710000000000,
        size = 24500000,
        path = "/comics/marvel/avengers_1.cbz"
    ),
    KatuniFile(
        name = "Spider-Man Classics",
        modifiedAt = 1711000000000,
        size = 18000000,
        path = "/comics/marvel/spiderman_classics.cbz"
    ),
    KatuniFile(
        name = "Batman Year One",
        modifiedAt = 1712000000000,
        size = 32000000,
        path = "/comics/dc/batman_year_one.cbz"
    )
)
