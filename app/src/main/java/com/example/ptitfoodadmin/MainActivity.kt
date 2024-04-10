package com.example.ptitfoodadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ptitfoodadmin.database.DataHandler
import com.example.ptitfoodadmin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var user :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()
        val auth = FirebaseAuth.getInstance()

// Lấy thông tin người dùng hiện tại
        val userNow = auth.currentUser
        if (userNow != null) {
            val userEmail = userNow.email
            if (userEmail == "adminptitfood@ptitfoodadmin.edu.vn") {
                // Không xóa tài khoản admin
                Toast.makeText(this, "Không thể xóa tài khoản admin", Toast.LENGTH_SHORT).show()
            } else {
                // Khởi tạo tham chiếu đến Realtime Database
                val databaseReference = FirebaseDatabase.getInstance().reference.child("Admin").child(sha1(userEmail.toString()))

                // Lắng nghe sự thay đổi trong dữ liệu
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Kiểm tra xem dữ liệu có tồn tại không
                        if (dataSnapshot.exists()) {
                            // Kiểm tra xem email, username, usercode có bị null không
                            val email = dataSnapshot.child("email").getValue(String::class.java)
                            val adminCode = dataSnapshot.child("adminCode").getValue(String::class.java)
                            val name = dataSnapshot.child("name").getValue(String::class.java)
                            Toast.makeText(this@MainActivity, email.toString(), Toast.LENGTH_LONG).show()

                            if (email != null && adminCode != null && name != null) {
                                // Không xóa tài khoản vì thông tin không null
                                Toast.makeText(this@MainActivity, "Không xóa vì thông tin không null", Toast.LENGTH_LONG).show()

                            }
                        } else {
                            // Dữ liệu không tồn tại
                            userNow.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Xóa thành công
                                        val successMessage = "Tài khoản đã bị Admin xóa trước đó"
                                        Toast.makeText(this@MainActivity, successMessage.toString(), Toast.LENGTH_SHORT).show()
                                        // Log out ngay sau khi xóa
                                        FirebaseAuth.getInstance().signOut()
                                        startActivity(Intent(this@MainActivity, LoginAdmin::class.java))
                                    } else {
                                        // Xóa thất bại
                                        Toast.makeText(this@MainActivity, "Xóa tài khoản thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            Toast.makeText(this@MainActivity, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Xử lý khi có lỗi xảy ra
                        Toast.makeText(this@MainActivity, "Đã xảy ra lỗi: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        setUp()

    }

    private fun setUp(){
        val soLuongDonhang = findViewById<TextView>(R.id.soLuongDonHang)
        val soLuongDonHangHoanTat = findViewById<TextView>(R.id.soLuongDonHangHoanTat)
        val tongTien = findViewById<TextView>(R.id.tongTien)
        val viewAddMenu = findViewById<ConstraintLayout>(R.id.btn_add_menu)
        val authAdmin = FirebaseAuth.getInstance()
        val admin = authAdmin.currentUser

        viewAddMenu.setOnClickListener{
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }
        val viewAllMenuItem = findViewById<ConstraintLayout>(R.id.bnt_show)
        viewAllMenuItem.setOnClickListener{
            val intent = Intent(this, AllMenuItemActivity::class.java)
            startActivity(intent)
        }
//
//        val viewChartActivity = findViewById<ConstraintLayout>(R.id.btn_view_chart)
//        viewChartActivity.setOnClickListener{
//            val intent = Intent(this, ChartActivity::class.java)
//            startActivity(intent)
//        }

        val viewRevenueActivity = findViewById<ConstraintLayout>(R.id.btn_view_revenue)
        viewRevenueActivity.setOnClickListener{
            val intent = Intent(this, RevenueActivity::class.java)
            startActivity(intent)
        }
        val viewOrder = findViewById<ConstraintLayout>(R.id.btn_order)
        viewOrder.setOnClickListener{
            /* val dataHandler = DataHandler()
             dataHandler.addFirebaseData()*/
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
        val managementOrder = findViewById<ConstraintLayout>(R.id.managementOrder)
        managementOrder.setOnClickListener{
            val intent = Intent(this, ManagementOrderActivity::class.java)
            startActivity(intent)
        }

        val adminProfile = findViewById<ConstraintLayout>(R.id.btn_profile)
        adminProfile.setOnClickListener{
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }


        val addUserAdmin = findViewById<ConstraintLayout>(R.id.addUser)
            addUserAdmin.setOnClickListener{
                if (admin != null) {
                    val userEmail = admin.email
                    if (userEmail == "adminptitfood@ptitfoodadmin.edu.vn"){
                        val intent = Intent(this, AddUserAdmin::class.java)
                        startActivity(intent)
                    }else {
                        Toast.makeText(this, "Bạn không có quyền thêm User", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        val manageAdmin = findViewById<ConstraintLayout>(R.id.btn_user_management)
        manageAdmin.setOnClickListener{
            val intent = Intent(this, ManageAdmin::class.java)
            startActivity(intent)
        }

        val btnLogOut = findViewById<ConstraintLayout>(R.id.btn_log_out)
        btnLogOut.setOnClickListener{
            user.signOut()
            val intent = Intent(this, LoginAdmin::class.java)
            startActivity(intent)
        }
    }

    fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}