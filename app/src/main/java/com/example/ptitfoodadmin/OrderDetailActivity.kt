package com.example.ptitfoodadmin

import OrderDetailAdapter
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.model.FoodItem
import com.example.ptitfoodadmin.model.OrderItem
import com.example.ptitfoodadmin.model.ToppingItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        val btnBack = findViewById<ImageButton>(R.id.backButton1)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val orderId = intent.getStringExtra("orderId")
        if (orderId != null) {
            displayOrderDetails(orderId)
        } else {
            Toast.makeText(this, "Invalid orderId", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayOrderDetails(orderId: String) {
        val recyclerView: RecyclerView = findViewById(R.id.orderDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val orderRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderDetailList = mutableListOf<FoodItem>()
                outerLoop@ for (orderSnapshot in snapshot.children) {
                    for (orderDetailSnapshot in orderSnapshot.children) {
                        if(orderDetailSnapshot.key == orderId) {
                            val orderItem = orderDetailSnapshot.getValue(OrderItem::class.java)
                            if (orderItem != null) {
                                val textTotalPrice: TextView = findViewById(R.id.tv_total_price)
                                textTotalPrice.text = "Tổng tiền: ${orderItem.totalPrice}"
                                for (foodItemSnapshot in orderDetailSnapshot.child("orderDetail").children) {
                                    val foodItem = foodItemSnapshot.getValue(FoodItem::class.java)
                                    if (foodItem != null) {
                                        val toppingList = mutableListOf<ToppingItem>()
                                        for (toppingItemSnapshot in foodItemSnapshot.child("foodTopping").children) {
                                            val toppingItem = toppingItemSnapshot.getValue(ToppingItem::class.java)
                                            if (toppingItem != null) {
                                                toppingList.add(toppingItem)
                                            }
                                        }
                                        foodItem.foodTopping = toppingList
                                        orderDetailList.add(foodItem)
                                    }
                                }
                                orderItem.orderDetail = orderDetailList
                            }
                            break@outerLoop
                        }
                    }
                }
                val adapter = OrderDetailAdapter(orderDetailList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(this@OrderDetailActivity, "Error fetching data from Firebase", Toast.LENGTH_SHORT).show()
            }
        })
    }
}