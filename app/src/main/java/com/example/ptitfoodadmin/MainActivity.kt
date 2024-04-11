package com.example.ptitfoodadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ptitfoodadmin.database.DataHandler
import com.example.ptitfoodadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val soLuongDonhang = findViewById<TextView>(R.id.soLuongDonHang)
        val soLuongDonHangHoanTat = findViewById<TextView>(R.id.soLuongDonHangHoanTat)
        val tongTien = findViewById<TextView>(R.id.tongTien)
        val viewAddMenu = findViewById<ConstraintLayout>(R.id.btn_add_menu)
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
            val intent = Intent(this, AddUserAdmin::class.java)
            startActivity(intent)
        }
        val manageAdmin = findViewById<ConstraintLayout>(R.id.btn_user_management)
        manageAdmin.setOnClickListener{
            val intent = Intent(this, ManageAdmin::class.java)
            startActivity(intent)
        }
    }
}