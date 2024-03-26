package br.studyleague.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


const val DATA_STORE_NAME = "settings"

class DataStoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)
    }

    suspend fun <T> getValue(key: Preferences.Key<T>): T? {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }.first()
    }

    suspend fun <T> setValue(
        key: Preferences.Key<T>, newValue: T
    ) {
        context.dataStore.edit { settings ->
            settings[key] = newValue
        }
    }
}

object DataStoreKeys {
    val startupScreenKey: Preferences.Key<String> = stringPreferencesKey("startupScreen")
    val studentIdKey: Preferences.Key<Long> = longPreferencesKey("studentId")
}