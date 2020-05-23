package com.appsAndDawns.saveUp.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsAndDawns.saveUp.adapter.ExpendituresAdapter
import com.appsAndDawns.saveUp.adapter.TypeDataAdapter
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.dialog.CustomListViewDialog
import com.appsAndDawns.saveUp.dialog.FillUpDialog
import com.appsAndDawns.saveUp.model.Supplier
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.core.content.ContextCompat
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.view.Window
import com.appsAndDawns.saveUp.adapter.BookmarksDialogAdapter
import com.appsAndDawns.saveUp.data.DataClass.prettyCount
import com.appsAndDawns.saveUp.model.Bookmark
import com.appsAndDawns.saveUp.model.BookmarkSupplier
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_dialog.*
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*


// all about Home Fragment
class HomeFragment : Fragment(),
    TypeDataAdapter.RecyclerViewItemClickListener {  // responsible for handling click on every item on recycler view
    private var customDialog: CustomListViewDialog? = null  // this is the Choose Type dialog
    private var customFillUpDialog: FillUpDialog? = null  // this is the Fill Up dialog
    private val itemConfig: RealmConfiguration? = RealmConfiguration.Builder().name("items.realm").build()
    private val itemRealm: Realm = Realm.getInstance(itemConfig!!)
    private var removedItemConfig: RealmConfiguration = RealmConfiguration.Builder().name("removedItems.realm").build()
    private var removedItemRealm: Realm = Realm.getInstance(removedItemConfig)
    private val deleteDateSelectionConfig: RealmConfiguration? = RealmConfiguration.Builder().name("deleteDateSelection.realm").build()
    private val deleteDateSelectionRealm: Realm? = Realm.getInstance(deleteDateSelectionConfig!!)
    private var deleteDateSelection = deleteDateSelectionRealm!!.where(ItemModel::class.java).equalTo("itemId", "deleteDateSelection").findFirst()
    private val bookmarkItemsConfig: RealmConfiguration = RealmConfiguration.Builder().name("bookmarkItemsRealm.realm").build()
    private val bookmarkItemsRealm: Realm = Realm.getInstance(bookmarkItemsConfig)
    private val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    private val themeRealm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()


    private lateinit var showcaseGuide:GuideView
    lateinit var showcaseBuilder: GuideView.Builder


    //private val logTag = "Home Fragment"

    /*
    override fun onAttach(context: Context) {
        Log.d(logTag, "On Create")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Create")
        //Realm.init(activity!!)
        super.onCreate(savedInstanceState)
    }

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Log.d(logTag, "On Create View")
        return inflater.inflate(
          R.layout.fragment_home,
            container,
            false
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doShowcase()
        onFABClick()
        setupRecyclerView()
        displayProgressBar()
        setTheme()
        onBookmarkFABClick()
        setUpBookmarkItems()
        setDeleteDateSelection()
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Activity Created")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        Log.d(logTag, "On Resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(logTag, "On Pause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(logTag, "On Stop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(logTag, "On Destroy View")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(logTag, "On Destroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(logTag, "On Detach")
        super.onDetach()
    }

     */

    private fun setupRecyclerView() {  // setting up the recycler viewer
        val layoutManager = LinearLayoutManager(context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val adapter = ExpendituresAdapter(context!!, Supplier.expenditures)
        recyclerView.adapter = adapter


    }

    override fun clickOnItem(data: String) {
        if (customDialog != null) {
            Handler().postDelayed(
                {
                    customDialog!!.dismiss()
                },
                200
            )  // delayed action to show the item being clicked

            DataClass.typeTitle = data  // setting the DataClass.typeTitle's value on the value(text) of the choose type item clicked

            customFillUpDialog = FillUpDialog(activity!!)
            customFillUpDialog!!.show()
        }
    }

    private fun onFABClick() {  // responsible for setting the options on choose type dialog/ custom dialog
        floatingActionButton.setOnClickListener {
            val items = arrayOf(
                getString(R.string.beverages),  // "Beverages"
                getString(R.string.bills),      // "Bills"
                getString(R.string.income),  // "Cash Deposit"
                getString(R.string.cosmetics),  // "Cosmetics"
                getString(R.string.entertainment),  // "Entertainment"
                getString(R.string.transportation),  //"Fare"
                getString(R.string.fitness),  //"Fitness"
                getString(R.string.food),  // "Food"
                getString(R.string.health),  // "Health"
                getString(R.string.hygiene),  // "Hygiene"
                getString(R.string.miscellaneous),  // "Miscellaneous"
                getString(R.string.education),  // "School Expenses"
                getString(R.string.shopping),  // "Shopping"
                getString(R.string.utilities)  // "Utilities"
            )


            // responsible for setting up items on the custom dialog
            val dataAdapter = TypeDataAdapter(items, this)
            customDialog = CustomListViewDialog(activity!!, dataAdapter)
            customDialog!!.show()
            customDialog!!.setCanceledOnTouchOutside(true)


        }
    }

    private fun onBookmarkFABClick() {  // gets BookmarkSupplier when Floating Bookmark Button is clicked
        floatingActionButtonBookmark.setOnClickListener {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.bookmark_dialog)

            val bookmarkLayoutManager = LinearLayoutManager(activity!!)
            bookmarkLayoutManager.orientation =  LinearLayoutManager.VERTICAL
            dialog.bookmark_dialog_recycler_view.layoutManager = bookmarkLayoutManager
            val bookmarkAdapter = BookmarksDialogAdapter(activity!!, BookmarkSupplier.bookmarks)
            dialog.bookmark_dialog_recycler_view.adapter = bookmarkAdapter

            dialog.setCancelable(true)
            if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                dialog.bookmark_dialog_bookmark_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))
            }
            dialog.show()
        }
    }

    private fun displayProgressBar() {
        // displays the progress bar's value, asset, expenditures, and balance
        val draw: Drawable?
        var fundValue = 0.0
        var expensesValue = 0.0
        val balanceValue: Double
        var expenses = 0.0
        var balances = 0.0



        for (item in Supplier.expenditures) {
            if (item.type != getString(R.string.income)) {
                expenses += item.price
                expensesValue += item.price
            } else {
                balances += item.price
                fundValue += item.price
            }
        }
        balanceValue = fundValue - expensesValue

        tv_expenses_value.text =  prettyCount(expensesValue)


         tv_fund_value.text =  prettyCount(fundValue)

        if (balanceValue < 0) {
            val balanceStr = "-" + prettyCount(abs(balanceValue))
            tv_balance_value.text = balanceStr
        } else {
            tv_balance_value.text = prettyCount(balanceValue)
        }



        //Log.e(logTag, "$expenses")
        //Log.e(logTag, "$balances")


        // changes color based on balance's value
        progressBar.progress = ((balances - expenses) / balances * 100).toInt()
        if (progressBar.progress >= 50) {
            draw = ContextCompat.getDrawable(
                context!!,
                R.drawable.custom_progress_bar_green
            )
        } else if (progressBar.progress >= 35) {
            draw = ContextCompat.getDrawable(
                context!!,
                R.drawable.custom_progress_bar_yellow
            )
        } else if (progressBar.progress >= 20) {
            draw = ContextCompat.getDrawable(
                context!!,
               R.drawable.custom_progress_bar_orange
            )
        } else if (fundValue == 0.0 && balanceValue == 0.0 && expensesValue == 0.0) {
            draw = ContextCompat.getDrawable(
                context!!,
               R.drawable.custom_progress_bar_light_grey
            )
        } else {
            draw = ContextCompat.getDrawable(
                context!!,
                R.drawable.custom_progress_bar_red
            )
        }

        progressBar.progressDrawable = draw
    }

    private fun setTheme() {  // theme for home fragment
        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            // home background color
            fragment_home_layout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))

            // progress card view
            progressCardView.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            tv_fund.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_fund_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_expenses.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_expenses_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_balance.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_balance_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        }
    }

    private fun setDeleteDateSelection() {  // deletes item automatically based on automaticallyDeleteItem's option
        val todayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val thisMonthDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        val thisWeekDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
        val thisTwoWeeksDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
        val thisYearDateFormat = SimpleDateFormat("yyyy", Locale.US)
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

    private fun setUpBookmarkItems() {  // gets bookmarkItems and adds it in supplier
        BookmarkSupplier.bookmarks.clear()
        val allBookmarkItems = bookmarkItemsRealm.where(ItemModel::class.java).findAll()
        allBookmarkItems.forEach { thisBookmarkItem ->
            BookmarkSupplier.bookmarks.add(0, Bookmark(thisBookmarkItem.itemTitle!!, thisBookmarkItem.itemValue!!, thisBookmarkItem.itemId!!, thisBookmarkItem.itemType!!))
        }
    }


    private fun doShowcase() {  // tutorial showcase on home fragment
        when(DataClass.tutorialHome!!.itemValue) {
            0.0 -> {
                showcaseBuilder = GuideView.Builder(activity)
                    .setTitle("Add Item")
                    .setContentText("Click this button to add an item.")
                    .setGravity(Gravity.auto)
                    .setDismissType(DismissType.targetView)
                    .setTargetView(floatingActionButton)
                    .setGuideListener(object: GuideListener {
                        override fun onDismiss(view:View) {
                            when (view.id) {
                                R.id.floatingActionButton -> return
                            }
                        }
                    })
                showcaseGuide = showcaseBuilder.build()
                showcaseGuide.show()
            }

            0.1 -> {
                showcaseBuilder = GuideView.Builder(activity)
                    .setTitle("Progress Bar")
                    .setContentText("You can track your asset, expenditure, and balance through this progress bar.")
                    .setGravity(Gravity.auto)
                    .setDismissType(DismissType.anywhere)
                    .setTargetView(progressCardView)
                    .setGuideListener(object: GuideListener {
                        override fun onDismiss(view:View) {
                            when (view.id) {
                                R.id.progressCardView -> showcaseBuilder.setTargetView(fragment_home_view_one)
                                    .setTitle("Item Info")
                                    .setDismissType(DismissType.anywhere)
                                    .setContentText("Your items will be displayed here. \n Pressing it shows the item's info;" +
                                            "\n Long Pressing it allows you to view, edit, or remove the item.")
                                    .build()
                                    .show()
                                R.id.fragment_home_view_one -> {
                                    DataClass.tutorialRealm.beginTransaction()
                                    DataClass.tutorialHome!!.itemValue = 0.2
                                    DataClass.tutorialRealm.commitTransaction()
                                    return

                                }
                            }
                        }
                    })
                showcaseGuide = showcaseBuilder.build()
                showcaseGuide.show()
            }

            0.3 -> {
                showcaseBuilder = GuideView.Builder(activity)
                    .setTitle("Nice!")
                    .setContentText("It's faster to add items now ^~^")
                    .setGravity(Gravity.auto)
                    .setDismissType(DismissType.anywhere)
                    .setTargetView(floatingActionButtonBookmark)
                    .setGuideListener(object: GuideListener {
                        override fun onDismiss(view:View) {
                            when (view.id) {
                                R.id.floatingActionButtonBookmark->
                                {
                                    DataClass.tutorialRealm.beginTransaction()
                                    DataClass.tutorialHome!!.itemValue = 0.4
                                    DataClass.tutorialRealm.commitTransaction()
                                    return
                                }
                            }
                        }
                    })
                showcaseGuide = showcaseBuilder.build()
                showcaseGuide.show()
            }
        }

        if (DataClass.tutorialSettings!!.itemValue == 0.3) {
            showcaseBuilder = GuideView.Builder(activity)
                .setTitle("Add Bookmark Item")
                .setContentText("Try how adding an item works using the Add Bookmark Button.")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.targetView)
                .setTargetView(floatingActionButtonBookmark)
                .setGuideListener(object: GuideListener {
                    override fun onDismiss(view:View) {
                        when (view.id) {
                            R.id.floatingActionButtonBookmark->
                            {
                                DataClass.tutorialRealm.beginTransaction()
                                DataClass.tutorialSettings!!.itemValue = 0.4
                                DataClass.tutorialRealm.commitTransaction()
                                return
                            }
                        }
                    }
                })
            showcaseGuide = showcaseBuilder.build()
            showcaseGuide.show()
        }
    }
}


