package com.example.ptitfoodadmin

import ManagementOrderAdapter
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.model.ManagementOrderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManagementOrderActivity : AppCompatActivity(),DataUpdateListener  {
    private lateinit var recyclerView: RecyclerView
    private val managementOrderList = mutableListOf<ManagementOrderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management_order)
        recyclerView = findViewById(R.id.managementOrderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            onBackPressed()
        }
        fetchAndUpdateData()
        val adapter = ManagementOrderAdapter(managementOrderList, this)
        recyclerView.adapter = adapter
    }
    override fun onDataUpdated() {
        managementOrderList.clear()
        fetchAndUpdateData()
    }

    private fun fetchAndUpdateData() {
        val orderRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val orderId = orderSnapshot.key ?: ""
                        val orderStatus = orderSnapshot.child("orderStatus").getValue(Int::class.java) ?: 0
                        val userName = orderSnapshot.child("userName").getValue(String::class.java) ?: ""
                        if (orderStatus == 0 || orderStatus==3 || orderStatus==1) {
                            val managementOrderItem = ManagementOrderItem(userName, orderId, orderStatus)
                            managementOrderList.add(managementOrderItem)
                        }
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManagementOrderActivity, "Error fetching data from Firebase", Toast.LENGTH_SHORT).show()
            }
        })
    }
}