package com.adityafakhri.storyapp.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adityafakhri.storyapp.utils.Const
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Const.preferenceName)

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val token = stringPreferencesKey(Const.UserPreferences.Token.name)

    fun getToken(): Flow<String> = dataStore.data.map { it[token] ?: Const.preferenceDefaultValue }

    suspend fun saveLoginSession(userToken: String) {
        dataStore.edit { preferences ->
            preferences[token] = userToken
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AuthPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}