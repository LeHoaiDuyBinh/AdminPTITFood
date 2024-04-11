package com.example.ptitfoodadmin.model

/*
data class FoodItem(
    val name: String,
    val price: Int,
    val quantity: Int,
    val url: String
)*/
data class FoodItem(
    val foodImage: String = "",
    val foodName: String = "",
    val foodNote: String = "",
    val foodPrice: String = "",
    val foodQuantity: Int = 0,
    var foodTopping: List<ToppingItem> = listOf()
) {
    constructor() : this("", "", "", "", 0, listOf())
}