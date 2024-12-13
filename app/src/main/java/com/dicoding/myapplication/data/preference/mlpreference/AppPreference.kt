package com.dicoding.myapplication.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.myapplication.data.API.apiml.CalculateResponse
import com.dicoding.myapplication.data.API.apiml.KebutuhanHarian
import com.dicoding.myapplication.data.API.apiml.RecommendResponse
import com.dicoding.myapplication.data.API.apiml.StatusResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveRecommendation(recommendation: RecommendResponse) {
        val jsonString = Gson().toJson(recommendation)
        dataStore.edit { preferences ->
            preferences[RECOMMENDATION_KEY] = jsonString
        }
    }

    fun getRecommendation(): Flow<RecommendResponse?> {
        return dataStore.data.map { preferences ->
            preferences[RECOMMENDATION_KEY]?.let { jsonString ->
                Gson().fromJson(jsonString, RecommendResponse::class.java)
            }
        }
    }

    suspend fun saveStatus(status: StatusResponse) {
        val jsonString = Gson().toJson(status)
        dataStore.edit { preferences ->
            preferences[STATUS_KEY] = jsonString
        }
    }

    fun getStatus(): Flow<StatusResponse?> {
        return dataStore.data.map { preferences ->
            preferences[STATUS_KEY]?.let { jsonString ->
                Gson().fromJson(jsonString, StatusResponse::class.java)
            }
        }
    }

    suspend fun saveNutrition(bmr: Float, tdee: Float, karbohidrat: Float, lemak: Float, protein: Float) {

        val kebutuhanHarian = KebutuhanHarian(
            karbohidrat = karbohidrat,
            lemak = lemak,
            protein = protein
        )

        val nutritionData = CalculateResponse(
            bmr = bmr,
            tdee = tdee,
            kebutuhanHarian = kebutuhanHarian
        )

        val jsonString = Gson().toJson(nutritionData)
        dataStore.edit { preferences ->
            preferences[NUTRITION_KEY] = jsonString
        }
    }

    fun getNutrition(): Flow<CalculateResponse?> {
        return dataStore.data.map { preferences ->
            preferences[NUTRITION_KEY]?.let { jsonString ->
                Gson().fromJson(jsonString, CalculateResponse::class.java)
            }
        }
    }

    companion object {
        private val RECOMMENDATION_KEY = stringPreferencesKey("recommendation_key")
        private val STATUS_KEY = stringPreferencesKey("status_key")
        private val NUTRITION_KEY = stringPreferencesKey("nutrition_key")

        @Volatile
        private var INSTANCE: AppPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
