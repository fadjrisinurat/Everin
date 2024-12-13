package com.dicoding.myapplication.view.main.recommendationfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.dicoding.myapplication.R
import com.dicoding.myapplication.data.API.apiml.MlApiConfig
import com.dicoding.myapplication.data.API.apiml.NutritionRequest
import com.dicoding.myapplication.data.API.apiml.RecommendResponse
import com.dicoding.myapplication.data.preference.AppPreference
import com.dicoding.myapplication.data.preference.preferenceDataStore
import com.dicoding.myapplication.view.main.HomeFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RecommendationFoodFragment : DialogFragment() {

    private lateinit var inputCarbohydrate: TextInputEditText
    private lateinit var inputProtein: TextInputEditText
    private lateinit var inputFat: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recommendation_food, container, false)

        inputCarbohydrate = view.findViewById(R.id.inputcarbohydrate)
        inputProtein = view.findViewById(R.id.inputProtein)
        inputFat = view.findViewById(R.id.inputfat)

        view.findViewById<View>(R.id.calculate).setOnClickListener {
            calculateNutrition()
        }

        return view
    }

    private fun calculateNutrition() {
        val carbText = inputCarbohydrate.text.toString()
        val proteinText = inputProtein.text.toString()
        val fatText = inputFat.text.toString()

        if (carbText.isEmpty()) {
            inputCarbohydrate.error = "Karbohidrat tidak boleh kosong!"
            inputCarbohydrate.requestFocus()
            return
        }
        if (proteinText.isEmpty()) {
            inputProtein.error = "Protein tidak boleh kosong!"
            inputProtein.requestFocus()
            return
        }
        if (fatText.isEmpty()) {
            inputFat.error = "Lemak tidak boleh kosong!"
            inputFat.requestFocus()
            return
        }

        val carb = carbText.toFloatOrNull() ?: 0f
        val protein = proteinText.toFloatOrNull() ?: 0f
        val fat = fatText.toFloatOrNull() ?: 0f

        val nutritionRequest = NutritionRequest(carb, protein, fat)

        lifecycleScope.launch {
            try {
                val response = MlApiConfig.getApiService().recommend(nutritionRequest)
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        AppPreference.getInstance(requireContext().preferenceDataStore)
                            .saveRecommendation(it)
                    }
                    sendResultToHomeFragment(result)
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendResultToHomeFragment(result: RecommendResponse?) {
        val parentFragment = parentFragmentManager.findFragmentByTag("HomeFragment") as? HomeFragment
        if (result?.recommendations?.isNotEmpty() == true) {
            parentFragment?.updateRecommendation(result)
        } else {
            Toast.makeText(requireContext(), "No recommendations available", Toast.LENGTH_SHORT).show()
        }

        dismiss()
    }
}
