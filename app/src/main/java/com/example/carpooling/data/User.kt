package com.example.carpooling.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val nickname: String,
    val age: Int,
    val role: String,  // "passenger" або "driver"
    val photoUri: String? = null // тут можна зберігати шлях до фото або URL
)