package com.example.ptitfoodadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ptitfoodadmin.adapter.MenuItemAdapter
import com.example.ptitfoodadmin.databinding.ActivityAllMenuItemBinding
import com.example.ptitfoodadmin.model.AllMenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllMenuItemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenu> = ArrayList()
    private val binding : ActivityAllMenuItemBinding by lazy {
        ActivityAllMenuItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        retrieveMenuItem()


        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("Menu")

        //Fecth data form data base
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear exiting data before populating
                menuItems.clear()

                // loop for through each food item
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }
    private fun setAdapter() {
        val adapter = MenuItemAdapter(this@AllMenuItemActivity, menuItems,databaseReference)
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter = adapter
    }
}