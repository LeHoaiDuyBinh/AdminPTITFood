package com.example.ptitfoodadmin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptitfoodadmin.adapter.RevenueAdapter
import com.example.ptitfoodadmin.model.OrderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RevenueActivity : AppCompatActivity() {
    private lateinit var adapter: RevenueAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalRevenue: TextView
    private lateinit var btnPickDate: Button
    private lateinit var tvSelectedMonthYear: TextView
    private lateinit var btnDeleteMonthYear: Button
    private var selectedMonth: Int = -1
    private var selectedYear: Int = -1
    private lateinit var allOrderItems: ArrayList<OrderItem>

    private val CREATE_FILE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revenue)

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnPickDate = findViewById(R.id.btnPickDate)
        btnDeleteMonthYear = findViewById(R.id.btnDeleteMonthYear)
        tvSelectedMonthYear = findViewById(R.id.tvSelectedMonthYear)
        btnDeleteMonthYear.visibility = View.GONE
        btnPickDate.setOnClickListener {
            showMonthYearPickerDialog()
        }
        btnDeleteMonthYear.setOnClickListener {
            deleteMonthYear()
        }
        recyclerView = findViewById(R.id.recyclerViewRevenue)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RevenueAdapter(this, ArrayList())
        recyclerView.adapter = adapter
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue)

        // Khởi tạo list rỗng để lưu trữ tất cả dữ liệu
        allOrderItems = ArrayList()

        // Load tất cả dữ liệu từ Firebase khi mở trang
        loadAllDataFromFirebase()

        val btnExportPdf = findViewById<Button>(R.id.btnExportPdf)
        btnExportPdf.setOnClickListener {
            generatePdf()
        }
    }

    private fun showMonthYearPickerDialog() {
        val dialog = android.app.AlertDialog.Builder(this@RevenueActivity)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.month_year_picker_dialog, null)
        dialog.setView(dialogView)

        val numberPickerMonth = dialogView.findViewById<NumberPicker>(R.id.numberPickerMonth)
        val numberPickerYear = dialogView.findViewById<NumberPicker>(R.id.numberPickerYear)

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val months = arrayOf("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12")

        numberPickerMonth.minValue = 1
        numberPickerMonth.maxValue = months.size
        numberPickerMonth.displayedValues = months

        numberPickerYear.minValue = 2020
        numberPickerYear.maxValue = currentYear

        dialog.setPositiveButton("OK") { _, _ ->
            selectedMonth = numberPickerMonth.value
            selectedYear = numberPickerYear.value
            // Cập nhật nội dung của TextView với tháng và năm đã chọn
            val selectedMonthYearText = "Tháng ${selectedMonth}, năm ${selectedYear}"
            tvSelectedMonthYear.visibility = View.VISIBLE
            tvSelectedMonthYear.text = selectedMonthYearText
            Log.d("RevenueActivity", "Selected month: $selectedMonth, year: $selectedYear")
            btnDeleteMonthYear.visibility = View.VISIBLE
            displayDataForSelectedMonthYear(selectedMonth, selectedYear)
        }
        dialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        dialog.show()
    }

    private fun loadAllDataFromFirebase() {
        val orderHistoryRef = FirebaseDatabase.getInstance().getReference("OrderHistory")
        orderHistoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderItems = ArrayList<OrderItem>()
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val orderItem = orderSnapshot.getValue(OrderItem::class.java)
                        orderItem?.let {
                            orderItems.add(it)
                        }
                    }
                }
                allOrderItems = orderItems
                displayData(orderItems)
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    private fun displayDataForSelectedMonthYear(selectedMonth: Int, selectedYear: Int) {
        val filteredOrderItems = ArrayList<OrderItem>()
        for (orderItem in allOrderItems) {
            val updatedTimeCalendar = convertStringToCalendar(orderItem.updatedTime)
            if (updatedTimeCalendar.get(Calendar.MONTH) + 1 == selectedMonth &&
                updatedTimeCalendar.get(Calendar.YEAR) == selectedYear) {
                filteredOrderItems.add(orderItem)
            }
        }
        // Hiển thị dữ liệu đã lọc
        displayData(filteredOrderItems)
    }

    private fun deleteMonthYear() {
        // Ẩn TextView và nút xóa
        tvSelectedMonthYear.visibility = View.GONE
        btnDeleteMonthYear.visibility = View.GONE
        displayData(allOrderItems)
    }
    private fun displayData(orderItems: List<OrderItem>) {
        // Hiển thị dữ liệu trong RecyclerView và TextView
        adapter.updateData(orderItems)
        val totalRevenue = calculateTotalRevenue(orderItems)
        tvTotalRevenue.text = "Tổng doanh thu: ${DecimalFormat("#,###").format(totalRevenue)} đ"
        adapter.notifyDataSetChanged()
    }

    private fun convertStringToCalendar(updatedTime: String): Calendar {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(updatedTime)
        calendar.time = date
        return calendar
    }

    private fun calculateTotalRevenue(orderList: List<OrderItem>): Float {
        var totalRevenue = 0f
        for (order in orderList) {
            // Thêm giá trị của mỗi đơn hàng vào tổng số tiền
            totalRevenue += order.totalPrice.replace(".", "").replace("đ", "").toFloatOrNull() ?: 0f
        }
        return totalRevenue
    }

    private fun generatePdf() {
        val filteredData = if (selectedMonth != -1 && selectedYear != -1) {
            filterDataByMonthAndYear(selectedMonth, selectedYear)
        } else {
            allOrderItems
        }

        if (filteredData.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_TITLE, "revenue_report.pdf")
            }
            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Không có dữ liệu để in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterDataByMonthAndYear(month: Int, year: Int): List<OrderItem> {
        return allOrderItems.filter { orderItem ->
            val calendar = convertStringToCalendar(orderItem.updatedTime)
            calendar.get(Calendar.MONTH) + 1 == month && calendar.get(Calendar.YEAR) == year
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                createPdf(uri, if (selectedMonth != -1 && selectedYear != -1) filterDataByMonthAndYear(selectedMonth, selectedYear) else allOrderItems)
            }
        }
    }

    private fun createPdf(uri: Uri, orderItems: List<OrderItem>) {
        contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
            FileOutputStream(descriptor.fileDescriptor).use { out ->
                try {
                    val pdfWriter = PdfWriter(out)
                    val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                    val document = Document(pdfDocument)

                    val title = Paragraph("Báo cáo doanh thu")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(26f)
                    document.add(title)

                    for ((index, orderItem) in orderItems.withIndex()) {
                        val detail = Paragraph("${index + 1}. ${orderItem.orderCode}: ${orderItem.totalPrice}")
                            .setFontSize(16f)
                            .setMarginLeft(20f)
                            .setMarginTop(10f)
                        document.add(detail)
                    }

                    val totalPrice = Paragraph("Total Revenue: ${DecimalFormat("#,###").format(calculateTotalRevenue(orderItems))} đ")
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontSize(20f)
                        .setBold()
                    document.add(totalPrice)

                    document.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
