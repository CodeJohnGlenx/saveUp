package com.example.menu.Fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
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
import com.example.menu.MainActivity
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.confirmation_dialog.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
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
    }

}
