package com.example.menu.Fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.felix.horizontalbargraph.model.BarItem
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*
import kotlinx.android.synthetic.main.test.*
import java.text.SimpleDateFormat
import com.squareup.timessquare.CalendarPickerView
import kotlinx.android.synthetic.main.fill_up_dialog_layout.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.spinner_options_item_properties.*
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
    var savedDate: Date? = null
    var horizontalItems = ArrayList<BarItem>()
    var count = 0
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

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
        pickADate()
        setOptionChoices()
        setTheme()
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
        var spinner: Int = R.layout.spinner_options_item_properties
        var options = arrayOf(
            "Today",
            "This Three Days",
            "This Week",
            "This Month",
            "This Three Months",
            "This Six Months",
            "This Year"
        )

        if (themeMode!!.itemType == "Dark Mode") {
            spinner = R.layout.dark_spinner_options_item_properties
        }

        spinner_options_statistics.adapter =
            ArrayAdapter<String>(context!!, spinner, options)

        spinner_options_statistics.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    setUpPieChart(options.get(position))
                    setMonetaryDistribution(options.get(position))
                    setUpLineChart(options.get(position))
                    setUpBarChart(options.get(position))
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

        }  // made it into a function to save lines of code  // when(thisItem.itemType!!)

        setOptionsSelected(optionSelected, ::setWhenITemType)


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
        colorsList.add(Color.parseColor("#34495E")) // darkest blue
        colorsList.add(Color.parseColor("#7F8C8D")) // darker grey
        colorsList.add(Color.parseColor("#9B59B6")) // purple

        for (color in colorsList)
            colors.add(color)


        dataSet.setColors(colors)

        var data = PieData((dataSet))
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.WHITE)


        statistics_pie_chart.legend.isEnabled = false
        statistics_pie_chart.animateY(1000, Easing.EaseInOutQuad)
        statistics_pie_chart.setData(data)


    }

    private fun pickADate() {
        var theme: Int = 0

        if (themeMode!!.itemType == "Dark Mode") {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_DARK
        } else {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
        }


        val pickADateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        buttonPickADate.setOnClickListener {
            val selectedDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                context!!, theme,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    savedDate = selectedDate.time

                    setUpPieChart(pickADateFormat.format(savedDate!!.time))
                    setMonetaryDistribution(pickADateFormat.format(savedDate!!.time))
                    setUpLineChart(pickADateFormat.format(savedDate!!.time))
                    setUpBarChart(pickADateFormat.format(savedDate!!.time))

                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)

            )
            datePicker.show()
        }
    }

    private fun setMonetaryDistribution(optionSelected: String) {

        var expenses: Double = 0.0
        var balances: Double = 0.0
        var healthValue: Double = 0.0
        var beveragesValue: Double = 0.0
        var foodValue: Double = 0.0
        var fareValue: Double = 0.0
        var schoolExpensesValue: Double = 0.0
        var entertainmentValue: Double = 0.0
        var miscellaneousValue: Double = 0.0

        val allItems = realm.where(ItemModel::class.java).findAll()
        fun setWhenItemType(thisItem: ItemModel) {
            when (thisItem.itemType) {
                "Cash Deposit" -> balances = balances + thisItem.itemValue!!
                else -> expenses = expenses + thisItem.itemValue!!
            }
            when (thisItem.itemType) {
                "Health" -> healthValue = healthValue + thisItem.itemValue!!
                "Beverages" -> beveragesValue = beveragesValue + thisItem.itemValue!!
                "Food" -> foodValue = foodValue + thisItem.itemValue!!
                "Fare" -> fareValue = fareValue + thisItem.itemValue!!
                "School Expenses" -> schoolExpensesValue =
                    schoolExpensesValue + thisItem.itemValue!!
                "Entertainment" -> entertainmentValue = entertainmentValue + thisItem.itemValue!!
                "Miscellaneous" -> miscellaneousValue = miscellaneousValue + thisItem.itemValue!!
            }
        }

        fun setProgressValue() {
            // setting values on each progress bar and on type values
            statistics_progress_cash_deposit.progress =
                ((balances - expenses) / balances * 100).toInt()  // cash deposit
            statistics_cash_deposit_value.text = balances.toString()

            statistics_progress_health.progress = (healthValue / balances * 100).toInt()  // health
            statistics_health_value.text = healthValue.toString()

            statistics_progress_beverages.progress =
                (beveragesValue / balances * 100).toInt()  // beverages
            statistics_beverages_value.text = beveragesValue.toString()

            statistics_progress_food.progress = (foodValue / balances * 100).toInt()  // food
            statistics_food_value.text = foodValue.toString()

            statistics_progress_fare.progress = (fareValue / balances * 100).toInt()  // fare
            statistics_fare_value.text = fareValue.toString()

            statistics_progress_school_expenses.progress =
                (schoolExpensesValue / balances * 100).toInt()  // school expenses
            statistics_school_expenses_value.text = schoolExpensesValue.toString()

            statistics_progress_entertainment.progress =
                (entertainmentValue / balances * 100).toInt()  // entertainment
            statistics_entertainment_value.text = entertainmentValue.toString()

            statistics_progress_miscellaneous.progress =
                (miscellaneousValue / balances * 100).toInt()  // miscellaneous
            statistics_miscellaneous_value.text = miscellaneousValue.toString()

        }

      setOptionsSelected(optionSelected, ::setWhenItemType)
      setProgressValue()

      if (themeMode!!.itemType == "Dark Mode") {
          monetary_distribution_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_cash_deposit_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_cash_deposit_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_health_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_health_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_beverages_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_beverages_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_food_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_food_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_fare_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_fare_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_school_expenses_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_school_expenses_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_entertainment_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_entertainment_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

          statistics_miscellaneous_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
          statistics_miscellaneous_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
      }

    }

    private fun setUpLineChart(optionSelected: String) {
        val timeFormat = SimpleDateFormat("hh a", Locale.US)
        fun setAmLineChart() {
            var twelveAM: Float = 0f
            var twoAM: Float = 0f
            var fourAM: Float = 0f
            var sixAM: Float = 0f
            var eightAM: Float = 0f
            var tenAM: Float = 0f
            var twelvePM: Float = 0f

            fun setWhenItemType(thisItem: ItemModel) {
                if (thisItem.itemType != "Cash Deposit") {
                    when (timeFormat.format(thisItem.itemTime!!.time)) {
                        "12 AM" -> twelveAM = twelveAM + thisItem.itemValue!!.toFloat()
                        "01 AM" -> twoAM = twoAM + thisItem.itemValue!!.toFloat()
                        "02 AM" -> twoAM = twoAM + thisItem.itemValue!!.toFloat()
                        "03 AM" -> fourAM = fourAM + thisItem.itemValue!!.toFloat()
                        "04 AM" -> fourAM = fourAM + thisItem.itemValue!!.toFloat()
                        "05 AM" -> sixAM = sixAM + thisItem.itemValue!!.toFloat()
                        "06 AM" -> sixAM = sixAM + thisItem.itemValue!!.toFloat()
                        "07 AM" -> eightAM = eightAM + thisItem.itemValue!!.toFloat()
                        "08 AM" -> eightAM = eightAM + thisItem.itemValue!!.toFloat()
                        "09 AM" -> tenAM = tenAM + thisItem.itemValue!!.toFloat()
                        "10 AM" -> tenAM = tenAM + thisItem.itemValue!!.toFloat()
                        "11 AM" -> twelvePM = twelvePM + thisItem.itemValue!!.toFloat()
                    }
                }

            }

            setOptionsSelected(optionSelected, ::setWhenItemType)

            var amXValues = ArrayList<String>()
            amXValues.add("12 am")
            amXValues.add("2 am")
            amXValues.add("4 am")
            amXValues.add("6 am")
            amXValues.add("8 am")
            amXValues.add("10 am")
            amXValues.add("11 am")

            var amEntries = ArrayList<Entry>()
            amEntries.add(Entry(0.toFloat(), twelveAM))
            amEntries.add(Entry(1.toFloat(), twoAM))
            amEntries.add(Entry(2.toFloat(), fourAM))
            amEntries.add(Entry(3.toFloat(), sixAM))
            amEntries.add(Entry(4.toFloat(), eightAM))
            amEntries.add(Entry(5.toFloat(), tenAM))
            amEntries.add(Entry(6.toFloat(), twelvePM))

            var dataSet = LineDataSet(amEntries, "")
            dataSet.setColor(ContextCompat.getColor(context!!, R.color.white))
            dataSet.fillColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.fillAlpha = 100
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircleHole(true)
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.isHighlightEnabled = false
            dataSet.lineWidth = 2f


            var amXAxis: XAxis = statistics_am_line_chart.xAxis
            amXAxis.position = XAxis.XAxisPosition.BOTTOM


            amXAxis.granularity = 1f
            amXAxis.valueFormatter = MyValueFormatter(amXValues)


            var amYAxisRight: YAxis = statistics_am_line_chart.axisRight
            amYAxisRight.isEnabled = false

            var amYAxisLeft: YAxis = statistics_am_line_chart.axisLeft
            amYAxisLeft.granularity = 1f

            var data = LineData(dataSet)
            statistics_am_line_chart.data = data
            statistics_am_line_chart.animateX(400)
            statistics_am_line_chart.invalidate()
            statistics_am_line_chart.xAxis.setDrawGridLines(false)
            statistics_am_line_chart.axisLeft.setDrawGridLines(false)
            statistics_am_line_chart.description.isEnabled = false
            statistics_am_line_chart.legend.isEnabled = false
            statistics_am_line_chart.xAxis.textColor = Color.WHITE
            statistics_am_line_chart.axisLeft.textColor = Color.WHITE

        }

        fun setPmLineChart() {
            var pmXValues = ArrayList<String>()
            pmXValues.add("12 pm")
            pmXValues.add("2 pm")
            pmXValues.add("4 pm")
            pmXValues.add("6 pm")
            pmXValues.add("8 pm")
            pmXValues.add("10 pm")
            pmXValues.add("11 pm")

            var twelvePM: Float = 0f
            var twoPM: Float = 0f
            var fourPM: Float = 0f
            var sixPM: Float = 0f
            var eightPM: Float = 0f
            var tenPM: Float = 0f
            var twelveAM: Float = 0f

            fun setWhenItemType(thisItem: ItemModel) {
                if (thisItem.itemType!! != "Cash Deposit") {
                    when (timeFormat.format(thisItem.itemTime!!.time)) {
                        "12 PM" -> twelvePM = twelvePM + thisItem.itemValue!!.toFloat()
                        "01 PM" -> twoPM = twoPM + thisItem.itemValue!!.toFloat()
                        "02 PM" -> twoPM = twoPM + thisItem.itemValue!!.toFloat()
                        "03 PM" -> fourPM = fourPM + thisItem.itemValue!!.toFloat()
                        "04 PM" -> fourPM = fourPM + thisItem.itemValue!!.toFloat()
                        "05 PM" -> sixPM = sixPM + thisItem.itemValue!!.toFloat()
                        "06 PM" -> sixPM = sixPM + thisItem.itemValue!!.toFloat()
                        "07 PM" -> eightPM = eightPM + thisItem.itemValue!!.toFloat()
                        "08 PM" -> eightPM = eightPM + thisItem.itemValue!!.toFloat()
                        "09 PM" -> tenPM = tenPM + thisItem.itemValue!!.toFloat()
                        "10 PM" -> tenPM = tenPM + thisItem.itemValue!!.toFloat()
                        "11 PM" -> twelveAM = twelveAM + thisItem.itemValue!!.toFloat()
                    }
                }

            }

            setOptionsSelected(optionSelected, ::setWhenItemType)

            var pmEntries = ArrayList<Entry>()
            pmEntries.add(Entry(0.toFloat(), twelvePM))
            pmEntries.add(Entry(1.toFloat(), twoPM))
            pmEntries.add(Entry(2.toFloat(), fourPM))
            pmEntries.add(Entry(3.toFloat(), sixPM))
            pmEntries.add(Entry(4.toFloat(), eightPM))
            pmEntries.add(Entry(5.toFloat(), tenPM))
            pmEntries.add(Entry(6.toFloat(), twelveAM))

            var dataSet = LineDataSet(pmEntries, "")
            dataSet.setColor(ContextCompat.getColor(context!!, R.color.white))
            dataSet.fillColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.fillAlpha = 100
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircleHole(true)
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.isHighlightEnabled = false
            dataSet.lineWidth = 2f


            var pmXAxis: XAxis = statistics_pm_line_chart.xAxis
            pmXAxis.position = XAxis.XAxisPosition.BOTTOM


            pmXAxis.granularity = 1f
            pmXAxis.valueFormatter = MyValueFormatter(pmXValues)


            var pmYAxisRight: YAxis = statistics_pm_line_chart.axisRight
            pmYAxisRight.isEnabled = false

            var pmYAxisLeft: YAxis = statistics_pm_line_chart.axisLeft
            pmYAxisLeft.granularity = 1f

            var data = LineData(dataSet)
            statistics_pm_line_chart.data = data
            statistics_pm_line_chart.animateX(400)
            statistics_pm_line_chart.invalidate()
            statistics_pm_line_chart.xAxis.setDrawGridLines(false)
            statistics_pm_line_chart.axisLeft.setDrawGridLines(false)
            statistics_pm_line_chart.description.isEnabled = false
            statistics_pm_line_chart.legend.isEnabled = false
            statistics_pm_line_chart.xAxis.textColor = Color.WHITE
            statistics_pm_line_chart.axisLeft.textColor = Color.WHITE

        }

        setAmLineChart()
        setPmLineChart()

        setChartAnimation()

    }

    private fun setUpBarChart(optionSelected: String) {

        var asset: Float = 0f
        var expenditures: Float = 0f
        var balance: Float = 0f

        fun setWhenItemType(thisItem: ItemModel) {
            when (thisItem.itemType!!) {
                "Cash Deposit" -> asset = asset + thisItem.itemValue!!.toFloat()
                else -> expenditures = expenditures + thisItem.itemValue!!.toFloat()
            }
        }


        setOptionsSelected(optionSelected, ::setWhenItemType)


        balance = asset - expenditures
        var colorGreenRed = ContextCompat.getColor(context!!, R.color.green)
        if (balance < 0f) {
            colorGreenRed = ContextCompat.getColor(context!!, R.color.red)
        }

        var xValues = arrayListOf<String>("Asset", "Expenditures", "Balance")
        var entries =
            mutableListOf<BarEntry>(BarEntry(0f, asset), BarEntry(1f, expenditures), BarEntry(2f, balance))
        var colors = mutableListOf<Int>(
            ContextCompat.getColor(context!!, R.color.blue),
            ContextCompat.getColor(context!!, R.color.yellow),
            colorGreenRed
        )
        var dataSet: BarDataSet = BarDataSet(entries, "")
        dataSet.setColors(colors)

        dataSet.valueTextSize = 12f
        dataSet.isHighlightEnabled = false

        var xAxis: XAxis = statistics_monetary_status_bar_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.granularity = 1f
        xAxis.valueFormatter = MyValueFormatter(xValues)


        var yAxisRight: YAxis = statistics_monetary_status_bar_chart.axisRight
        yAxisRight.isEnabled = false

        var yAxisLeft: YAxis = statistics_monetary_status_bar_chart.axisLeft
        yAxisLeft.granularity = 1f

        var data = BarData(dataSet)
        statistics_monetary_status_bar_chart.data = data
        statistics_monetary_status_bar_chart.invalidate()
        statistics_monetary_status_bar_chart.legend.isEnabled = false
        statistics_monetary_status_bar_chart.description.isEnabled = false
        statistics_monetary_status_bar_chart.xAxis.setDrawGridLines(false)
        statistics_monetary_status_bar_chart.axisLeft.setDrawGridLines(false)


        setChartAnimation()

        if (themeMode!!.itemType == "Dark Mode") {
            monetary_status_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.white)
            statistics_monetary_status_bar_chart.xAxis.textColor = ContextCompat.getColor(context!!, R.color.white)
            statistics_monetary_status_bar_chart.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.white)
        } else {
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.grey)
            statistics_monetary_status_bar_chart.xAxis.textColor = ContextCompat.getColor(context!!, R.color.grey)
            statistics_monetary_status_bar_chart.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.grey)
        }


    }

    private fun setChartAnimation() {
        statistics_scroll_view.getViewTreeObserver()
            .addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
                override fun onScrollChanged() {
                    statistics_scroll_view?.let {
                        if (statistics_scroll_view.scrollY != null && statistics_scroll_view.height != null) {
                            if (statistics_scroll_view.scrollY + statistics_scroll_view.height >= 3200 && statistics_scroll_view.scrollY + statistics_scroll_view.height <= 3450) {
                                //scroll view is not at bottom
                                //statistics_am_line_chart.animateX(500)
                                //statistics_pm_line_chart.animateX(500)
                                statistics_am_line_chart.animateXY(450, 900,Easing.EaseInOutQuart)
                                statistics_pm_line_chart.animateXY(450, 900, Easing.EaseInOutQuart)
                            }

                            if (statistics_scroll_view.scrollY == statistics_scroll_view.getChildAt(0).measuredHeight - statistics_scroll_view.measuredHeight) {
                                Log.e(TAG, "End of Scroll View")
                                statistics_monetary_status_bar_chart.animateY(800)
                            }

                            if (statistics_scroll_view.scrollY == statistics_scroll_view.top) {
                                statistics_pie_chart.animateY(1000, Easing.EaseInOutQuad)
                            }
                        }
                    }

                }
            })
    }

    private fun setOptionsSelected(optionSelected: String, setWhenItemType : (thisItem: ItemModel) -> Unit) {
        val allItems = realm.where(ItemModel::class.java).findAll()
        val todayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val thisMonthDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        val thisWeekDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
        val thisYearDateFormat = SimpleDateFormat("yyyy", Locale.US)
        val pickADateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val thisSixMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        val thisThreeDaysDateFormat = SimpleDateFormat("D", Locale.US)
        val thisThreeMonthsDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        when (optionSelected) {
            "Today" -> {
                allItems.forEach { thisItem ->
                    if (todayDateFormat.format(Date()) == todayDateFormat.format(thisItem.itemDate!!.time)) {
                        setWhenItemType(thisItem)
                    }
                }
            }
            "This Month" -> {
                allItems.forEach { thisItem ->
                    if (thisMonthDateFormat.format(Calendar.getInstance().time) == thisMonthDateFormat.format(
                            thisItem.itemDate!!.time
                        )
                    ) {
                        setWhenItemType(thisItem)
                    }
                }
            }
            "This Year" -> {
                allItems.forEach { thisItem ->
                    if (thisYearDateFormat.format(Date()) == thisYearDateFormat.format(thisItem.itemDate!!.time)) {
                        setWhenItemType(thisItem)
                    }
                }
            }
            "This Week" -> {
                allItems.forEach { thisItem ->
                    if (thisWeekDateFormat.format(Date()) == thisWeekDateFormat.format(thisItem.itemDate!!.time)) {
                        setWhenItemType(thisItem)
                    }
                }
            }
            "This Three Days" -> {
                // just initialization (will be used in if statement)
                val yesterdayCalendar = Calendar.getInstance()
                val dayBeforeYesterdayCalendar = Calendar.getInstance()

                yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
                dayBeforeYesterdayCalendar.add(Calendar.DAY_OF_YEAR, -2)

                val yesterdayDate = yesterdayCalendar.time
                val dayBeforeYesterdayDate = dayBeforeYesterdayCalendar.time

                allItems.forEach { thisItem ->
                    if ((thisThreeDaysDateFormat.format(Date()) == thisThreeDaysDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisThreeDaysDateFormat.format(yesterdayDate!!.time) == thisThreeDaysDateFormat.format(
                            thisItem.itemDate!!.time
                        )) ||
                        (thisThreeDaysDateFormat.format(dayBeforeYesterdayDate!!.time) == thisThreeDaysDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                    ) {
                        setWhenItemType(thisItem)
                    }
                }
            }
            "This Three Months" -> {
                // just initialization (will be used in if statement)
                val onePreviousMonthCalendar = Calendar.getInstance()
                val twoPreviousMonthCalendar = Calendar.getInstance()

                onePreviousMonthCalendar.add(Calendar.MONTH, -1)
                twoPreviousMonthCalendar.add(Calendar.MONTH, -2)

                val onePreviousMonthDate = onePreviousMonthCalendar.time
                val twoPreviousMonthDate = twoPreviousMonthCalendar.time

                allItems.forEach { thisItem ->
                    if ((thisThreeMonthsDateFormat.format(Date()) == thisThreeMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisThreeMonthsDateFormat.format(onePreviousMonthDate!!.time) == thisThreeMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisThreeMonthsDateFormat.format(twoPreviousMonthDate!!.time) == thisThreeMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                    ) {
                        setWhenItemType(thisItem)
                    }
                }
            }
            "This Six Months" -> {
                // just initialization (will be used in if statement)
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
                    if ((thisSixMonthsDateFormat.format(Date()) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(onePreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(twoPreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(threePreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(fourPreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(fivePreviousMonthDate!!.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                    ) {
                        setWhenItemType(thisItem)
                    }
                }
            }
            else -> {
                allItems.forEach { thisItem ->
                    if (optionSelected == pickADateFormat.format(thisItem!!.itemDate!!.time)) {
                        setWhenItemType(thisItem)
                    }
                }
            }
        }
    }

    private fun setTheme() {
        if (themeMode!!.itemType == "Dark Mode") {
            statistics_scroll_view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))
            statistics_relative_layout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))
        }

    }
}

class MyValueFormatter(private val xValsLabel: ArrayList<String>) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        if (value.toInt() >= 0 && value.toInt() <= xValsLabel.size - 1) {
            return xValsLabel[value.toInt()]
        } else {
            return ("").toString()
        }
    }
}

