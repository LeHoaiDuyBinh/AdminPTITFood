package com.example.ptitfoodadmin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import com.example.ptitfoodadmin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

class AddUserAdmin : AppCompatActivity() {
    private lateinit var emailaddAdmin: String
    private lateinit var passwordaddAdmin: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText




    val position = arrayListOf("Quản lí", "Nhân viên")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_admin)
        val btnBack = findViewById<ImageButton>(R.id.backButton1)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val spinner = findViewById<Spinner>(R.id.spin_position)
        val position = arrayOf("Quản lí", "Nhân viên")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, position)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: android.view.View?,
                position: Int,
                id: Long
            ) {
                val item = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val btnaddUser = findViewById<Button>(R.id.btn_add_user)
        btnaddUser.setOnClickListener {
            emailaddAdmin = edtEmail.text.toString().trim()
            passwordaddAdmin = edtPassword.text.toString().trim()

            if (emailaddAdmin.isBlank() || passwordaddAdmin.isBlank()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin !", Toast.LENGTH_SHORT).show()
            } else {

            }
        }

    }
    private fun addAdmin(emailaddAdmin: String, passwordaddAdmin: String) {
        auth.createUserWithEmailAndPassword(emailaddAdmin, passwordaddAdmin).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val admin = auth.currentUser
                updateAdmin(admin)
            } else {
                Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAdmin(admin: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun setUp() {
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
    }
}