package com.example.ptitfoodadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.RevenueItem
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.OrderItem
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class RevenueAdapter(private val context: Context, private val dataList: ArrayList<OrderItem>) : RecyclerView.Adapter<RevenueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_revenue, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = dataList[position]
        holder.textViewIndex.text = (position + 1).toString()
        holder.textViewOrderCode.text = orderItem.orderCode
        holder.textViewUserName.text = orderItem.userName
        holder.textViewTotal.text = formatCurrency(orderItem.totalPrice).toString() + "đ"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun formatCurrency(currency: String): Int {
        val cleanString = currency.replace("[^\\d]".toRegex(), "")
        return try {
            cleanString.toInt()
        } catch (e: NumberFormatException) {
            0 // Trả về 0 nếu không thể chuyển đổi thành số nguyên
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewIndex: TextView = itemView.findViewById(R.id.textViewIndex)
        val textViewOrderCode: TextView = itemView.findViewById(R.id.textViewOrderCode)
        val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        val textViewTotal: TextView = itemView.findViewById(R.id.textViewTotalPrices)
    }

    fun updateData(newDataList: List<OrderItem>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }

}
