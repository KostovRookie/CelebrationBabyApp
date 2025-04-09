package com.stanga.nanit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kids")
data class KidEntity(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val birthday: Long,
    val pictureUri: String?
)