package com.dicoding.myapplication.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.myapplication.data.API.apiauth.AuthApiConfig
import com.dicoding.myapplication.data.API.apiauth.LoginRequest
import com.dicoding.myapplication.data.API.apiauth.LoginResultResponse
import com.dicoding.myapplication.data.preference.authpreference.UserModel
import com.dicoding.myapplication.databinding.ActivityLoginBinding
import com.dicoding.myapplication.view.main.MainActivity
import com.dicoding.myapplication.view.ViewModelFactory
import com.dicoding.myapplication.view.signup.SignupActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                login(email, password)
            }
        }

        val daftarTextView: TextView = binding.daftar
        daftarTextView.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                binding.emailEditTextLayout.error = "Email must be filled in"
                return false
            }
            password.isEmpty() -> {
                binding.passwordEditTextLayout.error = "Password must be filled in"
                return false
            }
            else -> {
                binding.emailEditTextLayout.error = null
                binding.passwordEditTextLayout.error = null
                return true
            }
        }
    }

    private fun login(email: String, password: String) {
        val apiService = AuthApiConfig.getApiService()
        val loginRequest = LoginRequest(email, password)

        lifecycleScope.launch {
            try {
                val response = apiService.login(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse: LoginResultResponse = response.body()!!

                    if (loginResponse.error) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val userId = loginResponse.loginResult.userId
                        val name = loginResponse.loginResult.name
                        val token = loginResponse.loginResult.token
                        val height = loginResponse.loginResult.height
                        val weight = loginResponse.loginResult.weight
                        val age = loginResponse.loginResult.age
                        val gender = loginResponse.loginResult.gender

                        if (token.isNullOrEmpty()) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login failed: Token not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val userModel = UserModel(
                                email = email,
                                token = token,
                                name = name,
                                height = height.toString(),
                                weight = weight.toString(),
                                age = age.toString(),
                                gender = gender
                            )
                            viewModel.saveSession(userModel)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("userId", userId)
                            intent.putExtra("name", name)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}
