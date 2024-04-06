import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.OrderDetailItem
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class OrderDetailAdapter(private val orderDetailList: List<OrderDetailItem>) :
    RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_order, parent, false)
        return OrderDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val currentItem = orderDetailList[position]
        FirebaseStorage.getInstance().getReferenceFromUrl(currentItem.imageUrl).downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(holder.iamgeOrderItem)
        }
        holder.textFoodName.text = currentItem.textNameOrderDetail
        holder.textExtra.text = "Đồ ăn thêm"
        holder.textPrice.text = "Giá : "+currentItem.textPriceOrderDetail.toString()
        holder.textNote.text = "Ghi chú"
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
