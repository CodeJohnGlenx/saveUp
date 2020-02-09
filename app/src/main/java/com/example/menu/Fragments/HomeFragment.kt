package com.example.menu.Fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.graphics.drawable.toDrawable

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menu.Adapter.ExpendituresAdapter
import com.example.menu.Adapter.TypeDataAdapter
import com.example.menu.Data.DataClass
import com.example.menu.Dialogs.CustomListViewDialog
import com.example.menu.Dialogs.FillUpDialog
import com.example.menu.Model.Supplier
import kotlinx.android.synthetic.main.fragment_home.*

import androidx.core.content.ContextCompat
import android.R.string.cancel
import android.widget.CompoundButton
import android.widget.Switch
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import com.google.android.material.navigation.NavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.list_item.*
import java.text.SimpleDateFormat
import java.util.*


// all about Home Fragment
class HomeFragment : Fragment(),
    TypeDataAdapter.RecyclerViewItemClickListener {  // responsible for handling click on every item on recycler view
    var customDialog: CustomListViewDialog? = null  // this is the Choose Type dialog
    var customFillUpDialog: FillUpDialog? = null  // this is the Fill Up dialog
    val itemConfig = RealmConfiguration.Builder().name("items.realm").build()
    val itemRealm = Realm.getInstance(itemConfig)
    var removedItemConfig = RealmConfiguration.Builder().name("removedItems.realm").build()
    var removedItemRealm = Realm.getInstance(removedItemConfig)
    val deleteDateSelectionConfig = RealmConfiguration.Builder().name("deleteDateSelection.realm").build()
    val deleteDateSelectionRealm = Realm.getInstance(deleteDateSelectionConfig)
    var deleteDateSelection = deleteDateSelectionRealm.where(ItemModel::class.java).equalTo("itemId", "deleteDateSelection").findFirst()



    val TAG = "Home Fragment"
    override fun onAttach(context: Context) {
        Log.d(TAG, "On Create")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        Realm.init(activity!!)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "On Create View")


        return inflater!!.inflate(
            com.example.menu.R.layout.fragment_home,
            container,
            false
        )  // inflates the fragment_home in this home fragment

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFABClick()  // instantiates the floating action button
        setupRecyclerView()  // instantiates the recycler viewer
        displayProgressBar()
        setTheme()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Activity Created")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        Log.d(TAG, "On Resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "On Pause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "On Stop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(TAG, "On Destroy View")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "On Destroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "On Detach")
        super.onDetach()
    }

    private fun setupRecyclerView() {  // setting up the recycler viewer
        val layoutManager = LinearLayoutManager(context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val adapter = ExpendituresAdapter(context!!, Supplier.expenditures)
        recyclerView.adapter = adapter
    }

    override fun clickOnItem(data: String) {  // when item on custom dialog/ choose type dialog is clicked
        if (customDialog != null) {
            //customDialog!!.dismiss()
            Handler().postDelayed(
                {
                    customDialog!!.dismiss()
                },
                200
            )  // delayed action to show the item being clicked

            DataClass.typeTitle =
                data  // setting the DataClass.typeTitle's value on the value(text) of the choose type item clicked

            // responsible for showing the fill up dialog
            customFillUpDialog = FillUpDialog(activity!!)
            customFillUpDialog!!.show()
        }
    }

    fun onFABClick() {  // responsible for setting the options on choose type dialog/ custom dialog
        floatingActionButton.setOnClickListener {
            val items = arrayOf(
                "Beverages",
                "Cash Deposit",
                "Fare",
                "Food",
                "Health",
                "Miscellaneous",
                "School Expenses",
                "Entertainment"

            )


            // responsible for setting up items on the custom dialog
            val dataAdapter = TypeDataAdapter(items, this)
            customDialog = CustomListViewDialog(activity!!, dataAdapter)
            customDialog!!.show()
            customDialog!!.setCanceledOnTouchOutside(true)


        }
    }

    private fun displayProgressBar() {
        var draw = ContextCompat.getDrawable(
            context!!,
            com.example.menu.R.drawable.custom_progress_bar_green
        )
        var fundValue: Double = 0.0
        var expensesValue: Double = 0.0
        var balanceValue: Double = 0.0
        var expenses: Double = 0.0
        var balances: Double = 0.0


        for (item in Supplier.expenditures) {
            if (item.type != "Cash Deposit") {
                expenses = expenses + item.price
                expensesValue = expensesValue + item.price
            } else {
                balances = balances + item.price
                fundValue = fundValue + item.price
            }
        }
        balanceValue = fundValue - expensesValue

        tv_fund_value.text = fundValue.toString()
        tv_expenses_value.text = expensesValue.toString()
        tv_balance_value.text = balanceValue.toString()

        Log.e(TAG, "$expenses")
        Log.e(TAG, "$balances")


        progressBar.progress = ((balances - expenses) / balances * 100).toInt()
        if (progressBar.progress >= 50) {
            draw = ContextCompat.getDrawable(
                context!!,
                com.example.menu.R.drawable.custom_progress_bar_green
            )
        } else if (progressBar.progress >= 35) {
            draw = ContextCompat.getDrawable(
                context!!,
                com.example.menu.R.drawable.custom_progress_bar_yellow
            )
        } else if (progressBar.progress >= 20) {
            draw = ContextCompat.getDrawable(
                context!!,
                com.example.menu.R.drawable.custom_progress_bar_orange
            )
        } else if (fundValue == 0.0 && balanceValue == 0.0 && expensesValue == 0.0) {
            draw = ContextCompat.getDrawable(
                context!!,
                com.example.menu.R.drawable.custom_progress_bar_light_grey
            )
        } else {
            draw = ContextCompat.getDrawable(
                context!!,
                com.example.menu.R.drawable.custom_progress_bar_red
            )
        }

        progressBar.progressDrawable = draw
    }


    fun setTheme() {
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        if (themeMode!!.itemType == "Dark Mode") {
            // home background color
            fragment_home_layout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))

            // progress card view
            progressCardView.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            tv_fund.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_fund_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            divider.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_expenses.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_expenses_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            dividerTwo.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_balance.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_balance_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        } else {
            // home background color
            fragment_home_layout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))

            // progress card view
            progressCardView.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.light_white))
            tv_fund.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
            tv_fund_value.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
            divider.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
            tv_expenses.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
            tv_expenses_value.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
            dividerTwo.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
            tv_balance.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
            tv_balance_value.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
        }

    }

    private fun setDeleteDateSelection() {
        val todayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val thisMonthDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        val thisWeekDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
        val thisTwoWeeksDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
        val thisYearDateFormat = SimpleDateFormat("yyyy", Locale.US)
        val pickADateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val thisSixMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        val thisThreeDaysDateFormat = SimpleDateFormat("D", Locale.US)
        val thisThreeMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)

        when(deleteDateSelection!!.itemTitle) {
            "over this day" -> {
                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if (todayDateFormat.format(Date()) != todayDateFormat.format(thisItem.itemDate!!.time)) {
                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()
                    }

                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if (todayDateFormat.format(Date()) != todayDateFormat.format(thisRemovedItem.itemDate!!.time)) {
                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()
                    }

                }
            }
            "over three days" -> {
                val yesterdayCalendar = Calendar.getInstance()
                val dayBeforeYesterdayCalendar = Calendar.getInstance()

                yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
                dayBeforeYesterdayCalendar.add(Calendar.DAY_OF_YEAR, -2)

                val yesterdayDate = yesterdayCalendar.time
                val dayBeforeYesterdayDate = dayBeforeYesterdayCalendar.time

                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if((thisThreeDaysDateFormat.format(Date()) != thisThreeDaysDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisThreeDaysDateFormat.format(yesterdayDate.time) != thisThreeDaysDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisThreeDaysDateFormat.format(dayBeforeYesterdayDate.time) != thisThreeDaysDateFormat.format(thisItem.itemDate!!.time))) {

                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()
                    }
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if((thisThreeDaysDateFormat.format(Date()) != thisThreeDaysDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisThreeDaysDateFormat.format(yesterdayDate.time) != thisThreeDaysDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisThreeDaysDateFormat.format(dayBeforeYesterdayDate.time) != thisThreeDaysDateFormat.format(thisRemovedItem.itemDate!!.time))) {

                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()
                    }

                }

            }
            "over this week" -> {
                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if (thisWeekDateFormat.format(Date()) != thisWeekDateFormat.format(thisItem.itemDate!!.time)) {
                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()
                    }
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if (thisWeekDateFormat.format(Date()) != thisWeekDateFormat.format(thisRemovedItem.itemDate!!.time)) {
                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()
                    }
                }
            }
            "over two weeks" -> {
                val lastWeek = Calendar.getInstance()
                lastWeek.add(Calendar.WEEK_OF_MONTH, -1)
                val lastWeekDate = lastWeek.time

                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if ((thisTwoWeeksDateFormat.format(Date()) != thisTwoWeeksDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisTwoWeeksDateFormat.format(lastWeekDate.time) != thisTwoWeeksDateFormat.format(thisItem.itemDate!!.time))) {
                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()
                    }
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if ((thisTwoWeeksDateFormat.format(Date()) != thisTwoWeeksDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisTwoWeeksDateFormat.format(lastWeekDate.time) != thisTwoWeeksDateFormat.format(thisRemovedItem.itemDate!!.time))) {
                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()
                    }
                }


            }
            "over this month" -> {
                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if (thisMonthDateFormat.format(Date()) != thisMonthDateFormat.format(thisItem.itemDate!!.time)) {
                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()
                    }
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if (thisMonthDateFormat.format(Date()) != thisMonthDateFormat.format(thisRemovedItem.itemDate!!.time)) {
                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()
                    }
                }
            }
            "over three months" -> {
                val onePreviousMonthCalendar = Calendar.getInstance()
                val twoPreviousMonthCalendar = Calendar.getInstance()

                onePreviousMonthCalendar.add(Calendar.MONTH, -1)
                twoPreviousMonthCalendar.add(Calendar.MONTH, -2)

                val onePreviousMonthDate = onePreviousMonthCalendar.time
                val twoPreviousMonthDate = twoPreviousMonthCalendar.time

                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if ((thisThreeMonthsDateFormat.format(Date()) != thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisThreeMonthsDateFormat.format(onePreviousMonthDate.time) != thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisThreeMonthsDateFormat.format(twoPreviousMonthDate.time) != thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time))) {

                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()

                    }
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if ((thisThreeMonthsDateFormat.format(Date()) != thisThreeMonthsDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisThreeMonthsDateFormat.format(onePreviousMonthDate.time) != thisThreeMonthsDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisThreeMonthsDateFormat.format(twoPreviousMonthDate.time) != thisThreeMonthsDateFormat.format(thisRemovedItem.itemDate!!.time))) {

                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()

                    }
                }


            }
            "over six months" -> {
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

                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if ((thisSixMonthsDateFormat.format(Date()) != thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(onePreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(twoPreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(threePreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(fourPreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(fivePreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))) {

                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()

                    }
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if ((thisSixMonthsDateFormat.format(Date()) != thisSixMonthsDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(onePreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(twoPreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(threePreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(fourPreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisRemovedItem.itemDate!!.time)) &&
                        (thisSixMonthsDateFormat.format(fivePreviousMonthDate.time) != thisSixMonthsDateFormat.format(thisRemovedItem.itemDate!!.time))) {

                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()

                    }
                }
            }
            "over this year" -> {
                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    if (thisYearDateFormat.format(Date()) != thisYearDateFormat.format(thisItem.itemDate!!.time)) {
                        itemRealm.beginTransaction()
                        thisItem.deleteFromRealm()
                        itemRealm.commitTransaction()
                    }
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { thisRemovedItem ->
                    if (thisYearDateFormat.format(Date()) != thisYearDateFormat.format(thisRemovedItem.itemDate!!.time)) {
                        removedItemRealm.beginTransaction()
                        thisRemovedItem.deleteFromRealm()
                        removedItemRealm.commitTransaction()
                    }
                }
            }
            else -> {
                // new year new me, bitch
            }

        }
    }

}


