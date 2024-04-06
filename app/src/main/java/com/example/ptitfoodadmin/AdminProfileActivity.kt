package com.example.ptitfoodadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.example.ptitfoodadmin.databinding.ActivityAdminProfileBinding
import com.example.ptitfoodadmin.model.AdminModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // Khởi tạo auth
        database = FirebaseDatabase.getInstance() // Khởi tạo database

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val adminID = sha1(auth.currentUser?.email ?: "")
        val email =  auth.currentUser?.email
        val test = sha1("adminstaff@ptitfoodadmin.edu.vn")
        Log.d("AdminProfileActivity", "Admin ID: $adminID")
        Log.d("AdminProfileActivity", "emai: $email")
        Log.d("AdminProfileActivity", "test: $test")

        setAdminData()

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etName.text.toString()
            val address = binding.etAddress.text.toString()
            val phone = binding.etSdt.text.toString()
            updateAdminData(name, address, phone)
        }
    }

    private fun updateAdminData(
        name: String,
        address: String,
        phone: String,
    ) {
        val adminID = sha1(auth.currentUser?.email ?: "")
        if (adminID != null) {
            val adminRef: DatabaseReference = database.getReference().child("Admin").child(adminID)

            val updateMap = HashMap<String, Any>()
            updateMap["name"] = name
            updateMap["address"] = address
            updateMap["phone"] = phone

            adminRef.updateChildren(updateMap).addOnSuccessListener {
                Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Profile Updated Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAdminData() {
        val adminID = sha1(auth.currentUser?.email ?: "")
        if (adminID != null) {
            val adminRef: DatabaseReference = database.getReference().child("Admin").child(adminID)
            adminRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val adminProfile = snapshot.getValue(AdminModel::class.java)
                    if(adminProfile != null)
                    {
                        binding.etAdminCode.setText(adminProfile.adminCode)
                        binding.etName.setText(adminProfile.name)
                        binding.etEmail.setText(adminProfile.email)
                        binding.etAddress.setText(adminProfile.address)
                        binding.etSdt.setText(adminProfile.phone)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}