import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.OrderDetailItem
import com.squareup.picasso.Picasso


class OrderDetailAdapter(private val orderDetailList: List<OrderDetailItem>) :
    RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_order, parent, false)
        return OrderDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val currentItem = orderDetailList[position]
        Picasso.get().load(currentItem.imageUrl).into(holder.iamgeOrderItem)
        holder.textFoodName.text = currentItem.textNameOrderDetail
        holder.textExtra.text = currentItem.textExtraOrderDetail
        holder.textPrice.text = "Giá : "+currentItem.textPriceOrderDetail.toString()
        holder.textNote.text = currentItem.textNoteOrderDetail
        holder.textQuantityOrderDetail.text="Số lượng : "+ currentItem.textQuantityOrderDetail.toString()
    }

    override fun getItemCount(): Int {
        return orderDetailList.size
    }

    inner class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textFoodName: TextView = itemView.findViewById(R.id.textNameOrderDetail)
        val textExtra: TextView = itemView.findViewById(R.id.textExtraOrderDetail)
        val textPrice: TextView = itemView.findViewById(R.id.textPriceOrderDetail)
        val textNote: TextView = itemView.findViewById(R.id.textNoteOrderDetail)
        val textQuantityOrderDetail: TextView = itemView.findViewById(R.id.textQuantityOrderDetail)
        val iamgeOrderItem:ImageView = itemView.findViewById(R.id.imageOrderDetail)
    }
}
