package com.dicoding.myapplication.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.dicoding.myapplication.R
import com.dicoding.myapplication.data.API.apiml.RecommendResponse
import com.dicoding.myapplication.view.ViewModelFactory
import com.dicoding.myapplication.view.login.LoginViewModel
import com.dicoding.myapplication.view.main.heightweight.InputHeightWeightDialogFragment
import com.dicoding.myapplication.view.main.recommendationfood.RecommendationFoodFragment
import com.bumptech.glide.Glide
import com.dicoding.myapplication.data.API.apiml.StatusResponse
import com.dicoding.myapplication.data.preference.AppPreference
import com.dicoding.myapplication.data.preference.authpreference.UserModel
import com.dicoding.myapplication.data.preference.authpreference.UserPreference
import com.dicoding.myapplication.data.preference.authpreference.dataStore
import com.dicoding.myapplication.data.preference.preferenceDataStore

class HomeFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val appPreference = AppPreference.getInstance(requireContext().preferenceDataStore)


        lifecycleScope.launchWhenStarted {
            userPreference.getSession()
                .flowWithLifecycle(lifecycle)
                .collect { user ->
                    updateUserUI(view, user)
                }
        }

        lifecycleScope.launchWhenStarted {
            AppPreference.getInstance(requireContext().preferenceDataStore)
                .getRecommendation()
                .collect { recommendation ->
                    recommendation?.let {
                        updateRecommendation(it)
                    }
                }
        }

        lifecycleScope.launchWhenStarted {
            appPreference.getStatus()
                .collect { nutritionData ->
                    nutritionData?.let {
                        updateNutritionData(view, it)
                    }
                }
        }

        lifecycleScope.launchWhenStarted {
            appPreference.getNutrition()
                .collect { nutritionData ->
                    nutritionData?.let {
                        updateNutritionActivity(
                            bmr = it.bmr ?: 0f,
                            tdee = it.tdee ?: 0f,
                            karbohidrat = it.kebutuhanHarian?.karbohidrat ?: 0f,
                            lemak = it.kebutuhanHarian?.lemak ?: 0f,
                            protein = it.kebutuhanHarian?.protein ?: 0f
                        )
                    }
                }
        }


        val cardView3 = view.findViewById<CardView>(R.id.cardView3)
        cardView3.setOnClickListener {
            val dialog = InputHeightWeightDialogFragment()
            dialog.show(parentFragmentManager, "InputHeightWeightDialog")
        }

        val cardView4 = view.findViewById<CardView>(R.id.cardView4)
        cardView4.setOnClickListener {
            val dialog = RecommendationFoodFragment()
            dialog.show(parentFragmentManager, "RecommendationFoodDialog")
        }

        return view
    }

    //update bagian atas yang ada namanya
    private fun updateUserUI(view: View, user: UserModel) {
        val helloText = view.findViewById<TextView>(R.id.helloText)
        val heightText = view.findViewById<TextView>(R.id.heightText)
        val weightText = view.findViewById<TextView>(R.id.WeightText)

        helloText.text = "Hello ${user.name}"
        heightText.text = "Height: ${user.height}"
        weightText.text = "Weight: ${user.weight}"
    }

    //update bagian rekomendasi
    fun updateRecommendation(result: RecommendResponse?) {
        if (result != null && result.recommendations.isNotEmpty()) {
            val firstRecommendation = result.recommendations.first()
            firstRecommendation?.let { recommendation ->
                view?.apply {
                    findViewById<TextView>(R.id.rekomendasi_carbo)?.text =
                        "Carbohydrate: ${recommendation.nutrition?.karbohidrat ?: 0f} g"
                    findViewById<TextView>(R.id.rekomendasi_protein)?.text =
                        "Protein: ${recommendation.nutrition?.protein ?: 0f} g"
                    findViewById<TextView>(R.id.rekomendasi_fat)?.text =
                        "Fat: ${recommendation.nutrition?.lemak ?: 0f} g"
                    findViewById<TextView>(R.id.rekomendasi_calories)?.text =
                        "Calories: ${recommendation.nutrition?.kalori ?: 0f} kcal"

                    findViewById<TextView>(R.id.nama_makanan)?.text = recommendation.nama

                    val imageUrl =
                        "https://storage.googleapis.com/everin-bucket/image-search/${recommendation.nama.lowercase()}.jpg"

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.jeruk)
                        .into(findViewById<ImageView>(R.id.imgrekomendasi))
                }
            }
        } else {

        }
    }

    //update nutrisi di data bagian ata
    private fun updateNutritionData(view: View, nutritionData: StatusResponse) {
        val calories = nutritionData.kalori
        val carbo = nutritionData.karbohidrat
        val fat = nutritionData.lemak

        view.findViewById<TextView>(R.id.caloriesnumber).text = "${calories.toInt()} /kcal"
        view.findViewById<TextView>(R.id.carbonumber).text = "${carbo.toInt()} /g"
        view.findViewById<TextView>(R.id.fatnumber).text = "${fat.toInt()} /g"
    }


    //update nutrisi di activity
    fun updateNutritionActivity(
        bmr: Float,
        tdee: Float,
        karbohidrat: Float,
        lemak: Float,
        protein: Float
    ) {
        view?.apply {
            findViewById<TextView>(R.id.bmrnumber).text = "${bmr.toInt()} kcal/day"
            findViewById<TextView>(R.id.tdeenumber).text = "${tdee.toInt()} kcal/day"
            findViewById<TextView>(R.id.calculatecarbo).text = "${karbohidrat.toInt()} g/day"
            findViewById<TextView>(R.id.calculatefat).text = "${lemak.toInt()} g/day"
            findViewById<TextView>(R.id.calculateprotein).text = "${protein.toInt()} g/day"
        }
    }
}
