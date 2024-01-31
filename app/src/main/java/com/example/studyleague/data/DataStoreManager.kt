package com.example.studyleague.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


const val DATA_STORE_NAME = "settings"

class DataStoreManager(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

    suspend fun <T> getValueFromDataStore(key: Preferences.Key<T>): T? {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }.first()
    }

    suspend fun <T> setDataStoreValue(
        newValue: T, key: Preferences.Key<T>
    ) {
        context.dataStore.edit { settings ->
            settings[key] = newValue
        }
    }
}

sealed class DataStoreKeys {
    companion object {
        val studentIdKey: Preferences.Key<Long> = longPreferencesKey("studentId")
        val hasCompletedOnboardingKey: Preferences.Key<Boolean> = booleanPreferencesKey("hasCompletedOnboarding")
    }
}