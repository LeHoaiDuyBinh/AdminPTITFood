package com.example.ptitfoodadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.adapter.AdminAdapter
import com.example.ptitfoodadmin.databinding.ActivityAdminProfileBinding
import com.example.ptitfoodadmin.model.AdminModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageAdmin : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : AdminAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private val adminManageList = mutableListOf<AdminModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_admin)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Admin")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adminManageList.clear()
                for (snapshot in dataSnapshot.children) {
                    val admin = snapshot.getValue(AdminModel::class.java)
                    admin?.let {
                        adminManageList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                Log.e("ManageAdmin", "Đã xảy ra lỗi: ${databaseError.message}")
            }
        }
        databaseReference.addValueEventListener(valueEventListener)
        // Gán ValueEventListener cho DatabaseReference để lắng nghe sự thay đổi dữ liệu
        databaseReference.addValueEventListener(valueEventListener)

        recyclerView = findViewById(R.id.rcv_manager)
        adapter = AdminAdapter(adminManageList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            onBackPressed()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        // Đảm bảo bạn gỡ bỏ ValueEventListener khi không cần thiết để tránh rò rỉ bộ nhớ
        databaseReference.removeEventListener(valueEventListener)
    }
}