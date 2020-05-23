package com.appsAndDawns.saveUp.data

import com.appsAndDawns.saveUp.model.Expenditure
import com.appsAndDawns.saveUp.model.Supplier
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import java.lang.Math.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


// this is where data from dialogs or activities or fragments get saved temporarily before applying it on the Realm Database
object DataClass {
    var typeTitle: String? = null
    var itemId: String? = null
    var selectedDate: Date? = Calendar.getInstance().time
    var todayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    var thisMonthDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
    private val thisWeekDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
    private val thisTwoWeeksDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
    private val thisYearDateFormat = SimpleDateFormat("yyyy", Locale.US)
    private val thisSixMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
    private val thisThreeDaysDateFormat = SimpleDateFormat("D", Locale.US)
    private val thisThreeMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
    private val includeItemsConfig: RealmConfiguration = RealmConfiguration.Builder().name("includeItems.realm").build()
    private val includeItemsRealm: Realm = Realm.getInstance(includeItemsConfig)
    val includeItems = includeItemsRealm.where(ItemModel::class.java).equalTo("itemId", "includeItems").findFirst()
    val config: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    val realm: Realm = Realm.getInstance(config)
    private val tutorialConfig: RealmConfiguration = RealmConfiguration.Builder().name("tutorialRealm.realm").build()
    val tutorialRealm: Realm = Realm.getInstance(tutorialConfig)
    var tutorialHome = tutorialRealm.where(ItemModel::class.java).equalTo("itemId", "home").findFirst()
    var tutorialCalendar = tutorialRealm.where(ItemModel::class.java).equalTo("itemId", "calendar").findFirst()
    var tutorialSettings: ItemModel? =  tutorialRealm.where(ItemModel::class.java).equalTo("itemId", "settings").findFirst()
    var tutorialTrash =  tutorialRealm.where(ItemModel::class.java).equalTo("itemId", "trash").findFirst()
    var tutorialStatistics =  tutorialRealm.where(ItemModel::class.java).equalTo("itemId", "statistics").findFirst()
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()


    fun prettyCount(number:Number):String {
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val numValueDouble = number.toDouble()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3

        if (numValue < 1000) {
            return numValueDouble.toString()
        } else {
            return when (value >= 3 && base < suffix.size)
            {
                true -> DecimalFormat("#0.0").format(numValue / pow(10.0, (base * 3).toDouble())) + suffix[base]
                false -> DecimalFormat("#,##0").format(numValue)
            }
        }


    }

     fun insertSupplierItems(realm: Realm, includeItems: ItemModel?) {
        Supplier.expenditures.clear()
        val allItems = realm.where(ItemModel::class.java).findAll()

        when (includeItems!!.itemType) {
            "include all items" -> {
                allItems?.let {
                    allItems.forEach { thisItem ->
                        Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                    }
                }
            }
            "within this day" -> {
                allItems?.let {
                    allItems.forEach { thisItem ->
                        val thisItemDate = todayDateFormat.format(thisItem.itemDate!!.time)
                        if (todayDateFormat.format(selectedDate!!.time) == thisItemDate)
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                    }
                }
            }
            "within this week" -> {
                allItems?.let {
                    allItems.forEach { thisItem ->
                        if (thisWeekDateFormat.format(Date()) == thisWeekDateFormat.format(thisItem.itemDate!!.time)) {
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                    }
                }
            }
            "within this month" -> {
                allItems?.let {
                    allItems.forEach { thisItem ->
                        if (thisMonthDateFormat.format(Date()) == thisMonthDateFormat.format(thisItem.itemDate!!.time)) {
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                    }
                }
            }
            "within this year" -> {
                allItems?.let {
                    allItems.forEach { thisItem ->
                        if (thisYearDateFormat.format(Date()) == thisYearDateFormat.format(thisItem.itemDate!!.time)) {
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                    }
                }
            }
            "within three days" -> {
                val yesterdayCalendar = Calendar.getInstance()
                val dayBeforeYesterdayCalendar = Calendar.getInstance()

                yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
                dayBeforeYesterdayCalendar.add(Calendar.DAY_OF_YEAR, -2)

                val yesterdayDate = yesterdayCalendar.time
                val dayBeforeYesterdayDate = dayBeforeYesterdayCalendar.time

                allItems?.let {
                    allItems.forEach { thisItem ->
                        if((thisThreeDaysDateFormat.format(Date()) == thisThreeDaysDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisThreeDaysDateFormat.format(yesterdayDate.time) == thisThreeDaysDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisThreeDaysDateFormat.format(dayBeforeYesterdayDate.time) == thisThreeDaysDateFormat.format(thisItem.itemDate!!.time))) {
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                    }
                }
            }
            "within two weeks" -> {
                val lastWeek = Calendar.getInstance()
                lastWeek.add(Calendar.WEEK_OF_MONTH, -1)
                val lastWeekDate = lastWeek.time

                allItems?.let {
                    allItems.forEach { thisItem ->
                        if ((thisTwoWeeksDateFormat.format(Date()) == thisTwoWeeksDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisTwoWeeksDateFormat.format(lastWeekDate.time) == thisTwoWeeksDateFormat.format(thisItem.itemDate!!.time))) {
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                    }
                }
            }
            "within three months" -> {
                val onePreviousMonthCalendar = Calendar.getInstance()
                val twoPreviousMonthCalendar = Calendar.getInstance()

                onePreviousMonthCalendar.add(Calendar.MONTH, -1)
                twoPreviousMonthCalendar.add(Calendar.MONTH, -2)

                val onePreviousMonthDate = onePreviousMonthCalendar.time
                val twoPreviousMonthDate = twoPreviousMonthCalendar.time

                allItems?.let {
                    allItems.forEach { thisItem ->
                        if ((thisThreeMonthsDateFormat.format(Date()) == thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisThreeMonthsDateFormat.format(onePreviousMonthDate.time) == thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisThreeMonthsDateFormat.format(twoPreviousMonthDate.time) == thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time))) {
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                    }
                }
            }
            "within six months" -> {
                val onePreviousMonthCalendar = Calendar.getInstance()
                val twoPreviousMonthCalendar = Calendar.getInstance()
                val threePreviousMonthCalendar = Calendar.getInstance()
                val fourPreviousMonthCalendar = Calendar.getInstance()
                val fivePreviousMonthCalendar = Calendar.getInstance()

                onePreviousMonthCalendar.add(Calendar.MONTH, -1)
                twoPreviousMonthCalendar.add(Calendar.MONTH, -2)
                threePreviousMonthCalendar.add(Calendar.MONTH, -3)
                fourPreviousMonthCalendar.add(Calendar.MONTH, -4)
                fivePreviousMonthCalendar.add(Calendar.MONTH, -5)

                val onePreviousMonthDate = onePreviousMonthCalendar.time
                val twoPreviousMonthDate = twoPreviousMonthCalendar.time
                val threePreviousMonthDate = threePreviousMonthCalendar.time
                val fourPreviousMonthDate = fourPreviousMonthCalendar.time
                val fivePreviousMonthDate = fivePreviousMonthCalendar.time

                allItems?.let {
                    allItems.forEach { thisItem ->
                        if ((thisSixMonthsDateFormat.format(Date()) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisSixMonthsDateFormat.format(onePreviousMonthDate.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisSixMonthsDateFormat.format(twoPreviousMonthDate.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisSixMonthsDateFormat.format(threePreviousMonthDate.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisSixMonthsDateFormat.format(fourPreviousMonthDate.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) ||
                            (thisSixMonthsDateFormat.format(fivePreviousMonthDate.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))) {
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                    }
                }
            }

            else -> {

            }
        }
    }
}