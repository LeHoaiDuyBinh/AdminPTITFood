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

class AddFoddItemMenuAdapter(private val context: Context, private val foods: List<Pair<String, String>>) :
    RecyclerView.Adapter<AddFoddItemMenuAdapter.FoodViewHolder>() {

    private var selectedIngredients: MutableList<String> = mutableListOf()
    private var ingredientCheckedChangeListener: OnIngredientCheckedChangeListener? = null
    private var foodTopping: MutableList<FoodTopping> = mutableListOf()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val (foodName, price) = foods[position]
        holder.bind(foodName, price, selectedIngredients.contains(foodName))
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_price)

        fun bind(foodName: String, price: String, isChecked: Boolean) {
            checkbox.text = foodName
            priceTextView.text = price
            checkbox.isChecked = isChecked

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedIngredients.add(foodName)
                } else {
                    selectedIngredients.remove(foodName)
                }
                ingredientCheckedChangeListener?.onIngredientsListChanged(selectedIngredients)
            }
        }
    }

    fun setOnIngredientCheckedChangeListener(listener: OnIngredientCheckedChangeListener) {
        ingredientCheckedChangeListener = listener
    }

    interface OnIngredientCheckedChangeListener {
        fun onIngredientsListChanged(selectedIngredients: List<String>)
    }

    fun setSelectedIngredients(selectedIngredients: List<String>) {
        this.selectedIngredients.clear()
        this.selectedIngredients.addAll(selectedIngredients)
        notifyDataSetChanged()
    }
    fun setFoodTopping(toppings: MutableList<FoodTopping>) {
        foodTopping = toppings
        notifyDataSetChanged() // Cập nhật lại adapter khi danh sách thay đổi
    }

    fun getFoodTopping(): MutableList<FoodTopping> {
        return foodTopping
    }


}
