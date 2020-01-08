package com.example.menu.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.menu.Data.DataClass
import com.example.menu.MainActivity
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import com.squareup.timessquare.CalendarPickerView
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.testCalendar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.test.*
import java.text.SimpleDateFormat
import java.util.*

// all about calendar fragment
class CalendarFragment : Fragment() {
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    val config = RealmConfiguration.Builder().name("items.realm").build()
    val realm = Realm.getInstance(config)
    val TAG = "Calendar Fragment"
    override fun onAttach(context: Context) {
        Log.d(TAG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        super.onCreate(savedInstanceState)
        Realm.init(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "On Create View")
        if (themeMode!!.itemType == "Dark Mode") {
            return inflater!!.inflate(R.layout.fragment_dark_calendar, container,false)  // inflate the fragment_calendar on xml in this calendar_fragment
        } else {
            return inflater!!.inflate(R.layout.fragment_calendar, container,false)  // inflate the fragment_calendar on xml in this calendar_fragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Activity Created")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setCalendar()
        clickCalendarDate()
        super.onViewCreated(view, savedInstanceState)

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

    fun setCalendar() {
        var today = Date()
        var nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 1)
        var previousYear = Calendar.getInstance()
        previousYear.add(Calendar.YEAR, -1)

        testCalendar.init(previousYear.time, nextYear.time).withSelectedDate(today)
    }

    private fun showHomeFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = HomeFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(com.example.menu.R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }


    fun clickCalendarDate() {
        testCalendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date: Date) {
                //String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);

                val calSelected = Calendar.getInstance()
                calSelected.time = date

                val selectedDate = dateFormat.format(calSelected.time)

                Toast.makeText(context, selectedDate, Toast.LENGTH_SHORT).show()


                val allItems = realm.where(ItemModel::class.java).findAll()
                Supplier.expenditures.clear()

                allItems.forEach { thisItem ->
                    var thisItemDateString = dateFormat.format(thisItem.itemDate!!.time)
                    if (dateFormat.format(calSelected.time) == thisItemDateString) {
                        Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                    }
                }

                showHomeFragment()
                activity!!.setTitle("Home")
                Toast.makeText(context!!, dateFormat.format(calSelected.time), Toast.LENGTH_SHORT).show()
            }

            override fun onDateUnselected(date: Date) {
            }
        })
    }





}