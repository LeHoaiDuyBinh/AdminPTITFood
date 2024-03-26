package com.example.ptitfoodadmin.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.ptitfoodadmin.databinding.ItemItemBinding
import com.example.ptitfoodadmin.model.AllMenu
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>(){
    private val itemQuantities = IntArray(menuList.size){1}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount(): Int = menuList.size
    inner class AddItemViewHolder(private val binding: ItemItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                val menuItem = menuList[position]
                val urisString = menuItem.foodImage
                val uri = Uri.parse(urisString)
                foodNameTextView.text = menuItem.foodName
                priceTextView.text = menuItem.foodPrice
                if (menuItem.foodTopping != null && menuItem.foodTopping.isNotEmpty()) {
//                ingredientTextView.text = menuItem.foodTopping
                    var string = ""
                    for (i in 0 until menuItem.foodTopping.size) {
                        if (i == menuItem.foodTopping.size - 1) {
                            string += menuItem.foodTopping[i].name
                        } else string += menuItem.foodTopping[i].name + ", "
                    }
                    ingredientTextView.text = string
                    textView10.visibility = View.VISIBLE // Hiển thị chữ "Ăn thêm"
                    ingredientTextView.visibility = View.VISIBLE // Hiển thị đồ ăn thêm
                } else {
                    ingredientTextView.text = "" // Đặt văn bản thành rỗng
                    textView10.visibility = View.GONE // Ẩn chữ "Ăn thêm"
                    ingredientTextView.visibility = View.GONE // Ẩn nguyên liệu
                }
                Glide.with(context).load(uri).into(foodImageView)
                deleteButton.setOnClickListener {
                    deleteQuantity(position)
                }
            }
        }
        private fun deleteQuantity(position: Int) {
            menuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, menuList.size)
        }
    }
}