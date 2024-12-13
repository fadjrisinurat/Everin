package com.dicoding.myapplication.di

import android.content.Context
import com.dicoding.myapplication.data.preference.authpreference.UserPreference
import com.dicoding.myapplication.data.UserRepository
import com.dicoding.myapplication.data.preference.authpreference.dataStore


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference)
    }
}
