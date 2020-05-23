package com.appsAndDawns.saveUp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.model.Expenditure
import com.appsAndDawns.saveUp.model.Supplier
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import com.squareup.timessquare.CalendarPickerView
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.testCalendar
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener
import java.text.SimpleDateFormat
import java.util.*

// all about calendar fragment
class CalendarFragment : Fragment() {
    val themeConfig: RealmConfiguration? = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm? = Realm.getInstance(themeConfig!!)
    var themeMode = themeRealm!!.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    val config: RealmConfiguration? = RealmConfiguration.Builder().name("items.realm").build()
    val realm: Realm? = Realm.getInstance(config!!)
    //private val logTag = "Calendar Fragment"

    private lateinit var showcaseGuide: GuideView
    private lateinit var showcaseBuilder: GuideView.Builder

    /*
    override fun onAttach(context: Context) {
        Log.d(logTag, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Create")
        super.onCreate(savedInstanceState)
        Realm.init(activity!!)
    }

     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Log.d(logTag, "On Create View")
        return if (themeMode!!.itemType == getString(R.string.dark_mode)) {
             inflater.inflate(R.layout.fragment_dark_calendar, container,false)  // inflate the fragment_calendar on xml in this calendar_fragment
        } else {
             inflater.inflate(R.layout.fragment_calendar, container,false)  // inflate the fragment_calendar on xml in this calendar_fragment
        }
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Activity Created")
        super.onActivityCreated(savedInstanceState)
    }

     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setCalendar()
        clickCalendarDate()
        doShowcase()
        super.onViewCreated(view, savedInstanceState)

    }

    /*
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

    private fun setCalendar() {  // set range of calendar from last year to next year
        val today = Date()
        val nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 1)
        val previousYear = Calendar.getInstance()
        previousYear.add(Calendar.YEAR, -1)
        testCalendar.init(previousYear.time, nextYear.time).withSelectedDate(today)
    }

    private fun showHomeFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = HomeFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }


    private fun clickCalendarDate() {  // supplies the supplier of items within the picked calendar date
        testCalendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date: Date) {

                val calSelected = Calendar.getInstance()
                calSelected.time = date

                val selectedDate = dateFormat.format(calSelected.time)

                Toast.makeText(context, selectedDate, Toast.LENGTH_SHORT).show()


                val allItems = realm!!.where(ItemModel::class.java).findAll()
                Supplier.expenditures.clear()

                allItems.forEach { thisItem ->
                    val thisItemDateString = dateFormat.format(thisItem.itemDate!!.time)
                    if (dateFormat.format(calSelected.time) == thisItemDateString) {
                        Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                    }
                }

                showHomeFragment()
                activity!!.title = getString(R.string.home)
                Toast.makeText(context!!, dateFormat.format(calSelected.time), Toast.LENGTH_SHORT).show()
            }

            override fun onDateUnselected(date: Date) {
            }
        })
    }

    private fun doShowcase() {  // shows tutorial on calendar
        if (DataClass.tutorialCalendar!!.itemValue == 0.0) {
            showcaseBuilder = GuideView.Builder(activity)
                .setTitle("Calendar")
                .setContentText("Picking a day on the calendar will \n" +
                        "bring you to the items within that date.")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(view_calendar)
                .setGuideListener(object : GuideListener {
                    override fun onDismiss(view: View) {
                        when (view.id) {
                            R.id.view_calendar ->  {
                                DataClass.tutorialRealm.beginTransaction()
                                DataClass.tutorialCalendar!!.itemValue = 0.1
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