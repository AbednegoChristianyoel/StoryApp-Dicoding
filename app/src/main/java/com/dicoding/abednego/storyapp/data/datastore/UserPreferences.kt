package com.dicoding.abednego.storyapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_token")

class UserPreferences(private val context: Context) {
    companion object {
        val USER_TOKEN_KEY = stringPreferencesKey("user_token_key")
    }

    suspend fun saveUserToken(userToken: String?) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = userToken ?: ""
        }
    }

    suspend fun clearUserToken() {
        context.dataStore.edit { it.clear() }
    }

    val isLogin = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }
}