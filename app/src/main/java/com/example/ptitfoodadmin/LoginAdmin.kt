package com.example.ptitfoodadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginAdmin : AppCompatActivity() {

    private lateinit var emailAdmin: String
    private lateinit var passwordAdmin: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)
        setUp()

        auth = Firebase.auth
        database = Firebase.database.reference

        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener{
            emailAdmin = edtEmail.text.toString().trim()
            passwordAdmin = edtPassword.text.toString().trim()

            if(emailAdmin.isBlank() || passwordAdmin.isBlank()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin !", Toast.LENGTH_SHORT).show()
            } else {
                signInAdmin(emailAdmin, passwordAdmin)
            }
        }
    }

    private fun signInAdmin(emailAdmin: String, passwordAdmin: String) {
        auth.signInWithEmailAndPassword(emailAdmin, passwordAdmin).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val admin = auth.currentUser
                updateAdmin(admin)
            }else {
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