package com.dicoding.myapplication.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.myapplication.R
import com.dicoding.myapplication.data.API.apiml.RecommendationsItem
import com.dicoding.myapplication.view.ViewModelFactory
import com.dicoding.myapplication.view.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.navigation_add -> {
                    loadFragment(AddFragment())
                    true
                }

                R.id.navigation_status -> {
                    loadFragment(StatusFragment())
                    true
                }

                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        if (savedInstanceState == null) {
            val foodItem = intent.getSerializableExtra("food_name") as? RecommendationsItem
            foodItem?.let {
                bottomNavigationView.selectedItemId = R.id.navigation_add
                loadFragmentWithFoodData(it)
            } ?: run {
                loadFragment(HomeFragment())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (fragment is HomeFragment) {
            fragmentTransaction.replace(R.id.fragment_container, fragment, "HomeFragment")
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment)
        }
        fragmentTransaction.commit()
    }

    private fun loadFragmentWithFoodData(foodItem: RecommendationsItem) {
        val addFragment = AddFragment().apply {
            arguments = Bundle().apply {
                putSerializable("food_name", foodItem)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, addFragment)
            .addToBackStack(null)
            .commit()
    }
}
