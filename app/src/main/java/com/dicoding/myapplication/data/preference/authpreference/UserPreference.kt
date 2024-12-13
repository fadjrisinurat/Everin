package com.dicoding.myapplication.data.preference.authpreference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[NAME_KEY] = user.name
            preferences[IS_LOGIN_KEY] = true
            preferences[HEIGHT_KEY] = user.height
            preferences[WEIGHT_KEY] = user.weight
            preferences[AGE_KEY] = user.age
            preferences[GENDER_KEY] = user.gender
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                email = preferences[EMAIL_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false,
                height = preferences[HEIGHT_KEY] ?: "",
                weight = preferences[WEIGHT_KEY] ?: "",
                age = preferences[AGE_KEY] ?: "",
                gender = preferences[GENDER_KEY] ?: ""
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val NAME_KEY = stringPreferencesKey("name")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val HEIGHT_KEY = stringPreferencesKey("height")
        private val WEIGHT_KEY = stringPreferencesKey("weight")
        private val AGE_KEY = stringPreferencesKey("age")
        private val GENDER_KEY = stringPreferencesKey("gender")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
