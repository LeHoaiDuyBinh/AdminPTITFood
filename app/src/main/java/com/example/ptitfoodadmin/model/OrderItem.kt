package com.example.ptitfoodadmin.model

data class OrderItem(
    val address: String = "",
    val createdTime: String = "",
    val orderCode: String = "",
    var orderDetail: List<FoodItem> = listOf(),
    val orderStatus: Int = 0,
    val phone: String = "",
    val totalPrice: String = "",
    val updatedTime: String = "",
    val userId: String = "",
    val userName: String = ""
) {
    constructor() : this("", "", "", listOf(), 0, "", "", "", "", "")
}