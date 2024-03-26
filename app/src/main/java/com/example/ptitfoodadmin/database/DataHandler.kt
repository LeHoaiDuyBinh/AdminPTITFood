package com.example.ptitfoodadmin.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DataHandler {
    fun addFirebaseData() {
        val database = FirebaseDatabase.getInstance()
        val rootRef: DatabaseReference = database.reference
        val giuakiLocRef: DatabaseReference = rootRef.child("giuaki-loc")
      //  giuakiLocRef.removeValue()
        val order0Ref: DatabaseReference = giuakiLocRef.child("order").child("Order 1")
        order0Ref.child("price").setValue(745000)
        order0Ref.child("id").setValue(1)
        order0Ref.child("name").setValue("Lê Hoài Duy Bình4")
        order0Ref.child("status").setValue("0")

        val order1Ref: DatabaseReference = order0Ref.child("orderList")
        addProductData(order1Ref, "https://i.imgur.com/tDw1bBe.png", "Gà KFC", 15000, 10)
        addProductData(order1Ref, "https://i.imgur.com/6Zfd0Bf.png", "Hamburger", 10000, 5)
        addProductData(order1Ref, "https://i.imgur.com/vEX920Y.png", "Pizza", 10000, 3)

        val order2Ref: DatabaseReference = giuakiLocRef.child("order").child("5")
        order2Ref.child("price").setValue(495000)
        order2Ref.child("id").setValue(5)
        order2Ref.child("name").setValue("Gia Lộc")
        order2Ref.child("status").setValue("1")

        val order2ListRef: DatabaseReference = order2Ref.child("orderList")
        addProductData(order2ListRef, "https://i.imgur.com/tDw1bBe.png", "Gà KFC", 15000, 5)
        addProductData(order2ListRef, "https://i.imgur.com/6Zfd0Bf.png", "Hamburger", 10000, 5)
        addProductData(order2ListRef, "https://i.imgur.com/vEX920Y.png", "Pizza", 10000, 3)

        val order3Ref: DatabaseReference = giuakiLocRef.child("order").child("6")
        order3Ref.child("price").setValue(455000)
        order3Ref.child("id").setValue(6)
        order3Ref.child("name").setValue("Vân Anh")
        order3Ref.child("status").setValue("2")

        val order3ListRef: DatabaseReference = order3Ref.child("orderList")
        addProductData(order3ListRef, "https://i.imgur.com/tDw1bBe.png", "Gà KFC", 15000, 5)
        addProductData(order3ListRef, "https://i.imgur.com/6Zfd0Bf.png", "Hamburger", 10000, 5)
        addProductData(order3ListRef, "https://i.imgur.com/vEX920Y.png", "Pizza", 10000, 2)
    }

    fun addProductData(orderListRef: DatabaseReference, url: String, name: String, price: Int, quantity: Int) {
        val productData = HashMap<String, Any>()
        productData["url"] = url
        productData["name"] = name
        productData["price"] = price
        val productRef: DatabaseReference = orderListRef.child(quantity.toString())
        productRef.setValue(productData)
        productRef.child("soluong").setValue(quantity)
    }
}