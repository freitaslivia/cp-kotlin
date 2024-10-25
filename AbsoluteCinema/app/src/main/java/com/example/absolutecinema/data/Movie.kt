package com.example.absolutecinema.data

data class Movie(
    val id: Int = 0,
    val name: String,
    val description: String,
    val releaseDate: String,
    val type: String,
    val coverImageUri: String
)
