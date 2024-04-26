package com.example.ptitfoodadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.adapter.OrderAdapter
import com.example.ptitfoodadmin.model.FoodItem
import com.example.ptitfoodadmin.model.ManagementOrderItem
import com.example.ptitfoodadmin.model.OrderItem
import com.example.ptitfoodadmin.model.ToppingItem
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

        val orderRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (orderSnapshot in snapshot.children) {
                    for (orderDetailSnapshot in orderSnapshot.children) {
                        val orderItem = orderDetailSnapshot.getValue(OrderItem::class.java)
                        if (orderItem != null) {
                            val orderDetailList = mutableListOf<FoodItem>()
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
                            // Hien thi order theo trang thai orderStatus = 0 (Dang xu ly)
//                            if (orderItem.orderStatus == 0) {
//                                orderList.add(orderItem)
//                            }
                            orderList.add(orderItem)
                        }
                        // Hien thi don moi nhat len dau
                        orderList.sortByDescending { it.orderStatus == 0 }
                    }
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