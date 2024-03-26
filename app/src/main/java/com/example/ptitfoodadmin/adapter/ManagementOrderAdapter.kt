import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.DataUpdateListener
import com.example.ptitfoodadmin.OrderDetailActivity
import com.example.ptitfoodadmin.R
import com.example.ptitfoodadmin.model.ManagementOrderItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ManagementOrderAdapter( private val orderList: List<ManagementOrderItem>,private val dataUpdateListener: DataUpdateListener) :
    RecyclerView.Adapter<ManagementOrderAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_management_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orderList[position]
        holder.name.text = currentOrder.name
        holder.id.text = currentOrder.id.toString()
        holder.btnSuccess.setOnClickListener {
            updateStatusOrder(currentOrder.id,"Đã nhận",holder)
        }
        holder.btnCancel.setOnClickListener { updateStatusOrder(currentOrder.id,"Đã hủy",holder) }
        holder.detail.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("orderId",currentOrder.id)
            context.startActivity(intent)
        }
    }

    private fun updateStatusOrder(id: Int, newStatus: String,holder: OrderViewHolder) {
        val giuakiLocRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("giuaki-loc")
        val updates = HashMap<String, Any>()
        updates["status"] = newStatus
        val orderPath = "order/$id"

        giuakiLocRef.child(orderPath).updateChildren(updates)
            .addOnSuccessListener {
                dataUpdateListener.onDataUpdated()
            }
            .addOnFailureListener {
                Log.e("UpdateStatus", "Update status failed", it)
            }
    }


    override fun getItemCount(): Int {
        return orderList.size
    }
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.orderName)
        val id: TextView = itemView.findViewById(R.id.orderId)
        val btnSuccess: Button = itemView.findViewById(R.id.btn_accpept)
        val btnCancel: Button = itemView.findViewById(R.id.btn_cancel)
        val detail: TextView =itemView.findViewById(R.id.viewDetail)
    }
}
