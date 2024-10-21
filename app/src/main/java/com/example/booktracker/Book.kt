package com.example.booktracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val author: String,
    var status: String
)
