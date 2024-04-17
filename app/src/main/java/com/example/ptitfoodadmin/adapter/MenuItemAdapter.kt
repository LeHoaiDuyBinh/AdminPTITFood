package com.example.ptitfoodadmin.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ptitfoodadmin.AddMenuActivity

import com.example.ptitfoodadmin.databinding.ItemItemBinding
import com.example.ptitfoodadmin.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

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
                            string += menuItem.foodTopping[i].toppingName
                        } else string += menuItem.foodTopping[i].toppingName + ", "
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
                //Khi nhấn edit_menu của AllMenu
//                editMenu.setOnClickListener {
//                    val intent = Intent(context, AddMenuActivity::class.java)
//                    context.startActivity(intent)
//                }

                // Kiểm tra quyền truy cập của người dùng
                val currentUser = FirebaseAuth.getInstance().currentUser
                val email = "adminptitfood@ptitfoodadmin.edu.vn"
                currentUser?.let { user ->
                    val databaseReference = FirebaseDatabase.getInstance().getReference("Admin").child(sha1(user.email.toString()))
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val role = dataSnapshot.child("role").getValue(String::class.java)
                            if (user.email == email || role == "Quản lí") {
                                // Hiển thị nút sửa và xóa cho email hoặc vai trò là Quản lí
                                editMenu.visibility = View.VISIBLE
                                deleteButton.visibility = View.VISIBLE
                            } else {
                                editMenu.visibility = View.GONE // Ẩn nút sửa
                                deleteButton.visibility = View.GONE // Ẩn nút xóa
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("AdminAdapter", "Đã xảy ra lỗi: ${databaseError.message}")
                            Toast.makeText(context, "Đã xảy ra lỗi: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                editMenu.setOnClickListener {
                    val intent = Intent(context, AddMenuActivity::class.java)
                    intent.putExtra("Menu", menuItem.foodName)
                    context.startActivity(intent)
                }
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
    fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}