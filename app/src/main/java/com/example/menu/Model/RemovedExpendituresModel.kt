package com.example.menu.Model

data class RemovedExpenditure(var title: String, var price: Double, var id: String, var type: String)

object  RemovedSupplier {
    val removedExpenditures = mutableListOf<RemovedExpenditure>()
}