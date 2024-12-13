package com.dicoding.myapplication.view.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.myapplication.R
import com.dicoding.myapplication.data.API.apiml.MlApiConfig
import com.dicoding.myapplication.data.API.apiml.Nutrition
import com.dicoding.myapplication.data.API.apiml.PredictResponse
import com.dicoding.myapplication.data.API.apiml.RecommendationsItem
import com.dicoding.myapplication.data.API.apiml.SubmitRequest
import com.dicoding.myapplication.data.preference.authpreference.UserPreference
import com.dicoding.myapplication.data.preference.authpreference.dataStore
import com.dicoding.myapplication.databinding.FragmentAddBinding
import com.dicoding.myapplication.view.main.imguri.getImageUri
import com.dicoding.myapplication.view.main.imguri.reduceFileImage
import com.dicoding.myapplication.view.main.imguri.uriToFile
import com.dicoding.myapplication.view.main.searchfood.SearchFoodActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private var foodData: RecommendationsItem? = null
    private lateinit var userPreference: UserPreference

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                processImage()
            } else {
                Toast.makeText(requireContext(), "Failed to take photo", Toast.LENGTH_SHORT).show()
                currentImageUri = null
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            searchBar.setOnClickListener {
                val intent = Intent(requireContext(), SearchFoodActivity::class.java)
                startActivity(intent)
            }

            imgCamera.setOnClickListener {
                checkAndRequestCameraPermission()
            }

            foodData = arguments?.getSerializable("food_name") as? RecommendationsItem

            foodData?.let { food ->
                displayResult(food)
            }

            btnSubmit.setOnClickListener {
                lifecycleScope.launch {
                    userPreference.getSession().collect { user ->
                        if (user.isLogin) {
                            val nutrition = foodData?.nutrition
                            if (nutrition != null) {
                                submitData(user.email, nutrition)
                            } else {

                            }
                        } else {

                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    requireContext(),
                    "Camera permission required",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun processImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "file", imageFile.name, requestImageFile
            )

            lifecycleScope.launch {
                try {
                    val response = MlApiConfig.getApiService().predict(multipartBody)
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            displayResult(data)
                        }
                    } else {

                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Eror: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } ?: Toast.makeText(requireContext(), "Gambar tidak ditemukan", Toast.LENGTH_SHORT).show()
    }

    private fun displayResult(data: Any) {
        with(binding) {
            if (data is PredictResponse) {
                val food = data.recommendations?.firstOrNull()
                food?.let {
                    with(binding) {
                        imgFood.setImageURI(currentImageUri)
                        foodName.text = data.foodName ?: " "
                        foodCalories.text = "Kalori: ${data.nutrition?.kalori ?: 0}"
                        foodCarbo.text = "Karbo: ${data.nutrition?.karbohidrat ?: 0}"
                        foodProtein.text = "Protein: ${data.nutrition?.protein ?: 0}"
                        foodFat.text = "Lemak: ${String.format("%.1f", data.nutrition?.lemak ?: 0.0)}"
                    }

                    foodData = RecommendationsItem(
                        nama = data.foodName,
                        nutrition = data.nutrition
                    )
                }
            } else if (data is RecommendationsItem) {
                foodName.text = data.nama ?: " "
                foodCalories.text = "Calories: ${data.nutrition?.kalori ?: 0}"
                foodCarbo.text = "Carbohydrate: ${data.nutrition?.karbohidrat ?: 0}"
                foodProtein.text = "Protein: ${data.nutrition?.protein ?: 0}"
                foodFat.text = "Fat: ${data.nutrition?.lemak ?: 0}"

                val formattedName = data.nama?.replace(" ", " ")?.toLowerCase() ?: ""
                val imageUrl = "https://storage.googleapis.com/everin-bucket/image-search/$formattedName.jpg"

                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.jeruk)
                    .error(R.drawable.jeruk)
                    .into(imgFood)
            } else {

            }
        }
    }

    private fun submitData(email: String, nutrition: Nutrition) {
        lifecycleScope.launch {
            try {
                val request = SubmitRequest(email, nutrition)
                val response = MlApiConfig.getApiService().submitNutrition(request)

                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(
                            requireContext(),
                            "Submit successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Submit failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {

            }
        }
    }



}
