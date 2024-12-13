package com.dicoding.myapplication.view.main.heightweight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dicoding.myapplication.R
import com.dicoding.myapplication.data.API.apiml.CalculateRequest
import com.dicoding.myapplication.data.API.apiml.MlApiConfig
import com.dicoding.myapplication.view.main.HomeFragment
import com.dicoding.myapplication.data.preference.AppPreference
import com.dicoding.myapplication.data.preference.preferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputHeightWeightDialogFragment : DialogFragment() {

    private var selectedCardId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input_height_weight_dialog, container, false)

        val cardLight = view.findViewById<CardView>(R.id.cardViewlight)
        val cardMedium = view.findViewById<CardView>(R.id.cardViewmedium)
        val cardHeavy = view.findViewById<CardView>(R.id.cardViewheavy)
        val calculateButton = view.findViewById<TextView>(R.id.calculate)
        val weightInput = view.findViewById<EditText>(R.id.inputweight)
        val heightInput = view.findViewById<EditText>(R.id.inputheight)
        val ageInput = view.findViewById<EditText>(R.id.inputage)
        val genderGroup = view.findViewById<RadioGroup>(R.id.radioGroupGender)

        cardLight.setOnClickListener { selectCard(cardLight, R.id.cardViewlight) }
        cardMedium.setOnClickListener { selectCard(cardMedium, R.id.cardViewmedium) }
        cardHeavy.setOnClickListener { selectCard(cardHeavy, R.id.cardViewheavy) }

        calculateButton.setOnClickListener {
            val weight = weightInput.text.toString().toIntOrNull()
            val height = heightInput.text.toString().toIntOrNull()
            val age = ageInput.text.toString().toIntOrNull()

            if (weight == null || height == null || age == null || genderGroup.checkedRadioButtonId == -1) {
                Toast.makeText(
                    requireContext(),
                    "Semua data harus diisi dengan benar",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val gender = when (genderGroup.checkedRadioButtonId) {
                R.id.radioButtonMen -> "pria"
                R.id.radioButtonWoman -> "wanita"
                else -> ""
            }

            val activityLevel = when (selectedCardId) {
                R.id.cardViewlight -> "ringan"
                R.id.cardViewmedium -> "sedang"
                R.id.cardViewheavy -> "berat"
                else -> ""
            }

            val request = CalculateRequest(
                weight = weight,
                height = height,
                age = age,
                gender = gender,
                activity_level = activityLevel
            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        MlApiConfig.getApiService().calculate(request)
                    }

                    if (response.isSuccessful) {
                        val calculateResponse = response.body()
                        calculateResponse?.let {
                            val bmr = it.bmr ?: 0f
                            val tdee = it.tdee ?: 0f
                            val kebutuhanHarian = it.kebutuhanHarian

                            val appPreference = AppPreference.getInstance(requireContext().preferenceDataStore)

                            appPreference.saveNutrition(
                                bmr = bmr,
                                tdee = tdee,
                                karbohidrat = kebutuhanHarian?.karbohidrat ?: 0f,
                                lemak = kebutuhanHarian?.lemak ?: 0f,
                                protein = kebutuhanHarian?.protein ?: 0f
                            )

                            sendResultToHomeFragment(
                                bmr = bmr,
                                tdee = tdee,
                                karbohidrat = kebutuhanHarian?.karbohidrat ?: 0f,
                                lemak = kebutuhanHarian?.lemak ?: 0f,
                                protein = kebutuhanHarian?.protein ?: 0f
                            )
                        }
                    } else {
                        Toast.makeText(requireContext(), "Perhitungan gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Kesalahan jaringan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun selectCard(selectedCard: CardView, cardId: Int) {
        resetCardAppearance()
        selectedCardId = cardId
        selectedCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.UltraLightGreen))
    }

    private fun resetCardAppearance() {
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.white)
        view?.findViewById<CardView>(R.id.cardViewlight)?.setCardBackgroundColor(defaultColor)
        view?.findViewById<CardView>(R.id.cardViewmedium)?.setCardBackgroundColor(defaultColor)
        view?.findViewById<CardView>(R.id.cardViewheavy)?.setCardBackgroundColor(defaultColor)
    }

    private fun sendResultToHomeFragment(bmr: Float, tdee: Float, karbohidrat: Float, lemak: Float, protein: Float) {
        val parentFragment = parentFragmentManager.findFragmentByTag("HomeFragment") as? HomeFragment
        parentFragment?.let {
            it.updateNutritionActivity(bmr, tdee, karbohidrat, lemak, protein)
        } ?: run {
            Toast.makeText(requireContext(), "HomeFragment not found", Toast.LENGTH_SHORT).show()
        }

        dismiss()
    }
}
