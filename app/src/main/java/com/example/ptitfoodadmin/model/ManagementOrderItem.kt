package com.example.ptitfoodadmin.model

data class ManagementOrderItem(

    val name: String,
    val id: String,
    val orderStatus: Int


){
    constructor() : this("", "", 0)
}