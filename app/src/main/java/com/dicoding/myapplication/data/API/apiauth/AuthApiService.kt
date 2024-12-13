package com.dicoding.myapplication.data.API.apiauth

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Headers

interface AuthApiService {

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResultResponse>
}