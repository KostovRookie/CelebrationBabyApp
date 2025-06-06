package com.stanga.nanit.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BirthdayEntity::class], version = 1)
abstract class NanitDatabase : RoomDatabase() {
    abstract fun kidDao(): DatabaseDao
}