package com.example.ptitfoodadmin.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.FoodTopping

class AddFoddItemMenuAdapter(private val context: Context, private val foods: List<Pair<String, String>> ) :
    RecyclerView.Adapter<AddFoddItemMenuAdapter.FoodViewHolder>() {
    private var foodIngredient: String? = null
    private var ingredientCheckedChangeListener: OnIngredientCheckedChangeListener? = null
    private val selectedIngredients = mutableListOf<FoodTopping>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val (foodName, price) = foods[position]
        holder.bind(foodName, price)
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.ingredient)
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_price)


        fun bind(name: String, price: String) {
            checkbox.text = name
            priceTextView.text = price

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val ingredient = name
                if (isChecked) {
                    foodIngredient = if (foodIngredient.isNullOrEmpty()) {
                        ingredient
                    } else {
                        "$foodIngredient, $ingredient"
                    }
                } else {
                    foodIngredient = foodIngredient?.replace("$ingredient, ", "")
                }
                ingredientCheckedChangeListener?.onIngredientCheckedChange(foodIngredient)
            }
        }
    }

    fun setOnIngredientCheckedChangeListener(listener: OnIngredientCheckedChangeListener) {
        ingredientCheckedChangeListener = listener
    }

    interface OnIngredientCheckedChangeListener {
        fun onIngredientCheckedChange(ingredient: String?)
    }

    fun getSelectedIngredients(): String? {
        return foodIngredient
    }
}
