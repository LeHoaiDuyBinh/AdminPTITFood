package com.example.ptitfoodadmin

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.example.ptitfoodadmin.model.OrderItem
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Random

class ChartActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("OrderHistory")

        val barChart = findViewById<BarChart>(R.id.barChart)
        val pieChart = findViewById<PieChart>(R.id.pieChart)

        fetchChartData(barChart, pieChart)
    }

    private fun fetchChartData(barChart: BarChart, pieChart: PieChart) {
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val monthlySalesMap = HashMap<String, Float>()
                val foodSalesMap = HashMap<String, Float>()

                for (userSnapshot in dataSnapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val order = orderSnapshot.getValue(OrderItem::class.java)
                        if (order != null) {
                            val updatedTime = order.updatedTime.split("/")[1] // Lấy tháng từ updatedTime
                            val orderTotalPrice = order.totalPrice.replace(".", "").replace("đ", "").toFloatOrNull() ?: 0f

                            if (updatedTime.isNotEmpty()) {
                                monthlySalesMap[updatedTime] = (monthlySalesMap[updatedTime] ?: 0f) + orderTotalPrice
                            }

                            order.orderDetail.forEach { foodItem ->
                                val foodName = foodItem.foodName
                                if (foodName.isNotEmpty()) {
                                    foodSalesMap[foodName] = (foodSalesMap[foodName] ?: 0f) + foodItem.foodQuantity
                                }
                            }
                        }
                    }
                }
                Log.d("ChartActivity", "Daily Sales: $monthlySalesMap")
                Log.d("ChartActivity", "Food Sales: $foodSalesMap")

                updateBarChart(barChart, monthlySalesMap)
                updatePieChart(pieChart, foodSalesMap)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ChartActivity", "Error fetching data from Firebase: ${databaseError.message}")
            }
        })
    }


    private fun updateBarChart(barChart: BarChart, data: HashMap<String, Float>) {
        // Tạo danh sách để lưu trữ giá trị của dữ liệu và tháng tương ứng
        val values = ArrayList<Float>()
        val months = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        // Khởi tạo giá trị mặc định cho mỗi tháng
        for (month in 1..12) {
            val monthKey = if (month < 10) "0$month" else "$month" // Chuyển đổi số tháng thành chuỗi (ví dụ: 1 -> "01", 2 -> "02", vv)
            val value = data[monthKey] ?: 0f // Lấy giá trị từ dữ liệu nếu có, nếu không thì gán mặc định là 0
            values.add(value)
        }

        // Tạo danh sách BarEntry từ giá trị dữ liệu
        val entries = ArrayList<BarEntry>()
        for (i in values.indices) {
            entries.add(BarEntry(i.toFloat(), values[i]))
        }

        // Tạo DataSet từ danh sách BarEntry
        val dataSet = BarDataSet(entries, "Doanh thu")
        dataSet.color = Color.RED
        dataSet.valueTextSize = 12f
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD

        // Tạo BarData từ DataSet
        val barData = BarData(dataSet)

        // Đặt nhãn từ danh sách tháng
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.data = barData
        barChart.animateY(1000)
        barChart.invalidate()
    }




    private fun updatePieChart(pieChart: PieChart, data: HashMap<String, Float>) {
        val entries = ArrayList<PieEntry>()

        // Tính tổng số lượng món để tính phần trăm
        val totalQuantity = data.values.sum()

        data.forEach { (label, value) ->
            // Tính phần trăm
            val percentage = (value / totalQuantity) * 100

            // Thêm PieEntry mới với phần trăm và nhãn tương ứng
            entries.add(PieEntry(percentage, label))
        }

        // Tạo DataSet và cấu hình màu sắc
        val dataSet = PieDataSet(entries, "Food Sales")
        dataSet.colors = generateColors(data.keys.size) // Tạo một danh sách màu sắc cho các món

        // Tạo PieData và cập nhật PieChart
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        pieChart.setDrawEntryLabels(true)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.invalidate()
    }

    private fun generateColors(size: Int): List<Int> {
        val colors = ArrayList<Int>()
        val random = Random()

        while (colors.size < size) {
            // Tạo màu ngẫu nhiên và thêm vào danh sách màu nếu màu đó chưa tồn tại
            val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            if (!colors.contains(color)) {
                colors.add(color)
            }
        }

        return colors
    }

}
