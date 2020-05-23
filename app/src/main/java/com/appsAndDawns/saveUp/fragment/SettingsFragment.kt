package com.appsAndDawns.saveUp.fragment

import android.app.*
import android.content.Intent
import android.os.*
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsAndDawns.saveUp.adapter.*
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.dialog.BookmarkFillUpDialog
import com.appsAndDawns.saveUp.dialog.CustomListViewDialog
import com.appsAndDawns.saveUp.MainActivity
import com.appsAndDawns.saveUp.model.*
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.confirmation_dialog.*
import kotlinx.android.synthetic.main.fragment_settings.*
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment(), TypeDataAdapter.RecyclerViewItemClickListener {
    private var customDialog: CustomListViewDialog? = null  // this is the Choose Type dialog
    private var bookmarkCustomFillUpDialog: BookmarkFillUpDialog? = null  // this is the Fill Up dialog
    //private val logTag = "Settings Fragment"
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    lateinit var builder : Notification.Builder
    private var itemConfig: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    private val itemRealm: Realm = Realm.getInstance(itemConfig)
    private var removedItemConfig: RealmConfiguration = RealmConfiguration.Builder().name("removedItems.realm").build()
    private var removedItemRealm: Realm = Realm.getInstance(removedItemConfig)
    private var deleteDateSelectionConfig: RealmConfiguration = RealmConfiguration.Builder().name("deleteDateSelection.realm").build()
    private val deleteDateSelectionRealm: Realm = Realm.getInstance(deleteDateSelectionConfig)
    private var deleteDateSelection = deleteDateSelectionRealm.where(ItemModel::class.java).equalTo("itemId", "deleteDateSelection").findFirst()
    private var bookmarkItemsConfig: RealmConfiguration = RealmConfiguration.Builder().name("bookmarkItemsRealm.realm").build()
    private val bookmarkItemsRealm: Realm = Realm.getInstance(bookmarkItemsConfig)
    private var includeItemsConfig: RealmConfiguration = RealmConfiguration.Builder().name("includeItems.realm").build()
    private val includeItemsRealm: Realm = Realm.getInstance(includeItemsConfig)
    private var includeItems = includeItemsRealm.where(ItemModel::class.java).equalTo("itemId", "includeItems").findFirst()
    private lateinit var showcaseGuide:GuideView
    lateinit var showcaseBuilder: GuideView.Builder

    /*
    override fun onAttach(context: Context) {
        Log.d(logTag, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Create")
        Realm.init(activity!!)
        super.onCreate(savedInstanceState)
    }

     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Log.d(logTag, "On Create View")
        return inflater.inflate(R.layout.fragment_settings, container,false)  // inflate the fragment_trash on xml in this trash_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTheme()
        setCardViewActions()
        setUpRecyclerView()
        setDeleteDateSelection()
        setImageViewActions()
        setUpBookmarkItems()
        setExcludeBalance()
        doShowcase()
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

    override fun clickOnItem(data: String) {  // shows the choose type dialog to bookmark fill up dialog
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
            bookmarkCustomFillUpDialog = BookmarkFillUpDialog(activity!!)
            bookmarkCustomFillUpDialog!!.show()
        }

    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        settings_selection_recycler_view.layoutManager = layoutManager

        val adapter = SettingsSelectionAdapter(activity!!, SelectionSupplier.settingsAutomaticallyDeleteSelectionModels)
        settings_selection_recycler_view.adapter = adapter

        val bookmarkLayoutManager = LinearLayoutManager(activity)
        settings_bookmark_items_recycler_view.layoutManager = bookmarkLayoutManager
        val bookmarkAdapter = BookmarksAdapter(activity!!, BookmarkSupplier.bookmarks)
        settings_bookmark_items_recycler_view.adapter = bookmarkAdapter

        val includeItemsLayoutManager = LinearLayoutManager(activity)
        settings_include_items_recycler_view.layoutManager = includeItemsLayoutManager
        val includeItemsAdapter = IncludeItemsAdapter(activity!!, IncludeItemsSelectionSupplier.includeItemsModels)
        settings_include_items_recycler_view.adapter = includeItemsAdapter
    }

    private fun setTheme() {

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            switch_dark_mode_settings_fragment.isChecked = true


            scroll_view_settings_fragment.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))

            // card view dark mode
            card_view_dark_mode_settings_fragment.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!, R.color.settings_card_view_dark_mode_state))
            relative_layout_settings_fragment.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))
            view_dark_mode_settings_fragment.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            tv_dark_mode_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            img_dark_mode_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))


            // card view delete all data
            card_view_delete_all_data_settings_fragment.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!, R.color.settings_card_view_dark_mode_state))
            view_delete_all_data_settings_fragment.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            img_delete_all_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
            tv_delete_all_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))


            // card view automatically delete data
            card_view_automatically_delete_data_settings_fragment.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!, R.color.settings_card_view_dark_mode_state))
            view_automatically_delete_data_settings_fragment.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
            tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))


            // card view bookmark
            card_view_bookmark_settings_fragment.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!, R.color.settings_card_view_dark_mode_state))
            view_bookmark_settings_fragment.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
            img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
            tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            //card_view_add_bookmark_settings_fragment.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!, R.color.green_button_state))


            // card view exclude balance
            card_view_include_items_settings_fragment.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!, R.color.settings_card_view_dark_mode_state))
            view_include_items_settings_fragment.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            img_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
            tv_title_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            tv_value_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            img_drop_down_up_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))

        } else {
            switch_dark_mode_settings_fragment.isChecked = false
        }



        // dark mode switch button
        switch_dark_mode_settings_fragment.setOnCheckedChangeListener { _, _ ->
            if (switch_dark_mode_settings_fragment.isChecked) {
                themeRealm.beginTransaction()
                themeMode!!.itemType = getString(R.string.dark_mode)
                themeMode!!.itemTitle = getString(R.string.dark_mode)
                themeRealm.commitTransaction()
                restartApp()
            } else {
                themeRealm.beginTransaction()
                themeMode!!.itemType = getString(R.string.light_mode)
                themeMode!!.itemTitle = getString(R.string.light_mode)
                themeRealm.commitTransaction()
                restartApp()
            }
        }

    }

    private fun restartApp() {  // refreshes Main Activity
        val intent =
            Intent(activity!!, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity!!.startActivity(intent)
    }

    private fun setCardViewActions() {  // card view actions


        // dark mode card view onTouchListener
        card_view_dark_mode_settings_fragment.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            val action: Int = motionEvent.action
            if (action == MotionEvent.ACTION_DOWN) {
                img_dark_mode_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.green))
                tv_dark_mode_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.green))
            } else if (action == MotionEvent.ACTION_UP) {
                if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                    tv_dark_mode_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    img_dark_mode_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                } else {
                    img_dark_mode_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_dark_mode_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                }

            }
            false
        }

        // delete all data card view onTouchListener
        card_view_delete_all_data_settings_fragment.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            val action: Int = motionEvent.action
            if (action == MotionEvent.ACTION_DOWN) {
                img_delete_all_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))
                tv_delete_all_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
            } else if (action == MotionEvent.ACTION_UP) {
                if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                    tv_delete_all_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    img_delete_all_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                } else {
                    img_delete_all_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_delete_all_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                }

            }
            false
        }


        // delete all data card view onClickListener
        card_view_delete_all_data_settings_fragment.setOnClickListener {
            val dialog = Dialog(activity!!)
            val confirmationStr = "Are you sure you want to delete all the data permanently?"
            dialog.setContentView(R.layout.confirmation_dialog)
            dialog.setCancelable(true)
            dialog.show()


            dialog.tv_confirmation_text_confirmation_dialog.text = confirmationStr

            if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                dialog.tv_confirmation_confirmation_dialog.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                dialog.relative_layout_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                dialog.tv_confirmation_text_confirmation_dialog.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                dialog.tv_confirmation_text_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                dialog.tv_confirmation_no_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                dialog.tv_confirmation_yes_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            }

            dialog.tv_confirmation_no_confirmation_dialog.setOnClickListener {
                dialog.cancel()
            }

            dialog.tv_confirmation_yes_confirmation_dialog.setOnClickListener {
                val allItems = itemRealm.where(ItemModel::class.java).findAll()
                allItems.forEach { item ->
                    itemRealm.beginTransaction()
                    item.deleteFromRealm()
                    itemRealm.commitTransaction()
                }

                val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
                allRemovedItems.forEach { removedItem ->
                    removedItemRealm.beginTransaction()
                    removedItem.deleteFromRealm()
                    removedItemRealm.commitTransaction()
                }

                val allBookmarkItems = bookmarkItemsRealm.where(ItemModel::class.java).findAll()
                allBookmarkItems.forEach { bookmarkItem ->
                    bookmarkItemsRealm.beginTransaction()
                    bookmarkItem.deleteFromRealm()
                    bookmarkItemsRealm.commitTransaction()
                }

                val theme = themeRealm.where(ItemModel::class.java).findFirst()
                themeRealm.beginTransaction()
                theme!!.itemType = getString(R.string.light_mode)
                theme.itemTitle = getString(R.string.light_mode)
                themeRealm.commitTransaction()

                val deleteDateSelection = deleteDateSelectionRealm.where(ItemModel::class.java).findFirst()
                deleteDateSelectionRealm.beginTransaction()
                deleteDateSelection!!.itemType = "never"
                deleteDateSelection.itemTitle = "never"
                deleteDateSelectionRealm.commitTransaction()

                val includeItems =  includeItemsRealm.where(ItemModel::class.java).findFirst()
                includeItemsRealm.beginTransaction()
                includeItems!!.itemType = "include all items"
                includeItems.itemTitle = "include all items"
                includeItemsRealm.commitTransaction()

                Toast.makeText(activity!!, "All Data Deleted", Toast.LENGTH_SHORT).show()
                dialog.cancel()
                restartApp()
            }


        }


        // automatically delete data card view onTouchListener
        card_view_automatically_delete_data_settings_fragment.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            val action: Int = motionEvent.action
            if (action == MotionEvent.ACTION_DOWN) {
                img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))
                tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
                tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
                img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))
            } else if (action == MotionEvent.ACTION_UP) {
                if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                    img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))

                } else {
                    img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                }
            }
            false
        }


        // automatically delete data card view onClickListener
        card_view_automatically_delete_data_settings_fragment.setOnClickListener {
            if (settings_selection_recycler_view.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(settings_selection_recycler_view, AutoTransition())
                settings_selection_recycler_view.visibility = View.VISIBLE
                img_drop_down_up_automatically_delete_data_settings_fragment.setImageResource(R.drawable.ic_arrow_drop_up)

                img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))
                tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
                tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
                img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))

            } else {
                TransitionManager.beginDelayedTransition(settings_selection_recycler_view, AutoTransition())
                settings_selection_recycler_view.visibility = View.GONE
                img_drop_down_up_automatically_delete_data_settings_fragment.setImageResource(R.drawable.ic_arrow_drop_down)

                if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                    img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))

                } else {
                    img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                }
            }
        }


        // bookmark card view onTouchListener
        card_view_bookmark_settings_fragment.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            val action: Int = motionEvent.action
                    if (action == MotionEvent.ACTION_DOWN) {
                        img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.yellow))
                        tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.yellow))
                        img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.yellow))
                    } else if (action == MotionEvent.ACTION_UP) {
                        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                            img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                            tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                            img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                        } else {
                            img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                            tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                            img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                        }
                    }
            false
        }


        // bookmark card view onClickListener
        card_view_bookmark_settings_fragment.setOnClickListener {
            if (settings_bookmark_items_recycler_view.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(settings_bookmark_items_recycler_view, AutoTransition())
                settings_bookmark_items_recycler_view.visibility = View.VISIBLE
                img_bookmark_drop_down_settings_fragment.setImageResource(R.drawable.ic_arrow_drop_up)
                TransitionManager.beginDelayedTransition(card_view_add_bookmark_settings_fragment, AutoTransition())
                card_view_add_bookmark_settings_fragment.visibility = View.VISIBLE


                img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.yellow))
                tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.yellow))
                img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.yellow))

            } else {
                settings_bookmark_items_recycler_view.visibility = View.GONE
                TransitionManager.beginDelayedTransition(settings_bookmark_items_recycler_view, AutoTransition())
                img_bookmark_drop_down_settings_fragment.setImageResource(R.drawable.ic_arrow_drop_down)
                card_view_add_bookmark_settings_fragment.visibility = View.GONE
                TransitionManager.beginDelayedTransition(card_view_add_bookmark_settings_fragment, AutoTransition())

                if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                    img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                } else {
                    img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                }
            }
        }

        // add bookmark card view onClickListener
        card_view_add_bookmark_settings_fragment.setOnClickListener {
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
            val dataAdapter = TypeDataAdapter(items, this@SettingsFragment)
            customDialog = CustomListViewDialog(activity!!, dataAdapter)
            customDialog!!.show()
            customDialog!!.setCanceledOnTouchOutside(true)
        }


        // include items card view onTouchListener
        card_view_include_items_settings_fragment.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            val action: Int = motionEvent.action
            if (action == MotionEvent.ACTION_DOWN) {
                img_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.blue))
                tv_title_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
                tv_value_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
                img_drop_down_up_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.blue))
            } else if (action == MotionEvent.ACTION_UP) {
                if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                    img_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    tv_title_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    tv_value_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    img_drop_down_up_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))

                } else {
                    img_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_title_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_value_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    img_drop_down_up_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                }
            }
            false
        }


        // include items card view onClickListener
        card_view_include_items_settings_fragment.setOnClickListener {
            if (settings_include_items_recycler_view.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(settings_include_items_recycler_view, AutoTransition())
                settings_include_items_recycler_view.visibility = View.VISIBLE
                img_drop_down_up_include_items_settings_fragment.setImageResource(R.drawable.ic_arrow_drop_up)

                img_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.blue))
                tv_title_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
                tv_value_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
                img_drop_down_up_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.blue))

            } else {
                TransitionManager.beginDelayedTransition(settings_include_items_recycler_view, AutoTransition())
                settings_include_items_recycler_view.visibility = View.GONE
                img_drop_down_up_include_items_settings_fragment.setImageResource(R.drawable.ic_arrow_drop_down)

                if (themeMode!!.itemType == getString(R.string.dark_mode)) {
                    img_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    tv_title_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    tv_value_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    img_drop_down_up_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))

                } else {
                    img_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_title_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    tv_value_include_items_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    img_drop_down_up_include_items_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                }
            }
        }

    }

    private fun setImageViewActions() {  // none
    }

    private fun setDeleteDateSelection() {   // automatically deletes data, same as in home fragment
        tv_value_automatically_delete_data_settings_fragment.text = deleteDateSelection!!.itemTitle
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
                // new year new me, beach
            }

        }
    }

    private fun setExcludeBalance() {
        tv_value_include_items_settings_fragment.text = includeItems!!.itemTitle
    }

    private fun setUpBookmarkItems() {
        BookmarkSupplier.bookmarks.clear()
        val allBookmarkItems = bookmarkItemsRealm.where(ItemModel::class.java).findAll()
        allBookmarkItems.forEach { thisBookmarkItem ->
            BookmarkSupplier.bookmarks.add(0, Bookmark(thisBookmarkItem.itemTitle!!, thisBookmarkItem.itemValue!!, thisBookmarkItem.itemId!!, thisBookmarkItem.itemType!!))
        }
    }


    private fun doShowcase() {   // tutorial on Settings
        if (DataClass.tutorialSettings!!.itemValue == 0.0) {
            showcaseBuilder = GuideView.Builder(activity)
                .setTitle(getString(R.string.dark_mode))
                .setContentText("Switches the theme to dark mode.")
                .setGravity(Gravity.center)
                .setDismissType(DismissType.anywhere)
                .setTargetView(card_view_dark_mode_settings_fragment)
                .setGuideListener(object: GuideListener {
                    override fun onDismiss(view:View) {
                        when (view.id) {
                            R.id.card_view_dark_mode_settings_fragment -> {
                                showcaseBuilder.setTargetView(card_view_include_items_settings_fragment)
                                    .setTitle("Include Items")
                                    .setContentText("Shows item within the chosen date.")
                                    .build()
                                    .show()
                            }
                            R.id.card_view_include_items_settings_fragment -> {
                                showcaseBuilder.setTargetView(card_view_automatically_delete_data_settings_fragment)
                                    .setTitle("Automatically Delete Data")
                                    .setContentText("Automatically delete items over the chosen date.")
                                    .build()
                                    .show()
                            }
                            R.id.card_view_automatically_delete_data_settings_fragment -> {
                                showcaseBuilder.setTargetView(card_view_delete_all_data_settings_fragment)
                                    .setTitle("Delete All Data")
                                    .setContentText("Deletes all data including items and options set.")
                                    .build()
                                    .show()
                            }
                            R.id.card_view_delete_all_data_settings_fragment -> {
                                DataClass.tutorialRealm.beginTransaction()
                                DataClass.tutorialSettings!!.itemValue = 0.1
                                DataClass.tutorialRealm.commitTransaction()
                                showcaseBuilder.setTargetView(card_view_bookmark_settings_fragment)
                                    .setTitle("Bookmark")
                                    .setContentText("Add frequently used items by using Bookmark \n" +
                                            "(click Bookmark to add a bookmark)")
                                    .setDismissType(DismissType.targetView)
                                    .build()
                                    .show()
                            }
                            R.id.card_view_bookmark_settings_fragment -> {
                                showcaseBuilder.setTargetView(card_view_add_bookmark_settings_fragment)
                                    .setTitle("Add Bookmark")
                                    .setContentText("Try adding a bookmark.")
                                    .setDismissType(DismissType.targetView)
                                    .build()
                                    .show()
                            }
                        }

                    }
                })
            showcaseGuide = showcaseBuilder.build()
            showcaseGuide.show()
        } else if (DataClass.tutorialSettings!!.itemValue == 0.2) {
            showcaseBuilder = GuideView.Builder(activity)
                .setTitle("Bookmark")
                .setContentText("Bookmark items can also be viewed by normal press \n" +
                        "and can be viewed, edited, or deleted by long press.")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(card_view_bookmark_settings_fragment)
                .setGuideListener(object : GuideListener {
                    override fun onDismiss(view: View) {
                        when (view.id) {
                            R.id.card_view_bookmark_settings_fragment ->  {
                                DataClass.tutorialRealm.beginTransaction()
                                DataClass.tutorialSettings!!.itemValue = 0.3
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
