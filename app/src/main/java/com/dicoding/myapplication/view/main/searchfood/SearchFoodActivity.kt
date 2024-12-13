package com.dicoding.myapplication.view.main.searchfood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myapplication.data.API.apiml.FoodRequest
import com.dicoding.myapplication.data.API.apiml.MlApiConfig
import com.dicoding.myapplication.data.API.apiml.RecommendByNameResponse
import com.dicoding.myapplication.databinding.ActivitySearchFoodBinding
import com.dicoding.myapplication.view.main.MainActivity
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchFoodBinding
    private lateinit var foodListAdapter: FoodListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foodListAdapter = FoodListAdapter(emptyList()) { foodItem ->
            val intent = Intent(this@SearchFoodActivity, MainActivity::class.java).apply {
                putExtra("food_name", foodItem)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchFoodActivity)
            adapter = foodListAdapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        searchFood(it)
                    } else {
                        Toast.makeText(this@SearchFoodActivity, "Masukkan nama makanan", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun searchFood(foodName: String) {
        lifecycleScope.launch {
            try {
                val foodRequest = FoodRequest(foodName)
                val response: Response<RecommendByNameResponse> = MlApiConfig.getApiService()
                    .recommendByName(foodRequest)

                if (response.isSuccessful) {
                    val foodList = response.body()?.recommendations?.filterNotNull() ?: emptyList()
                    foodListAdapter.updateFoodList(foodList)
                } else {
                    Toast.makeText(this@SearchFoodActivity, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SearchFoodActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
