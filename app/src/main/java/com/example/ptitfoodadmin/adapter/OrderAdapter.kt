package com.example.ptitfoodadmin.adapter

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.OrderDetailActivity
import com.example.ptitfoodadmin.model.OrderItem
import com.example.ptitfoodadmin.R

class OrderAdapter(private val orderList: List<OrderItem>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentItem = orderList[position]
        holder.textNameOrder.text = currentItem.userName
        holder.createdTime.text = "Thời gian tạo: "+currentItem.createdTime
        holder.updatedTime.text = "Thời gian cập nhật: "+currentItem.updatedTime
        holder.textStatusOrder.text = currentItem.orderStatus.toString()
        if (currentItem.orderStatus==0){
            holder.textStatusOrder.text = "Đang xử lý"
            holder.imageOrder.setImageResource(R.drawable.status_pending)
            holder. textStatusOrder.setTextColor(Color.parseColor("#FABE05"))
        }
        if (currentItem.orderStatus==3){
            holder.textStatusOrder.text = "Đang giao hàng"
            holder.imageOrder.setImageResource(R.drawable.status_transport)
            holder. textStatusOrder.setTextColor(Color.parseColor("#15CD1C"))
        }
        if (currentItem.orderStatus==1){
            holder.textStatusOrder.text = "Đã xác nhận"
            holder.imageOrder.setImageResource(R.drawable.status_recevie)
            holder. textStatusOrder.setTextColor(Color.parseColor("#15CD1C"))
        }
        if (currentItem.orderStatus==2){
            holder.textStatusOrder.text = "Đã hủy"
            holder.imageOrder.setImageResource(R.drawable.status_cancel)
            holder. textStatusOrder.setTextColor(Color.parseColor("#E63131"))
        }
        if (currentItem.orderStatus==4){
            holder.textStatusOrder.text = "Đã hoàn thành"
            holder.imageOrder.setImageResource(R.drawable.status_recevie)
            holder. textStatusOrder.setTextColor(Color.parseColor("#15CD1C"))
        }
        holder.textIdOrder.text = "ID : "+ currentItem.orderCode
        holder.buttonDetail.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("orderId", currentItem.orderCode)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNameOrder: TextView = itemView.findViewById(R.id.textNameOrder)
        val textStatusOrder: TextView = itemView.findViewById(R.id.textStatusOrder)
        val textIdOrder: TextView = itemView.findViewById(R.id.textIdOrder)
        val buttonDetail:Button=itemView.findViewById(R.id.btn_detail)
        val imageOrder:ImageView=itemView.findViewById(R.id.imageOrder)
        val createdTime: TextView = itemView.findViewById(R.id.createdTime)
        val updatedTime: TextView = itemView.findViewById(R.id.updatedTime)
    }
}

private fun Any.putExtra(s: String, orderCode: Any?) {

}
