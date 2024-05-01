package com.example.ptitfoodadmin

import com.example.ptitfoodadmin.adapter.AddFoddItemMenuAdapter
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ptitfoodadmin.adapter.MenuItemAdapter
import com.example.ptitfoodadmin.databinding.ActivityAddMenuBinding
import com.example.ptitfoodadmin.model.AllMenu
import com.example.ptitfoodadmin.model.FoodTopping
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.OnSuccessListener




class AddMenuActivity : AppCompatActivity(), AddFoddItemMenuAdapter.OnIngredientCheckedChangeListener {
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private var foodIngredient: String? = null
//    private var foodTopping: MutableList<FoodTopping>? = null
    private lateinit var foodTopping: MutableList<FoodTopping>

    private var foodImageUri: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddFoddItemMenuAdapter
    private var selectedMenuKey: String? = null

    private val binding: ActivityAddMenuBinding by lazy {
        ActivityAddMenuBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val foodNameEdt = findViewById<EditText>(R.id.foodName)
        val foodPriceEdt = findViewById<EditText>(R.id.foodPrice)
        val foodDescriptionEdt = findViewById<EditText>(R.id.decription)
//        val foodToppingCb = findViewById<RecyclerView>(R.id.recyclerView)

        val foodList = listOf(
            Pair(getString(R.string.cha_lua), "12.000đ"),
            Pair(getString(R.string.thit_bo), "20.000đ"),
            Pair(getString(R.string.xuc_xich), "15.000đ"),
            Pair(getString(R.string.ca_chua), "10.000đ"),
            Pair(getString(R.string.bap_cai), "5.000đ")
        )

        recyclerView = findViewById(R.id.recyclerView)
        adapter = AddFoddItemMenuAdapter(this, foodList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        foodTopping = mutableListOf()

        adapter.setOnIngredientCheckedChangeListener(this)

        binding.AddItemButton.setOnClickListener {
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.decription.text.toString().trim()
            val splitTopping = foodIngredient?.split(", ")?.map { it.trim() }
            if (splitTopping != null) {
                for (item in splitTopping) {
                    // Tìm kiếm trong foodList xem có phần tử nào trùng với item không
                    val matchedFood = foodList.find { it.first == item }
                    // Nếu tìm thấy phần tử trùng khớp, thêm vào foodTopping
                    if (matchedFood != null) {
                        val foodToppingItem = FoodTopping(matchedFood.first, matchedFood.second)
                        foodTopping.add(foodToppingItem)
                    }
                }
            }
            Log.d("Topping", foodTopping.toString())
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
//Điền dữ liêu vào để sửa
        val selectedImage = findViewById<ImageView>(R.id.selectedImage)
        val menuName = intent.getStringExtra("Menu")
        val query = FirebaseDatabase.getInstance().getReference("Menu")
            .orderByChild("foodName")
            .equalTo(menuName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    selectedMenuKey = snapshot.key
                    val menuItemToEdit = snapshot.getValue(AllMenu::class.java)
                    foodNameEdt.setText(menuItemToEdit?.foodName)
                    foodPriceEdt.setText(menuItemToEdit?.foodPrice)
                    val imageUri = Uri.parse(menuItemToEdit?.foodImage)
                    Glide.with(this@AddMenuActivity).load(imageUri).into(selectedImage)
                    foodDescriptionEdt.setText(menuItemToEdit?.foodDescription)
                    // Lưu trữ URL hình ảnh hiện tại
                    val currentImageUrl = menuItemToEdit?.foodImage ?: ""
                    // Lấy danh sách topping của món ăn
                    val foodTopping = menuItemToEdit?.foodTopping
                    if (foodTopping != null) {
                        val selectedIngredients = foodTopping.filterNotNull().map { it.toppingName }
                        // Cập nhật dữ liệu vào adapter
                        adapter.setSelectedIngredients(selectedIngredients)
                    }
                    binding.EditItemButton.setOnClickListener {
                        foodName = binding.foodName.text.toString().trim()
                        foodPrice = binding.foodPrice.text.toString().trim()
                        foodDescription = binding.decription.text.toString().trim()
                        val splitTopping = foodIngredient?.split(", ")?.map { it.trim() }
                        if (splitTopping != null) {
//                            foodTopping?.clear()
                            val foodRef = FirebaseDatabase.getInstance().getReference("Menu").child(selectedMenuKey!!).child("foodTopping")
                            foodRef.removeValue().addOnSuccessListener {
                                Log.d("TAG", "Xóa foodTopping thành công!")
                                }
                                .addOnFailureListener {
                                    Log.e("TAG", "Xóa foodTopping thất bại!")
                                }
                            // Sử dụng hàm setFoodTopping để đặt danh sách foodTopping từ splitTopping
                            if (splitTopping != null) {
                                adapter.setFoodTopping(mutableListOf()) // Xóa danh sách cũ trước khi thêm mới
                                for (item in splitTopping) {
                                    val matchedFood = foodList.find { it.first == item }
                                    if (matchedFood != null) {
                                        val foodToppingItem = FoodTopping(matchedFood.first, matchedFood.second)
                                        // Thêm foodToppingItem vào danh sách foodTopping bằng cách gọi hàm add
                                        adapter.getFoodTopping().add(foodToppingItem)
                                    }
                                }
                                adapter.notifyDataSetChanged() // Cập nhật lại adapter
                            }
// Để lấy danh sách foodTopping hiện tại, sử dụng hàm getFoodTopping
                            val currentFoodToppings = adapter.getFoodTopping()
                        }
                        Log.d("Topping", foodTopping.toString())

                        if (!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank())) {
                            // Tìm đến nút dữ liệu cần cập nhật
                            val menuToUpdateRef =
                                database.getReference("Menu").child(selectedMenuKey!!)
                            // Kiểm tra xem hình ảnh mới có được chọn không
                            val imageUrl = if (foodImageUri != null) {
                                val storageRef = FirebaseStorage.getInstance().reference
                                val imageRef = storageRef.child("menu_images/$selectedMenuKey.jpg")
                                val uploadTask = imageRef.putFile(foodImageUri!!)
                                uploadTask.addOnCompleteListener { uploadTask ->
                                    if (uploadTask.isSuccessful) {
                                        imageRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                                            if (downloadUrlTask.isSuccessful) {
                                                val downloadUrl = downloadUrlTask.result
                                                val imageUrl = downloadUrl.toString()

                                                // Cập nhật dữ liệu
                                                val updatedItem = AllMenu(
                                                    foodName = foodName,
                                                    foodPrice = foodPrice,
                                                    foodDescription = foodDescription,
                                                    foodImage = imageUrl,
                                                    foodTopping = foodTopping ?: mutableListOf() // Sử dụng danh sách topping hiện tại
                                                )
                                                // Cập nhật dữ liệu
                                                menuToUpdateRef.setValue(updatedItem)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            this@AddMenuActivity,
                                                            "Cập nhật món ăn thành công!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        finish()
                                                    }.addOnFailureListener {
                                                        Toast.makeText(
                                                            this@AddMenuActivity,
                                                            "Cập nhật món ăn thất bại!",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            } else {
                                                // Xử lý khi không thể lấy được URL download
                                                Log.e(
                                                    "AddMenuActivity",
                                                    "Error getting download URL: ${downloadUrlTask.exception}"
                                                )
                                                Toast.makeText(
                                                    this@AddMenuActivity,
                                                    "Có lỗi xảy ra khi tải lên hình ảnh!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        // Xử lý khi không thể tải lên hình ảnh
                                        Log.e(
                                            "AddMenuActivity",
                                            "Error uploading image: ${uploadTask.exception}"
                                        )
                                        Toast.makeText(
                                            this@AddMenuActivity,
                                            "Có lỗi xảy ra khi tải lên hình ảnh!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                ""
                            } else {
                                currentImageUrl // Sử dụng URL hình ảnh hiện tại
                            }
                            splitTopping?.let { toppings ->
                                foodTopping?.removeIf { it.toppingName !in toppings }
                            }
// Cập nhật dữ liệu
                            val updatedItem = AllMenu(
                                foodName = foodName,
                                foodPrice = foodPrice,
                                foodDescription = foodDescription,
                                foodImage = imageUrl,
                                foodTopping = foodTopping
                                    ?: mutableListOf() // Sử dụng danh sách topping hiện tại
                            )
                            // Cập nhật dữ liệu
                            menuToUpdateRef.setValue(updatedItem).addOnSuccessListener {
                                Toast.makeText(
                                    this@AddMenuActivity,
                                    "Cập nhật món ăn thành công!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@AddMenuActivity,
                                    "Cập nhật món ăn thất bại!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@AddMenuActivity,
                                "Vui lòng điền vào ô trống! ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error: ${databaseError.message}")
                Toast.makeText(
                    this@AddMenuActivity,
                    "Có lỗi xảy ra khi truy vấn dữ liệu từ Firebase: ${databaseError.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
//Thêm dữ liệu lên firebase khi nhấn thêm
    private fun uploadData() {
        val menuRef = database.getReference("Menu")
        GlobalScope.launch(Dispatchers.Main) {
            val newIndexKey = "Menu_${getIndex()}"
            if (foodImageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("menu_images/$newIndexKey.jpg")
                val uploadTask = imageRef.putFile(foodImageUri!!)
                try {
                    uploadTask.await()
                    val downloadUrl = imageRef.downloadUrl.await()
                    val newItem = AllMenu(
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodImage = downloadUrl.toString(),
                        foodTopping = foodTopping
                    )
                    menuRef.child(newIndexKey).setValue(newItem).addOnSuccessListener {
                        Toast.makeText(
                            this@AddMenuActivity, "Dữ liệu đã được cập nhật thành công!", Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@AddMenuActivity,
                            "Dữ liệu cập nhật thất bại!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("AddMenuActivity", "Error uploading data: ${e.message}")
                    Toast.makeText(this@AddMenuActivity, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@AddMenuActivity, "Hãy chọn hình ảnh cho món ăn!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun createKeyFromName(name: String): String {
        return name.filter { it.isLetterOrDigit() }.toLowerCase()
    }

    private suspend fun getIndex(): Int {
        val query = database.getReference("Menu")
        return try {
            val snapshot = query.get().await()
            snapshot.childrenCount.toInt() + 1
        } catch (e: Exception) {
            Log.e("AddMenuActivity", "Error getting index: ${e.message}")
            1
        }
    }
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.selectedImage.setImageURI(uri)
                foodImageUri = uri
            }
        }
    override fun onIngredientsListChanged(selectedIngredients: List<String>) {
        foodIngredient = selectedIngredients.joinToString(", ")
        Log.d("AddMenuActivity", "Selected Ingredients: $foodIngredient")
    }

}
