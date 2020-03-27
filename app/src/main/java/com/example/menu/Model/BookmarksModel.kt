package com.example.menu.Model

data class Bookmark(var title: String, var price: Double, var id:String, var type: String)

object BookmarkSupplier {
    var bookmarks = mutableListOf<Bookmark>(
    )

    /*
        Bookmark("Tubig", 10.0, "aljtlajtl", "Beverages"),
        Bookmark("7/11 Sisig", 29.0, "fafaf", "Food"),
        Bookmark("Pamasahe", 8.0, "aljafafaftlajtl", "Fare")
     */
}