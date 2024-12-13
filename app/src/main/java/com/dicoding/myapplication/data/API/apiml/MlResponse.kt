package com.dicoding.myapplication.data.API.apiml

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RecommendByNameResponse(
	@field:SerializedName("input_food")
	val inputFood: InputFood? = null,

	@field:SerializedName("recommendations")
	val recommendations: List<RecommendationsItem?>? = null
) : Serializable

data class FoodRequest(
	@SerializedName("food_name")
	val foodName: String
) : Serializable

data class CalculateRequest(
	val weight: Int,
	val height: Int,
	val age: Int,
	val gender: String,
	val activity_level: String
) : Serializable

data class CalculateResponse(
	@field:SerializedName("bmr")
	val bmr: Float? = null,

	@field:SerializedName("kebutuhan_harian")
	val kebutuhanHarian: KebutuhanHarian? = null,

	@field:SerializedName("tdee")
	val tdee: Float? = null
) : Serializable

data class KebutuhanHarian(
	@field:SerializedName("karbohidrat")
	val karbohidrat: Float? = null,

	@field:SerializedName("lemak")
	val lemak: Float? = null,

	@field:SerializedName("protein")
	val protein: Float? = null
) : Serializable

data class RecommendResponse(
	val inputValues: InputValues? = null,
	val recommendations: List<Recommendation> = emptyList()
): Serializable

data class PredictResponse(
	@field:SerializedName("food_name")
	val foodName: String? = null,

	@field:SerializedName("nutrition")
	val nutrition: Nutrition? = null,

	@field:SerializedName("confidence")
	val confidence: Any? = null,

	@field:SerializedName("recommendations")
	val recommendations: List<RecommendationsItem?>? = null
) : Serializable

data class Nutrition(
	@field:SerializedName("kalori")
	val kalori: Float? = null,

	@field:SerializedName("karbohidrat")
	val karbohidrat: Float? = null,

	@field:SerializedName("protein")
	val protein: Float? = null,

	@field:SerializedName("lemak")
	val lemak: Float? = null
) : Serializable

data class InputFood(
	@field:SerializedName("nutrition")
	val nutrition: Nutrition? = null,

	@field:SerializedName("nama")
	val nama: String? = null
): Serializable

data class RecommendationsItem(
	@field:SerializedName("nutrition")
	val nutrition: Nutrition? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("similarity_score")
	val similarityScore: Float? = null
) : Serializable

data class NutritionRequest(
	val karbohidrat: Float,
	val protein: Float,
	val lemak: Float
): Serializable

data class InputValues(
	val karbohidrat: Float,
	val protein: Float,
	val lemak: Float
): Serializable

data class Recommendation(
	val nama: String,
	val nutrition: Nutrition,
	val similarityScore: Float
): Serializable

data class SubmitRequest(
	@SerializedName("email")
	val email: String,

	@SerializedName("nutrition")
	val nutrition: Nutrition
): Serializable

data class SubmitResponse(
	@SerializedName("message")
	val message: String,

	@SerializedName("status")
	val status: String,

	@SerializedName("totals")
	val totals: Nutrition
): Serializable

data class StatusResponse(
	@SerializedName("email")
	val email: String,

	@SerializedName("kalori")
	val kalori: Double,

	@SerializedName("karbohidrat")
	val karbohidrat: Double,

	@SerializedName("lemak")
	val lemak: Double,

	@SerializedName("protein")
	val protein: Double,

	@SerializedName("status")
	val status: String
) : Serializable


