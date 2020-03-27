package com.example.menu.Data

import java.text.DecimalFormat
import java.util.*


// this is where data from dialogs or activities or fragments get saved temporarily before applying it on the Realm Database
object DataClass {
    var typeTitle: String? = null
    var itemId: String? = null
    var selectedDate: Date? = Calendar.getInstance().time
    var expense: Double = 0.0

    fun prettyCount(number:Number):String {
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
        val base = value / 3
        if (value >= 3 && base < suffix.size)
        {
            return DecimalFormat("#0.0").format(numValue / Math.pow(10.0, (base * 3).toDouble())) + suffix[base]
        }
        else
        {
            return DecimalFormat("#,##0").format(numValue)
        }
    }

}