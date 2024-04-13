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
        if (currentOrder.status == 3) {
            holder.btnTransport.isEnabled = false
            holder.btnTransport.text = "Đang giao"
        }
        if (currentOrder.status == 1) {
            holder.btnTransport.visibility=View.GONE;
            holder.btnSuccess.visibility=View.GONE;
            holder.btnCancel.visibility=View.GONE;
            holder.btnEnd.visibility=View.VISIBLE;
        }
        holder.btnTransport.setOnClickListener {
            it.isEnabled = false // Disable the button
            holder.btnTransport.text = "Đang giao"
            updateStatusOrder(currentOrder.id, 3, holder) // Đang giao
        }
        holder.btnSuccess.setOnClickListener {
            updateStatusOrder(currentOrder.id, 1, holder) // Đã nhận

        }
        holder.btnCancel.setOnClickListener {
            updateStatusOrder(currentOrder.id, 2, holder) // Đã hủy
        }
        holder.btnEnd.setOnClickListener {
            deleteOrder(holder)


        }
        holder.detail.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("orderId",currentOrder.id)
            context.startActivity(intent)
        }
    }

    private fun deleteOrder(holder: OrderViewHolder) {
        val orderRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        if(orderSnapshot.key==holder.id.text.toString()){
                            val orderItem = orderSnapshot.getValue(OrderItem::class.java)
                            if (orderItem != null) {
                                val orderDetailList = mutableListOf<FoodItem>()
                                for (foodItemSnapshot in orderSnapshot.child("orderDetail").children) {
                                    val foodItem = foodItemSnapshot.getValue(FoodItem::class.java)
                                    if (foodItem != null) {
                                        val toppingList = mutableListOf<ToppingItem>()
                                        for (toppingItemSnapshot in foodItemSnapshot.child("foodTopping").children) {
                                            val toppingItem = toppingItemSnapshot.getValue(ToppingItem::class.java)
                                            if (toppingItem != null) {
                                                toppingList.add(toppingItem)
                                            }
                                        }
                                        foodItem.foodTopping = toppingList
                                        orderDetailList.add(foodItem)
                                    }
                                }
                                orderItem.orderDetail = orderDetailList
                            }

                            val userId= orderItem?.userId
                            val userCode = orderItem?.orderCode
                            val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("OrderHistory").child(userId.toString())
                            if (userCode != null) {
                                userRef.child(userCode).setValue(orderItem)
                                    .addOnSuccessListener {
                                        Log.d("AddOrder", "Order added successfully to userRef")
                                    }
                                    .addOnFailureListener {
                                        Log.e("AddOrder", "Failed to add order to userRef", it)
                                    }
                            }
                            orderSnapshot.ref.removeValue()
                            dataUpdateListener.onDataUpdated()
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
        val  btnEnd :Button =itemView.findViewById(R.id.btn_end)
    }
}
