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
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*
import kotlinx.android.synthetic.main.test.*
import java.text.SimpleDateFormat
import com.squareup.timessquare.CalendarPickerView




// all about statistics fragment
class StatisticsFragment : Fragment() {
    val TAG = "Statistics Fragment"
    var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    val config = RealmConfiguration.Builder().name("items.realm").build()
    val realm = Realm.getInstance(config)

    override fun onAttach(context: Context) {
        Log.d(TAG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "On Create View")
        return inflater!!.inflate(com.example.menu.R.layout.test, container,false)  // inflate the fragment_trash on xml in this trash_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setCalendar()
        clickCalendarDate()
        super.onViewCreated(view, savedInstanceState)

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

    fun setCalendar() {
        var today = Date()
        var minMonth = Calendar.getInstance()
        minMonth.add(Calendar.MONTH, -3)
        var maxMonth = Calendar.getInstance()
        maxMonth.add(Calendar.MONTH, 3)


        testCalendar.init(minMonth.time, maxMonth.time).withSelectedDate(today)

    }

    private fun showHomeFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = HomeFragment()
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

                val selectedDate = ("" + calSelected.get(Calendar.DAY_OF_MONTH)
                        + " " + (calSelected.get(Calendar.MONTH) + 1)
                        + " " + calSelected.get(Calendar.YEAR))

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