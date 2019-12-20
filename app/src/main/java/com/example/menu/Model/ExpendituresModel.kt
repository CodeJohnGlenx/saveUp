package com.example.menu.Model


// used to display the data on recycler viewer
data class Expenditure(var title: String, var price: Double, var id: String, var type: String)

object  Supplier {
    val expenditures = mutableListOf<Expenditure>()
}