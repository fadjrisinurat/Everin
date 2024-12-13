package com.dicoding.myapplication.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.myapplication.data.UserRepository
import com.dicoding.myapplication.data.preference.authpreference.UserModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getSession(): Flow<UserModel> {
        return repository.getSession()
    }
}
