package com.example.ptitfoodadmin

import com.example.ptitfoodadmin.adapter.AddFoddItemMenuAdapter
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.databinding.ActivityAddMenuBinding
import com.example.ptitfoodadmin.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddMenuActivity : AppCompatActivity(), AddFoddItemMenuAdapter.OnIngredientCheckedChangeListener {
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private var foodIngredient: String? = null

    private var foodImageUri: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddFoddItemMenuAdapter

    private val binding: ActivityAddMenuBinding by lazy {
        ActivityAddMenuBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val foodList = listOf(
            Pair("Chả lụa", "12.000đ"),
            Pair("Thịt bò", "20.000đ"),
            Pair("Xúc xích", "15.000đ")
        )

        recyclerView = findViewById(R.id.recyclerView)
        adapter = AddFoddItemMenuAdapter(this, foodList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        adapter.setOnIngredientCheckedChangeListener(this)

        binding.AddItemButton.setOnClickListener {

            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.decription.text.toString().trim()

            if (!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank())) {
                uploadData()
                Toast.makeText(this, "Thêm món ăn thành công!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Vui lòng điền vào ô trống! ", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun uploadData() {
        val menuRef = database.getReference("menu")
        val newItemKey = menuRef.push().key

        if (foodImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val newItem = AllMenu(
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDecription = foodDescription,
                        foodImage = downloadUrl.toString(),
                        foodIngredient = foodIngredient
                    )
                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(
                                this, "Dữ liệu đã được cập nhật thành công!", Toast.LENGTH_SHORT
                            ).show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Dữ liệu cập nhật thất bại!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Cập nhật hình ảnh thất bại!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Hãy chọn hình ảnh cho món ăn!", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.selectedImage.setImageURI(uri)
                foodImageUri = uri
            }
        }

    override fun onIngredientCheckedChange(ingredient: String?) {
        foodIngredient = ingredient
        Log.d("AddMenuActivity", "Selected Ingredients: $foodIngredient")
    }
}