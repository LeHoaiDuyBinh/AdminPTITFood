package com.example.ptitfoodadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.adapter.OrderAdapter
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

        val giuakiLocRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("giuaki-loc")

        giuakiLocRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (orderSnapshot in snapshot.child("order").children) {
                    val orderId = orderSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val orderStatus = orderSnapshot.child("status").getValue(String::class.java) ?: ""
                    val orderName = orderSnapshot.child("name").getValue(String::class.java) ?: ""
                    val orderItem = OrderItem(orderName, orderStatus, orderId)
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