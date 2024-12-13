package com.dicoding.myapplication.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.myapplication.data.UserRepository
import com.dicoding.myapplication.di.Injection
import com.dicoding.myapplication.view.main.MainViewModel
import com.dicoding.myapplication.view.login.LoginViewModel

class ViewModelFactory private constructor(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    if (INSTANCE == null) {
                        val userRepository = Injection.provideRepository(context.applicationContext)
                        INSTANCE = ViewModelFactory(userRepository)
                    }
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
