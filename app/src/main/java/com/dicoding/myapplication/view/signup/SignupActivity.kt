package com.dicoding.myapplication.view.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.myapplication.data.API.apiauth.AuthApiConfig
import com.dicoding.myapplication.data.API.apiauth.RegisterRequest
import com.dicoding.myapplication.databinding.ActivitySignupBinding
import com.dicoding.myapplication.view.login.LoginActivity
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmpasswordEditText.text.toString()
            val height = binding.HeightEditText.text.toString()
            val weight = binding.WeightEditText.text.toString()
            val age = binding.AgeEditText.text.toString()

            val selectedGenderId = binding.radioGroupGender.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                binding.radioButtonMen.id -> "man"
                binding.radioButtonWoman.id -> "women"
                else -> ""
            }

            if (validateInput(name, email, password, confirmPassword, height, weight, age, gender)) {
                registerUser(name, email, password, confirmPassword, height, weight, age, gender)
            }
        }
    }


    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        height: String,
        weight: String,
        age: String,
        gender: String?
    ): Boolean {
        when {
            name.isEmpty() -> {
                binding.nameEditTextLayout.error = "Name is required"
                return false
            }

            email.isEmpty() -> {
                binding.emailEditTextLayout.error = "Email is required"
                return false
            }

            password.isEmpty() -> {
                binding.passwordEditTextLayout.error = "Password is required"
                return false
            }

            confirmPassword.isEmpty() -> {
                binding.confirmpasswordEditTextLayout.error = "Confirm password is required"
                return false
            }

            password != confirmPassword -> {
                binding.confirmpasswordEditTextLayout.error = "Password does not match"
                return false
            }

            height.isEmpty() -> {
                binding.HeightEditTextLayout.error = "Height is required"
                return false
            }

            weight.isEmpty() -> {
                binding.WeightEditTextLayout.error = "Weight is required"
                return false
            }

            age.isEmpty() -> {
                binding.AgeEditTextLayout.error = "Age is required"
                return false
            }

            gender.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
                return false
            }

            else -> {
                binding.nameEditTextLayout.error = null
                binding.emailEditTextLayout.error = null
                binding.passwordEditTextLayout.error = null
                binding.confirmpasswordEditTextLayout.error = null
                binding.HeightEditTextLayout.error = null
                binding.WeightEditTextLayout.error = null
                binding.AgeEditTextLayout.error = null
                return true
            }
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        height: String,
        weight: String,
        age: String,
        gender: String
    ) {
        val heightInt = height.toIntOrNull() ?: 0
        val weightInt = weight.toIntOrNull() ?: 0
        val ageInt = age.toIntOrNull() ?: 0

        val apiService = AuthApiConfig.getApiService()
        val registerRequest = RegisterRequest(
            fullname = name,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            height = heightInt.toString(),
            weight = weightInt.toString(),
            age = ageInt.toString(),
            gender = gender
        )

        lifecycleScope.launch {
            try {
                val response = apiService.register(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()
                    Toast.makeText(
                        this@SignupActivity,
                        registerResponse?.message ?: "Registration successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        "Registration failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SignupActivity,
                    "Terjadi kesalahan:",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
