package com.stanga.nanit.data

import androidx.room.*

@Dao
interface DatabaseDao {
    @Query("SELECT * FROM kids LIMIT 1")
    suspend fun getKid(): BirthdayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKid(kid: BirthdayEntity)
}