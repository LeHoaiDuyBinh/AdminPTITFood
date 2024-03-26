package com.example.ptitfoodadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.RevenueItem
import com.example.ptitfoodadmin.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class RevenueAdapter(private val context: Context, private val dataList: ArrayList<RevenueItem>) : RecyclerView.Adapter<RevenueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_revenue, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val revenueItem = dataList[position]
        holder.textViewIndex.text = (position + 1).toString()
        holder.textViewProductName.text = revenueItem.productName
        holder.textViewQuantity.text = revenueItem.quantity.toString()
        holder.textViewTotal.text = formatCurrency(revenueItem.total) + "đ"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun formatCurrency(number: Int): String {
        val formatter = DecimalFormat("###,###,###", DecimalFormatSymbols.getInstance(Locale("vi", "VN")))
        return formatter.format(number)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewIndex: TextView = itemView.findViewById(R.id.textViewIndex)
        val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)
        val textViewTotal: TextView = itemView.findViewById(R.id.textViewTotal)
    }
}
