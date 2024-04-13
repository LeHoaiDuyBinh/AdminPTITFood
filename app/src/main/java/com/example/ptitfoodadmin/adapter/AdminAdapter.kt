package com.example.ptitfoodadmin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.DataUpdateListener
import com.example.ptitfoodadmin.ManageAdmin
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.AdminModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class AdminAdapter(private val admins: MutableList<AdminModel>, private val context: Context) :
    RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Admin")
    private val s: String = "adminptitfood@ptitfoodadmin.edu.vn"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_manage_user, parent, false)
        return AdminViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val currentAdmin = admins[position]

        holder.textNameUserAD.text = currentAdmin.name
        holder.textEmailAD.text = currentAdmin.email
        holder.textRoleUserAD.text = currentAdmin.role

//        if (currentUser.r != "Nhân viên" || currentUser.role != "Quản lí") {
//            holder.deleteButton.setOnClickListener {
//                // Xử lý sự kiện khi người dùng nhấn vào nút delete
//                val position = holder.adapterPosition // Lấy vị trí của phần tử hiện tại
//                if (position != RecyclerView.NO_POSITION) {
//                    val adminToDelete = admins[position]
//                    deleteAdminFromFirebase(adminToDelete)
//                    removeItem(position)
//                }
//            }
//        }else{
//            Toast.makeText(context, "Don't permisson", Toast.LENGTH_SHORT).show()
//        }
        currentUser?.let { user ->
            // Kiểm tra quyền truy cập của tài khoản đang đăng nhập
            if (user.email == s.toString()) {
                // Hiển thị nút delete và cho phép xóa tài khoản khác
                holder.deleteButton.visibility = View.VISIBLE
                holder.deleteButton.setOnClickListener {
                    // Xử lý sự kiện khi người dùng nhấn vào nút delete
                    val position = holder.adapterPosition // Lấy vị trí của phần tử hiện tại
                    if (position != RecyclerView.NO_POSITION) {
                        val adminToDelete = admins[position]
                        deleteAdminFromFirebase(adminToDelete)
                        removeItem(position)
                    }
                }
            } else {
                // Ẩn nút delete với các tài khoản không phải là quản trị viên
                holder.deleteButton.visibility = View.GONE
            }
        }


    }

    override fun getItemCount() = admins.size

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNameUserAD: TextView = itemView.findViewById(R.id.tvNameUserAD)
        val textEmailAD: TextView = itemView.findViewById(R.id.tvEmailUserAD)
        val textRoleUserAD: TextView = itemView.findViewById(R.id.tvRoleUserAD)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    fun removeItem(position: Int) {
        admins.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun deleteAdminFromFirebase(admin: AdminModel) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Admin")
        val query: Query = databaseReference.orderByChild("email").equalTo(admin.email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình xóa dữ liệu từ Firebase
                Log.e("ManageAdmin", "Đã xảy ra lỗi: ${databaseError.message}")
            }
        })
    }

    fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

}