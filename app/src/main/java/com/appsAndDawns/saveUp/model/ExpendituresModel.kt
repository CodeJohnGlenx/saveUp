package com.appsAndDawns.saveUp.model


// models used in items
data class Expenditure(var title: String, var price: Double, var id: String, var type: String)

object  Supplier {
    val expenditures = mutableListOf<Expenditure>()
}