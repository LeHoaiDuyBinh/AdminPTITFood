package com.example.ptitfoodadmin.model

data class AllMenu(
    val foodName :String? = null,
    val foodPrice :String? = null,
    val foodDescription :String? = null,
    val foodImage :String? = null,
    val foodTopping :MutableList<FoodTopping>? = null,
)

