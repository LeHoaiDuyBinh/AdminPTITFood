package com.example.ptitfoodadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.adapter.OrderAdapter
import com.example.ptitfoodadmin.model.FoodItem
import com.example.ptitfoodadmin.model.OrderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {

    private val orderList = mutableListOf<OrderItem>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        recyclerView = findViewById(R.id.orderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val orderRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Order")

        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (orderSnapshot in snapshot.children) {
                    val orderId = orderSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val orderStatus = orderSnapshot.child("Status").getValue(Int::class.java) ?: 0
                    val orderName = orderSnapshot.child("Name").getValue(String::class.java) ?: ""
                    val totalPrice = orderSnapshot.child("TotalPrice").getValue(Int::class.java) ?: 0

                    val orderListSnapshot = orderSnapshot.child("OrderList")
                    val foodList = mutableMapOf<String, FoodItem>()
                    for (itemSnapshot in orderListSnapshot.children) {
                        val itemName = itemSnapshot.child("Name").getValue(String::class.java) ?: ""
                        val itemPrice = itemSnapshot.child("Price").getValue(Int::class.java) ?: 0
                        val itemQuantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                        val itemUrl = itemSnapshot.child("url").getValue(String::class.java) ?: ""
                        val foodItem = FoodItem(itemName, itemPrice, itemQuantity, itemUrl)
                        foodList[itemName] = foodItem
                    }

                    val orderItem = OrderItem(orderName, orderStatus, orderId, totalPrice, foodList)
                    orderList.add(orderItem)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi
            }
        })
        adapter = OrderAdapter(orderList)
        recyclerView.adapter = adapter
    }
}