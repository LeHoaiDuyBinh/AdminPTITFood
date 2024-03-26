package com.example.ptitfoodadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.RevenueItem
import com.example.ptitfoodadmin.adapter.RevenueAdapter

class RevenueActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revenue)

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRevenue)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val dataList = generateData()
        val adapter = RevenueAdapter(this, dataList)
        recyclerView.adapter = adapter

    }

    private fun generateData(): ArrayList<RevenueItem> {
        val data = ArrayList<RevenueItem>()
        data.add(RevenueItem("Burger", 50, 1000000))
        data.add(RevenueItem("Salab", 30, 3000000))
        data.add(RevenueItem("Pasta", 20, 2000000))
        data.add(RevenueItem("Pizza", 40, 4000000))
        return data
    }


}