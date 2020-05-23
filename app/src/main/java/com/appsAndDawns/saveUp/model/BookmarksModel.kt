package com.appsAndDawns.saveUp.model


// model used in bookmark
data class Bookmark(var title: String, var price: Double, var id:String, var type: String)

object BookmarkSupplier {
    var bookmarks = mutableListOf<Bookmark>(
    )

}