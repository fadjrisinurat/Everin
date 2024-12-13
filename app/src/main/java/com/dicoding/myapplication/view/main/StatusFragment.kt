package com.dicoding.myapplication.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dicoding.myapplication.data.API.apiml.MlApiConfig
import com.dicoding.myapplication.data.API.apiml.StatusResponse
import com.dicoding.myapplication.data.preference.AppPreference
import com.dicoding.myapplication.data.preference.authpreference.UserPreference
import com.dicoding.myapplication.data.preference.authpreference.dataStore
import com.dicoding.myapplication.data.preference.preferenceDataStore
import com.dicoding.myapplication.databinding.FragmentStatusBinding
import kotlinx.coroutines.launch

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding
    private lateinit var appPreference: AppPreference
    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreference = AppPreference.getInstance(requireContext().preferenceDataStore)
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        displayCachedStatus()

        fetchUserStatus()
    }

    private fun displayCachedStatus() {
        lifecycleScope.launch {
            appPreference.getStatus().collect { cachedStatus ->
                cachedStatus?.let {
                    updateUI(it)
                }
            }
        }
    }

    private fun fetchUserStatus() {
        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                if (user.isLogin) {
                    val email = user.email
                    fetchStatusFromApi(email)
                } else {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun fetchStatusFromApi(email: String) {
        try {
            val response = MlApiConfig.getApiService().getStatus(email)
            if (response.isSuccessful) {
                response.body()?.let { statusResponse ->
                    appPreference.saveStatus(statusResponse)

                    updateUI(statusResponse)
                }
            } else {

            }
        } catch (e: Exception) {

        }
    }

    private fun updateUI(status: StatusResponse) {
        with(binding) {
            yourCalories.text = status.kalori.toString()
            yourProtein.text = status.protein.toString()
            yourCarbohydrate.text = status.karbohidrat.toString()
            yourFat.text = status.lemak.toString()
        }
    }
}
