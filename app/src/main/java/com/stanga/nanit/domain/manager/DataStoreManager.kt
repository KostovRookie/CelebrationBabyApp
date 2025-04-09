package com.stanga.nanit.domain.manager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(context: Context) {
    private val Context.dataStore by preferencesDataStore("kid_details")
    private val dataStore = context.dataStore

    companion object {
        val PICTURE_URI_KEY = stringPreferencesKey("picture_uri")
    }

    suspend fun savePictureUri(uri: String) {
        dataStore.edit { preferences ->
            preferences[PICTURE_URI_KEY] = uri
        }
    }

    fun getPictureUri(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PICTURE_URI_KEY]
        }
    }
}