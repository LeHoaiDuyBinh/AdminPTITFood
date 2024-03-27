package com.example.ptitfoodadmin.model

data class OrderItem(
    val name: String,
    val status: Int,
    val id: Int,
    val totalPrice: Int,
    val foodList: Map<String, FoodItem>
)