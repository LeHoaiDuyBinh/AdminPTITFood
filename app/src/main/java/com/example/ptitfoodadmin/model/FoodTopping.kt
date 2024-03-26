package com.example.ptitfoodadmin.model

data class FoodTopping(val name: String, val price: String) {
    // Empty constructor required by Firebase
    constructor() : this("", "") {}
}