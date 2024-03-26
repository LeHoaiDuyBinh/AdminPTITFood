package com.example.ptitfoodadmin

import OrderDetailAdapter
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.model.OrderDetailItem
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

        val orderId = intent.getIntExtra("orderId", -1)
        if (orderId != -1) {
            displayOrderDetails(orderId)
        } else {
            Toast.makeText(this, "Invalid orderId", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayOrderDetails(orderId: Int) {
        val recyclerView: RecyclerView = findViewById(R.id.orderDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val giuakiLocRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("giuaki-loc")
        giuakiLocRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderDetailList = mutableListOf<OrderDetailItem>()
                for (orderSnapshot in snapshot.child("order").children) {
                    val currentOrderId = orderSnapshot.child("id").getValue(Long::class.java)
                    if (currentOrderId?.toInt() == orderId) {
                        val orderListSnapshot = orderSnapshot.child("orderList")
                        for (productSnapshot in orderListSnapshot.children) {
                            val productId = productSnapshot.key?.toInt() ?: 0
                            val productName = productSnapshot.child("name").getValue(String::class.java) ?: ""
                            val productDescription = productSnapshot.child("description").getValue(String::class.java) ?: ""
                            val productPrice = productSnapshot.child("price").getValue(Int::class.java) ?: 0
                            val productSpecialNote = productSnapshot.child("specialNote").getValue(String::class.java) ?: ""
                            val productQuantity = productSnapshot.child("soluong").getValue(Int::class.java) ?: 0
                            val productImageUrl = productSnapshot.child("url").getValue(String::class.java) ?: ""
                            val orderDetailItem = OrderDetailItem(productId, productName, productDescription, productPrice, productSpecialNote, productQuantity, productImageUrl)
                            orderDetailList.add(orderDetailItem)
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