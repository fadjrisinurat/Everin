package com.dicoding.myapplication.data.API.apiml

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MlApiservice {

    @Headers("Content-Type: application/json")
    @POST("recommend-by-name")
    suspend fun recommendByName(
        @Body foodRequest: FoodRequest
    ): Response<RecommendByNameResponse>

    @Headers("Content-Type: application/json")
    @POST("calculate")
    suspend fun calculate(
        @Body request: CalculateRequest
    ): Response<CalculateResponse>

    @POST("recommend")
    suspend fun recommend(
        @Body nutrition: NutritionRequest
    ): Response<RecommendResponse>

    @Multipart
    @POST("predict")
    suspend fun predict(
        @Part file: MultipartBody.Part
    ): Response<PredictResponse>

    @Headers("Content-Type: application/json")
    @POST("submit")
    suspend fun submitNutrition(
        @Body submitRequest: SubmitRequest
    ): Response<SubmitResponse>

    @GET("status/{email}")
    suspend fun getStatus(
        @Path("email") email: String
    ): Response<StatusResponse>
}