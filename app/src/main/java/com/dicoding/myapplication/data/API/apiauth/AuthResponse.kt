package com.dicoding.myapplication.data.API.apiauth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisterRequest(
    @SerializedName("fullname")
    val fullname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("confirmPassword")
    val confirmPassword: String,

    @SerializedName("height")
    val height: String,

    @SerializedName("weight")
    val weight: String,

    @SerializedName("age")
    val age: String,

    @SerializedName("gender")
    val gender: String
) : Serializable



data class RegisterResponse(
    @SerializedName("message")
    val message: String
) : Serializable


data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
) : Serializable


data class LoginResultResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("loginResult")
    val loginResult: LoginResponse
) : Serializable

data class LoginResponse(
    @SerializedName("userId")
    val userId: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("height")
    val height: Int = 0,

    @SerializedName("weight")
    val weight: Int = 0,

    @SerializedName("age")
    val age: Int = 0,

    @SerializedName("gender")
    val gender: String = "",

    @SerializedName("token")
    val token: String = ""
) : Serializable

