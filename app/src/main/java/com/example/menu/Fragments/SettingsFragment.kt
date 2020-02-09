package com.example.menu.Fragments

import android.app.*
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.*
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RemoteViews
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.menu.Adapter.BookmarksAdapter
import com.example.menu.Adapter.SettingsSelectionAdapter
import com.example.menu.Adapter.TypeDataAdapter
import com.example.menu.Data.DataClass
import com.example.menu.Dialogs.BookmarkFillUpDialog
import com.example.menu.Dialogs.CustomListViewDialog
import com.example.menu.Dialogs.FillUpDialog
import com.example.menu.MainActivity
import com.example.menu.Model.BookmarkSupplier
import com.example.menu.Model.SelectionSupplier
import com.example.menu.Model.SettingsAutomaticallyDeleteSelectionModel
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.confirmation_dialog.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment(), TypeDataAdapter.RecyclerViewItemClickListener {
    var customDialog: CustomListViewDialog? = null  // this is the Choose Type dialog
    var bookmarkCustomFillUpDialog: BookmarkFillUpDialog? = null  // this is the Fill Up dialog
    val TAG = "Settings Fragment"
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = " com.example.menu.Fragments"
    private val description = "Student Budgeting App"
    val itemConfig = RealmConfiguration.Builder().name("items.realm").build()
    val itemRealm = Realm.getInstance(itemConfig)
    var removedItemConfig = RealmConfiguration.Builder().name("removedItems.realm").build()
    var removedItemRealm = Realm.getInstance(removedItemConfig)
    val deleteDateSelectionConfig = RealmConfiguration.Builder().name("deleteDateSelection.realm").build()
    val deleteDateSelectionRealm = Realm.getInstance(deleteDateSelectionConfig)
    var deleteDateSelection = deleteDateSelectionRealm.where(ItemModel::class.java).equalTo("itemId", "deleteDateSelection").findFirst()


    override fun onAttach(context: Context) {
        Log.d(TAG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        Realm.init(activity!!)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "On Create View")
        return inflater!!.inflate(R.layout.fragment_settings, container,false)  // inflate the fragment_trash on xml in this trash_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTheme()
        setCardViewActions()
        setUpRecyclerView()
        setDeleteDateSelection()
        setImageViewActions()
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

    override fun clickOnItem(data: String) {
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
    }

    private fun setTheme() {

        if (themeMode!!.itemType == "Dark Mode") {
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

        } else {
            switch_dark_mode_settings_fragment.isChecked = false
        }




       switch_dark_mode_settings_fragment.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
               override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                   if (switch_dark_mode_settings_fragment.isChecked) {
                       themeRealm.beginTransaction()
                       themeMode!!.itemType = "Dark Mode"
                       themeMode!!.itemTitle = "Dark Mode"
                       themeRealm.commitTransaction()
                       restartApp()
                   } else {
                       themeRealm.beginTransaction()
                       themeMode!!.itemType = "Light Mode"
                       themeMode!!.itemTitle = "Light Mode"
                       themeRealm.commitTransaction()
                       restartApp()
                   }

               }
        })

    }

    private fun showSettingsFragment() {
        val transaction = (activity!! as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun restartApp() {  // refreshes Main Activity
        val intent =
            Intent(activity!!, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity!!.startActivity(intent)
    }

    /*
    private fun showNotifications() {
       notificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        btn_notify.setOnClickListener {

            builder = Notification.Builder(activity)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Student Budgeting App")
                .setContentText("Have you updated your expenditure?")

            var contentIntent = PendingIntent.getActivity(activity!!, 0, Intent(activity!!, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

            builder.setContentIntent(contentIntent)

            notificationManager.notify(1234, builder.build())
        }
    }

     */

    /*
    private fun getNotification(content: String) : Notification {
        var builder = Notification.Builder(activity)
        builder.setContentTitle("Student Budgeting App")
        builder.setContentText("Have you track your expenses?")
        builder.setSmallIcon(R.drawable.ic_notifications)

        var contentIntent = PendingIntent.getActivity(activity!!, 0, Intent(activity!!, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)
        return builder.build()
    }

    private fun scheduleNotification(notification : Notification, time : Int) {
        var notificationIntent = Intent(activity, NotificationClass::class.java)
        notificationIntent.putExtra(NOTIFICATION_ID, 1)
        notificationIntent.putExtra(NOTIFICATION, notification)
        var pendingIntent = PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var futureMillis = SystemClock.elapsedRealtime() + time
        var alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, futureMillis, pendingIntent)
    }

    private fun showNotifications() {
        btn_notify.setOnClickListener {
            scheduleNotification(getNotification("3 second delay"), 3000)
        }
    }

     */

    private fun setCardViewActions() {
        card_view_dark_mode_settings_fragment.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, motionEvent: MotionEvent?): Boolean {
                var action: Int = motionEvent!!.action
                if (action == MotionEvent.ACTION_DOWN) {
                    img_dark_mode_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.green))
                    tv_dark_mode_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.green))
                } else if (action == MotionEvent.ACTION_UP) {
                    if (themeMode!!.itemType == "Dark Mode") {
                        tv_dark_mode_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                        img_dark_mode_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    } else {
                        img_dark_mode_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                        tv_dark_mode_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    }

                }
                return false
            }
        })

        card_view_delete_all_data_settings_fragment.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, motionEvent: MotionEvent?): Boolean {
                var action: Int = motionEvent!!.action
                if (action == MotionEvent.ACTION_DOWN) {
                    img_delete_all_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))
                    tv_delete_all_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
                } else if (action == MotionEvent.ACTION_UP) {
                    if (themeMode!!.itemType == "Dark Mode") {
                        tv_delete_all_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                        img_delete_all_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    } else {
                        img_delete_all_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                        tv_delete_all_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    }

                }
                return false
            }
        })
        card_view_delete_all_data_settings_fragment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                var dialog = Dialog(activity!!)
                dialog.setContentView(R.layout.confirmation_dialog)
                dialog.setCancelable(true)
                dialog.show()

                dialog.tv_confirmation_text_confirmation_dialog.text = "Are you sure you want to delete all the data permanently?"

                if (themeMode!!.itemType == "Dark Mode") {
                    dialog.tv_confirmation_confirmation_dialog.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    dialog.relative_layout_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                    dialog.tv_confirmation_text_confirmation_dialog.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    dialog.tv_confirmation_text_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                    dialog.tv_confirmation_no_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                    dialog.tv_confirmation_yes_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                }

                dialog.tv_confirmation_no_confirmation_dialog.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        dialog.cancel()
                    }
                })

                dialog.tv_confirmation_yes_confirmation_dialog.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
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

                        Toast.makeText(activity!!, "All Data Deleted", Toast.LENGTH_SHORT).show()
                        dialog.cancel()
                    }
                })
            }
        })

        card_view_automatically_delete_data_settings_fragment.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                var action: Int = motionEvent!!.action
                if (action == MotionEvent.ACTION_DOWN) {
                    img_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))
                    tv_title_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
                    tv_value_automatically_delete_data_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.red))
                    img_drop_down_up_automatically_delete_data_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.red))
                } else if (action == MotionEvent.ACTION_UP) {
                    if (themeMode!!.itemType == "Dark Mode") {
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
                return false
            }
        })
        card_view_automatically_delete_data_settings_fragment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
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

                    if (themeMode!!.itemType == "Dark Mode") {
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
        })

        card_view_bookmark_settings_fragment.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                var action: Int = motionEvent!!.action
                if (action == MotionEvent.ACTION_DOWN) {
                    img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.yellow))
                    tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.yellow))
                    img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.yellow))
                } else if (action == MotionEvent.ACTION_UP) {
                    if (themeMode!!.itemType == "Dark Mode") {
                        img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                        tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                        img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.white))
                    } else {
                        img_bookmark_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                        tv_title_bookmark_settings_fragment.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_grey))
                        img_bookmark_drop_down_settings_fragment.setColorFilter(ContextCompat.getColor(activity!!, R.color.dark_grey))
                    }
                }
                return false
            }
        })
        card_view_bookmark_settings_fragment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
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

                    if (themeMode!!.itemType == "Dark Mode") {
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
        })

        card_view_add_bookmark_settings_fragment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
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
                val dataAdapter = TypeDataAdapter(items, this@SettingsFragment)
                customDialog = CustomListViewDialog(activity!!, dataAdapter)
                customDialog!!.show()
                customDialog!!.setCanceledOnTouchOutside(true)
            }
        })

    }

    private fun setImageViewActions() {
    }

    private fun setDeleteDateSelection() {
        tv_value_automatically_delete_data_settings_fragment.text = deleteDateSelection!!.itemTitle
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
