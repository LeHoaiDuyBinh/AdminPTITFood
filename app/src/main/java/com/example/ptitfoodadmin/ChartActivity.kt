//package com.example.myapplication
//
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.Typeface
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.ImageButton
//import com.github.mikephil.charting.charts.BarChart
//import com.github.mikephil.charting.charts.PieChart
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.BarEntry
//import com.github.mikephil.charting.data.PieData
//import com.github.mikephil.charting.data.PieDataSet
//import com.github.mikephil.charting.data.PieEntry
//
//class ChartActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chart)
//
//        val btnBack = findViewById<ImageButton>(R.id.backButton)
//        btnBack.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        val barChart = findViewById<BarChart>(R.id.barChart)
//        val pieChart = findViewById<PieChart>(R.id.pieChart)
//
//        // Sample data for bar chart
//        val barChartData = mapOf(
//            "January" to 100.0f,
//            "February" to 200.0f,
//            "March" to 150.0f,
//            "April" to 300.0f,
//            "May" to 250.0f
//        )
//        setupBarChart(barChart, barChartData)
//
//        // Sample data for pie chart
//        val pieChartData = mapOf(
//            "Pizza" to 30.0f,
//            "Burger" to 20.0f,
//            "Sushi" to 15.0f,
//            "Pasta" to 25.0f,
//            "Salad" to 10.0f
//        )
//        setupPieChart(pieChart, pieChartData)
//    }
//
//    private fun setupBarChart(barChart: BarChart, data: Map<String, Float>) {
//        val entries = ArrayList<BarEntry>()
//        data.forEach { (label, value) ->
//            entries.add(BarEntry(entries.size.toFloat(), value))
//        }
//
//        val dataSet = BarDataSet(entries, "Revenue")
//        dataSet.color = Color.RED
//
//        val barData = BarData(dataSet)
//        barChart.data = barData
//
//        // Customize chart
//        barChart.setFitBars(true)
//        barChart.animateY(1000)
//        barChart.invalidate()
//    }
//
//    private fun setupPieChart(pieChart: PieChart, data: Map<String, Float>) {
//        val entries = ArrayList<PieEntry>()
//        data.forEach { (label, value) ->
//            entries.add(PieEntry(value, label))
//        }
//
//        val dataSet = PieDataSet(entries, "Food Sales")
//        dataSet.colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA)
//
//        val pieData = PieData(dataSet)
//        pieChart.data = pieData
//
//        // Customize chart
//        pieChart.setDrawEntryLabels(true)
//        pieChart.setUsePercentValues(true)
//        pieChart.description.isEnabled = false
//        pieChart.invalidate()
//    }
//}