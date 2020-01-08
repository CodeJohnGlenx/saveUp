package com.example.menu.RealmClass

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

// Realm Classes
// items.realm
// removedItems.realm
// themeMode.realm  // id: themeMode  // title/type: Light Mode or Dark Mode
@RealmClass
open class ItemModel() : RealmObject() {
    @PrimaryKey
    var itemId: String? = null
    var itemType: String? = null
    var itemTitle: String? = null
    var itemDate: Date? = null
    var itemTime: Date? = null
    var itemValue: Double?  = null
}
