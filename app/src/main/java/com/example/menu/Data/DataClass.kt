package com.example.menu.Data

import java.util.*


// this is where data from dialogs or activities or fragments get saved temporarily before applying it on the Realm Database
object DataClass {
    var typeTitle: String? = null
    var itemId: String? = null
    var selectedDate: Date? = Calendar.getInstance().time
    var expense: Double = 0.0
    var balance: Double = 0.0
}