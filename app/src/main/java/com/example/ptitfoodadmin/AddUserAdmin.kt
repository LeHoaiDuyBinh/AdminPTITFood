package com.example.ptitfoodadmin

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.AdminModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.security.MessageDigest

class AddUserAdmin : AppCompatActivity() {
    private lateinit var userNameAdmin: String
    private lateinit var emailaddAdmin: String
    private lateinit var passwordaddAdmin: String
    private lateinit var auth: FirebaseAuth
    private var count: Int = 0
    private lateinit var database: DatabaseReference
    private lateinit var databaseReference : DatabaseReference
    private lateinit var edtUserName : EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var phoneNumber: String
    private lateinit var address: String
    private lateinit var userCode: String
    private lateinit var roleAdmin: String



    val position = arrayListOf("Quản lí", "Nhân viên")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_admin)
        val btnBack = findViewById<ImageButton>(R.id.backButton1)
        btnBack.setOnClickListener {
            onBackPressed()
        }
        setUp()
        val spinner = findViewById<Spinner>(R.id.spin_position)
        val positions = arrayOf("Quản lí", "Nhân viên")

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            position
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                val text = "Chức vụ: ${positions[position]}"
                val spannableText = SpannableString(text)
                spannableText.setSpan(StyleSpan(Typeface.BOLD), 0, "Chức vụ: ".length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                v.text = spannableText
                v.textSize = 18f
                return v
            }

        }
        spinner.adapter = adapter



        // Thêm user vào management admin
        auth = Firebase.auth
        database = Firebase.database.reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Admin")
        val query: Query = databaseReference.orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Lấy số lượng phần tử trong dataSnapshot
                count = dataSnapshot.childrenCount.toInt()
                Log.d("YourActivity", "Size of Admin data: $count")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("YourActivity", "Error getting Admin data size: ${databaseError.message}")
            }
        })
        var s : String = "@ptitfoodadmin.edu.vn"

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: android.view.View?,
                position: Int,
                id: Long
            ) {
                roleAdmin = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val btnaddUser = findViewById<Button>(R.id.btn_add_user)
        btnaddUser.setOnClickListener {
            userNameAdmin = edtUserName.text.toString().trim()
            emailaddAdmin = edtEmail.text.toString().trim()
            passwordaddAdmin = edtPassword.text.toString().trim()

            if (emailaddAdmin.isBlank() || passwordaddAdmin.isBlank()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin !", Toast.LENGTH_SHORT).show()
            } else {
                if (emailaddAdmin.endsWith(s.toString())) {
                    registerUserAdmin(userNameAdmin, emailaddAdmin, passwordaddAdmin)
                }else{
                    Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun addAdmin(emailaddAdmin: String, passwordaddAdmin: String) {
        val lowerCaseEmail = emailaddAdmin.lowercase()
        auth.createUserWithEmailAndPassword(lowerCaseEmail, passwordaddAdmin).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val admin = auth.currentUser
                updateAdmin(admin)
            } else {
                Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUserAdmin(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Đã tạo thêm tài khoản thành công", Toast.LENGTH_SHORT).show()
                saveUser()
//                val intent = Intent(this, ManageAdmin ::class.java)
//                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Đã xảy ra lỗi khi đăng ký", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser() {
        var code : String = "AD"
        emailaddAdmin = edtEmail.text.toString().trim()
        userNameAdmin = edtUserName.text.toString().trim()
        passwordaddAdmin = edtPassword.text.toString().trim()
        phoneNumber = ""
        address = ""
        userCode = (code + count).toString()

        val user = AdminModel(userCode,userNameAdmin, emailaddAdmin, passwordaddAdmin, roleAdmin ,address, phoneNumber)
        // Lưu thông tin khách hàng vào database của Firebase realtime
//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val encodedEmail = sha1(emailaddAdmin)
        Toast.makeText(this, "Đăng nhập lại Admin để cấp lại quyền", Toast.LENGTH_SHORT).show()
        database.child("Admin").child(encodedEmail).setValue(user)
        val logout = FirebaseAuth.getInstance()
        logout.signOut()
        val intent = Intent(this, LoginAdmin::class.java)
        startActivity(intent)
    }

    private fun updateAdmin(admin: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun setUp() {
        edtUserName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
    }

    fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}