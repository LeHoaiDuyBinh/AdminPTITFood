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
import com.example.ptitfoodadmin.model.FoodItem
import com.example.ptitfoodadmin.model.ManagementOrderItem
import com.example.ptitfoodadmin.model.OrderItem
import com.example.ptitfoodadmin.model.ToppingItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

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
        if(currentOrder.status==0){
            holder.btnSuccess.visibility=View.GONE;

        }

        if (currentOrder.status == 3) {

            holder.btnSuccess.visibility=View.VISIBLE;
            holder.btnCancel.visibility=View.VISIBLE;
        }
        holder.btnSuccess.setOnClickListener {
            moveOrderToHistory(holder)
            updateStatusOrder(currentOrder.id, 1, holder) // Đã nhận


        }
        holder.btnCancel.setOnClickListener {
            updateStatusOrder(currentOrder.id, 2, holder) // Đã hủy
        }

        holder.btnTransport.setOnClickListener {
//            holder.btnTransport.visibility=View.GONE
            updateStatusOrder(currentOrder.id, 3, holder) // Đang giao
        }
        holder.detail.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("orderId",currentOrder.id)
            context.startActivity(intent)
        }
    }

    private fun moveOrderToHistory(holder: OrderViewHolder) {
        val orderRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        if (orderSnapshot.key == holder.id.text.toString()) {
                            val order = orderSnapshot.getValue(OrderItem::class.java)
                            val historyRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("OrderHistory")
                            historyRef.child(userSnapshot.key!!).child(holder.id.text.toString()).setValue(order)
                                .addOnSuccessListener {
                                    dataUpdateListener.onDataUpdated()
                                }
                                .addOnFailureListener {
                                    Log.e("MoveToHistory", "Move to history failed", it)
                                }
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MoveToHistory", "Move to history failed", error.toException())
            }
        })
    }
    private fun updateStatusOrder(id: String, newStatus: Int, holder: OrderViewHolder) {
        val orderRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        if (orderSnapshot.key == id) {
                            val updates = HashMap<String, Any>()
                            updates["orderStatus"] = newStatus
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                            val formattedDate = sdf.format(System.currentTimeMillis())
                            updates["updatedTime"] = formattedDate
                            orderSnapshot.ref.updateChildren(updates)
                                .addOnSuccessListener {
                                    // If newStatus is 1, move the order to OrderHistory
                                    if (newStatus == 1) {
                                        moveOrderToHistory(holder)
                                    }
                                    dataUpdateListener.onDataUpdated()
                                }
                                .addOnFailureListener {
                                    Log.e("UpdateStatus", "Update status failed", it)
                                }
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpdateStatus", "Update status failed", error.toException())
            }
        })
    }


    override fun getItemCount(): Int {
        return orderList.size
    }
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.orderName)
        val id: TextView = itemView.findViewById(R.id.orderId)
        val btnTransport: Button = itemView.findViewById(R.id.btn_transport)
        val btnSuccess: Button = itemView.findViewById(R.id.btn_accpept)
        val btnCancel: Button = itemView.findViewById(R.id.btn_cancel)
        val detail: TextView =itemView.findViewById(R.id.viewDetail)
    }
}