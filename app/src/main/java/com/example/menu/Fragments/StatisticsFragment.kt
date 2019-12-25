package com.example.menu.Fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*
import kotlinx.android.synthetic.main.test.*
import java.text.SimpleDateFormat
import com.squareup.timessquare.CalendarPickerView
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


// all about statistics fragment
class StatisticsFragment : Fragment() {
    val TAG = "Statistics Fragment"
    val config = RealmConfiguration.Builder().name("items.realm").build()
    val realm = Realm.getInstance(config)
    var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    override fun onAttach(context: Context) {
        Log.d(TAG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        super.onCreate(savedInstanceState)
        Realm.init(activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "On Create View")
        return inflater!!.inflate(
            com.example.menu.R.layout.fragment_statistics,
            container,
            false
        )  // inflate the fragment_trash on xml in this trash_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOptionChoices()
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

    private fun setOptionChoices() {
        var options = arrayOf("Today", "This Three Days", "This Week", "This Month", "This Three Months", "This Six Months", "This Year")

        spinner_options_statistics.adapter =
            ArrayAdapter<String>(context!!, R.layout.spinner_options_item_properties, options)

        spinner_options_statistics.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setUpPieChart(options.get(position))
                }
            }
    }

    private fun setUpPieChart(optionSelected: String) {
        statistics_pie_chart.setUsePercentValues(true)
        statistics_pie_chart.description.isEnabled = false
        statistics_pie_chart.setExtraOffsets(5f, 5f, 5f, 0f)

        statistics_pie_chart.setDragDecelerationFrictionCoef(0.99f)

        statistics_pie_chart.setDrawHoleEnabled(true)
        statistics_pie_chart.setHoleColor(Color.parseColor("#58D68D"))
        statistics_pie_chart.setTransparentCircleRadius(61f)

        var yValues = ArrayList<PieEntry>()


        // initialize values on expenditure (used as variable to combine values based on itemType)
        var foodValue: Float = 0.toFloat()
        var fareValue: Float = 0.toFloat()
        var beveragesValue: Float = 0.toFloat()
        var healthValue: Float = 0.toFloat()
        var entertainmentValue: Float = 0.toFloat()
        var schoolExpensesValue: Float = 0.toFloat()
        var miscellaneousValue: Float = 0.toFloat()

        val allItems = realm.where(ItemModel::class.java).findAll()

        fun setWhenITemType(thisItem: ItemModel) {
            when (thisItem.itemType) {
                "Food" -> foodValue = foodValue + thisItem.itemValue!!.toFloat()
                "Fare" -> fareValue = fareValue + thisItem.itemValue!!.toFloat()
                "Beverages" -> beveragesValue =
                    beveragesValue + thisItem.itemValue!!.toFloat()
                "Health" -> healthValue =
                    healthValue + thisItem.itemValue!!.toFloat()
                "Entertainment" -> entertainmentValue =
                    entertainmentValue + thisItem.itemValue!!.toFloat()
                "School Expenses" -> schoolExpensesValue =
                    schoolExpensesValue + thisItem.itemValue!!.toFloat()
                "Miscellaneous" -> miscellaneousValue =
                    miscellaneousValue + thisItem.itemValue!!.toFloat()
            }
            var message =
                thisItem.itemTitle + " : " + thisItem.itemType + " : " + dateFormat.format(thisItem.itemDate!!.time)
            Log.e(TAG, message)
        }  // made it into a function to save lines of code  // when(thisItem.itemType!!)

        when (optionSelected) {
            "Today" -> {
                allItems.forEach { thisItem ->
                    if (dateFormat.format(Date()) == dateFormat.format(thisItem.itemDate!!.time)) {
                        setWhenITemType(thisItem)
                    }
                }
            }
            "This Month" -> {
                val thisMonthDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
                allItems.forEach { thisItem ->
                    if (thisMonthDateFormat.format(Calendar.getInstance().time) == thisMonthDateFormat.format(thisItem.itemDate!!.time)) {
                        setWhenITemType(thisItem)
                    }
                }
            }
            "This Year" -> {
                val thisMonthDateFormat = SimpleDateFormat("yyyy", Locale.US)
                allItems.forEach { thisItem ->
                    if (thisMonthDateFormat.format(Date()) == thisMonthDateFormat.format(thisItem.itemDate!!.time)) {
                        setWhenITemType(thisItem)
                    }
                }
            }
            "This Week" -> {
                val thisWeekDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
                allItems.forEach { thisItem ->
                    if (thisWeekDateFormat.format(Date()) == thisWeekDateFormat.format(thisItem.itemDate!!.time)) {
                        setWhenITemType(thisItem)
                    }
                }
            }
            "This Three Days" -> {
                // just initialization (will be used in if statement)
                val thisThreeDaysDateFormat = SimpleDateFormat("D", Locale.US)
                val yesterdayCalendar = Calendar.getInstance()
                val dayBeforeYesterdayCalendar = Calendar.getInstance()

                yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
                dayBeforeYesterdayCalendar.add(Calendar.DAY_OF_YEAR, -2)

                val yesterdayDate = yesterdayCalendar.time
                val dayBeforeYesterdayDate = dayBeforeYesterdayCalendar.time

                allItems.forEach { thisItem ->
                    if ((thisThreeDaysDateFormat.format(Date()) == thisThreeDaysDateFormat.format(thisItem.itemDate!!.time))
                        || (thisThreeDaysDateFormat.format(yesterdayDate!!.time) == thisThreeDaysDateFormat.format(thisItem.itemDate!!.time)) ||
                        (thisThreeDaysDateFormat.format(dayBeforeYesterdayDate!!.time) == thisThreeDaysDateFormat.format(thisItem.itemDate!!.time))) {
                        setWhenITemType(thisItem)
                    }
                }
            }
            "This Three Months" -> {
                // just initialization (will be used in if statement)
                val thisThreeMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
                val onePreviousMonthCalendar = Calendar.getInstance()
                val twoPreviousMonthCalendar = Calendar.getInstance()

                onePreviousMonthCalendar.add(Calendar.MONTH, -1)
                twoPreviousMonthCalendar.add(Calendar.MONTH, -2)

                val onePreviousMonthDate = onePreviousMonthCalendar.time
                val twoPreviousMonthDate = twoPreviousMonthCalendar.time

                allItems.forEach { thisItem ->
                    if ((thisThreeMonthsDateFormat.format(Date()) == thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time))
                        || (thisThreeMonthsDateFormat.format(onePreviousMonthDate!!.time) == thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time))
                        || (thisThreeMonthsDateFormat.format(twoPreviousMonthDate!!.time) == thisThreeMonthsDateFormat.format(thisItem.itemDate!!.time))) {
                        setWhenITemType(thisItem)
                    }
                }
            }
            "This Six Months" -> {
                // just initialization (will be used in if statement)
                val thisSixMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
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

                allItems.forEach { thisItem ->
                    if ((thisSixMonthsDateFormat.format(Date()) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))
                        || (thisSixMonthsDateFormat.format(onePreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))
                        || (thisSixMonthsDateFormat.format(twoPreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))
                        || (thisSixMonthsDateFormat.format(threePreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))
                        || (thisSixMonthsDateFormat.format(fourPreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))
                        || (thisSixMonthsDateFormat.format(fivePreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(thisItem.itemDate!!.time))) {
                        setWhenITemType(thisItem)
                    }
                }
            }
        }

        // adds a Pie Entry if any of these value is greater than 0
        if (fareValue > 0f) yValues.add(PieEntry(fareValue, "Fare"))
        if (foodValue > 0f) yValues.add(PieEntry(foodValue, "Food"))
        if (beveragesValue > 0f) yValues.add(PieEntry(beveragesValue, "Beverages"))
        if (healthValue > 0f) yValues.add(PieEntry(healthValue, "Health"))
        if (entertainmentValue > 0f) yValues.add(PieEntry(entertainmentValue, "Entertainment"))
        if (schoolExpensesValue > 0f) yValues.add(PieEntry(schoolExpensesValue, "School Expenses"))
        if (miscellaneousValue > 0f) yValues.add(PieEntry(miscellaneousValue, "Miscellaneous"))


        var dataSet = PieDataSet(yValues, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 10f

        var colors = ArrayList<Int>()
        var colorsList = ArrayList<Int>()

        colorsList.add(Color.parseColor("#E74C3C")) // red
        colorsList.add(Color.parseColor("#F39C12")) // orange
        colorsList.add(Color.parseColor("#F1C40F")) // yellow
        colorsList.add(Color.parseColor("#3498DB")) // blue
        colorsList.add(Color.parseColor("#34495E")) // dark blue
        colorsList.add(Color.parseColor("#7F8C8D")) // grey
        colorsList.add(Color.parseColor("#9B59B6")) // purple

        for (color in colorsList)
            colors.add(color)


        dataSet.setColors(colors)

        var data = PieData((dataSet))
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.WHITE)


        statistics_pie_chart.legend.isEnabled = false
        statistics_pie_chart.animateY(1400, Easing.EaseInOutQuad)
        statistics_pie_chart.setData(data)


    }
}