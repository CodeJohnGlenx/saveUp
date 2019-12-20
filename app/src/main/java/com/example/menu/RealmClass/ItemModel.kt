package com.example.menu.RealmClass

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class ItemModel() : RealmObject() {  // item model of every item
    @PrimaryKey
    var itemId: String? = null
    var itemType: String? = null
    var itemTitle: String? = null
    var itemDate: Date? = null
    var itemTime: Date? = null
    var itemValue: Double?  = null
}
