package com.dicoding.myapplication.view.main.searchfood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.myapplication.R
import com.dicoding.myapplication.data.API.apiml.RecommendationsItem

class FoodListAdapter(
    private var foodList: List<RecommendationsItem>,
    private val onItemClick: (RecommendationsItem) -> Unit
) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.food_name)
        val foodCalories: TextView = itemView.findViewById(R.id.food_calories)
        val foodCarbo: TextView = itemView.findViewById(R.id.food_carbo)
        val foodFat: TextView = itemView.findViewById(R.id.food_fat)
        val foodImage: ImageView = itemView.findViewById(R.id.img_food)

        fun bind(food: RecommendationsItem) {
            foodName.text = food.nama
            foodCalories.text = "Calories: ${food.nutrition?.kalori ?: 0}"
            foodCarbo.text = "Carbohydrate: ${food.nutrition?.karbohidrat ?: 0}"
            foodFat.text = "Fat: ${food.nutrition?.lemak ?: 0}"

            val formattedName = food.nama?.replace(" ", " ")?.toLowerCase() ?: ""
            val imageUrl = "https://storage.googleapis.com/everin-bucket/image-search/$formattedName.jpg"

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.jeruk)
                .error(R.drawable.jeruk)
                .into(foodImage)

            itemView.setOnClickListener {
                onItemClick(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int = foodList.size

    fun updateFoodList(newFoodList: List<RecommendationsItem>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }
}

