package com.diba.katuni.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diba.katuni.model.KatuniFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

interface PreferencesRepository {
    fun getComicsFolder(): Flow<String?>
    suspend fun saveComicsFolder(uri: String)
    suspend fun clearComicsFolder()

    fun getCachedFiles(): Flow<List<KatuniFile>>
    suspend fun cacheFiles(files: List<KatuniFile>)
    suspend fun clearCachedFiles()
}

class PreferencesRepositoryImpl(
    private val context: Context
) : PreferencesRepository {
    private object PreferenceKeys {
        val COMICS_FOLDER_URI = stringPreferencesKey("comics_folder_uri")
        val CACHED_FILES = stringPreferencesKey("cached_files")
    }

    private val json = Json { ignoreUnknownKeys = true }

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

    override fun getCachedFiles(): Flow<List<KatuniFile>> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[PreferenceKeys.CACHED_FILES]
            if (jsonString != null) {
                try {
                    json.decodeFromString<List<KatuniFile>>(jsonString)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    override suspend fun cacheFiles(files: List<KatuniFile>) {
        context.dataStore.edit { preferences ->
            val jsonString = json.encodeToString(files)
            preferences[PreferenceKeys.CACHED_FILES] = jsonString
        }
    }

    override suspend fun clearCachedFiles() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.CACHED_FILES)
        }
    }
}