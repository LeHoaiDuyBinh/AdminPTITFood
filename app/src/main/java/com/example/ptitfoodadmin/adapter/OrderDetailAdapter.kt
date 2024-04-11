import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.FoodItem
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class OrderDetailAdapter(private val orderDetailList: MutableList<FoodItem>) :
    RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_order, parent, false)
        return OrderDetailViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val currentItem = orderDetailList[position]
        FirebaseStorage.getInstance().getReferenceFromUrl(currentItem.foodImage).downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(holder.iamgeOrderItem)
        }
        holder.textFoodName.text = currentItem.foodName
        val foodToppingString = currentItem.foodTopping.joinToString(separator = ", ") { it.toString() }
        if (foodToppingString.isNotEmpty()) {
            holder.textExtra.text = "Topping : $foodToppingString"
        } else {
            holder.textExtra.text = ""
        }
        holder.textPrice.text = "Giá : "+currentItem.foodPrice
        holder.textNote.text = "Ghi chú : "+currentItem.foodNote
        holder.textQuantityOrderDetail.text="Số lượng : "+ currentItem.foodQuantity.toString()
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
