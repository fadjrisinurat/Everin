package com.dicoding.myapplication.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.myapplication.R
import com.dicoding.myapplication.data.preference.authpreference.UserPreference
import com.dicoding.myapplication.data.preference.authpreference.dataStore
import com.dicoding.myapplication.view.ViewModelFactory
import com.dicoding.myapplication.view.login.LoginActivity
import com.dicoding.myapplication.view.login.LoginViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileFragment : Fragment() {

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userPreference.getSession().onEach { user ->
            val nameTextView: TextView = view.findViewById(R.id.nameTextView)
            val heightTextView: TextView = view.findViewById(R.id.number_height)
            val weightTextView: TextView = view.findViewById(R.id.number_weight)
            val ageTextView: TextView = view.findViewById(R.id.number_age)
            val genderTextView: TextView = view.findViewById(R.id.gender_text)

            nameTextView.text = user.name
            heightTextView.text = "${user.height} cm"
            weightTextView.text = "${user.weight} kg"
            ageTextView.text = "${user.age} years"
            genderTextView.text = user.gender
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        val signOutButton: Button = view.findViewById(R.id.signupbutton)
        signOutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }
}

