package com.example.ptitfoodadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class ManageAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_admin)
        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}