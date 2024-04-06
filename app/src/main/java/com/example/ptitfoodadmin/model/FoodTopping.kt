package com.example.ptitfoodadmin.model

data class FoodTopping(val toppingName: String, val toppingPrice: String) {
    // Empty constructor required by Firebase
    constructor() : this("", "") {}
}