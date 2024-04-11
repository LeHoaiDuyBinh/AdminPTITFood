package com.example.ptitfoodadmin.model

data class ToppingItem(
    val toppingName: String = "",
    val toppingPrice: String = "",
    val toppingQuantity: Int = 0
) {
    constructor() : this("", "", 0)

    override fun toString(): String {
        return "$toppingName x $toppingQuantity"
    }

}