package com.dicoding.myapplication.data.preference.authpreference

data class UserModel(
    val email: String,
    val token: String,
    val name: String = "",
    val isLogin: Boolean = true,
    val height: String,
    val weight: String,
    val age: String,
    val gender: String
)
