package com.diba.katuni.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

interface PreferencesRepository {
    fun getComicsFolder(): Flow<String?>
    suspend fun saveComicsFolder(uri: String)
    suspend fun clearComicsFolder()
}

class PreferencesRepositoryImpl(
    private val context: Context
): PreferencesRepository {
    private object PreferenceKeys {
        val COMICS_FOLDER_URI = stringPreferencesKey("comics_folder_uri")
    }

    override fun getComicsFolder(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.COMICS_FOLDER_URI]
        }
    }

    override suspend fun saveComicsFolder(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.COMICS_FOLDER_URI] = uri
        }
    }

    override suspend fun clearComicsFolder() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.COMICS_FOLDER_URI)
        }
    }
}