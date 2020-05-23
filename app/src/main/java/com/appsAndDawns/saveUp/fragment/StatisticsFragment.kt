package com.appsAndDawns.saveUp.fragment

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.fragment_statistics.*
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener
import kotlin.collections.ArrayList


// all about statistics fragment
class StatisticsFragment : Fragment() {
    //val logTag = "Statistics Fragment"
    val config: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    val realm: Realm = Realm.getInstance(config)
    private var savedDate: Date? = null
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode =
        themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()


    private lateinit var showcaseGuide: GuideView
    lateinit var showcaseBuilder: GuideView.Builder


    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Create")
        super.onCreate(savedInstanceState)
        Realm.init(activity!!)
    }

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Log.d(logTag, "On Create View")
        return inflater.inflate(
            R.layout.fragment_statistics,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pickADate()
        setOptionChoices()
        setTheme()
        doShowcase()
        super.onViewCreated(view, savedInstanceState)
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

    private fun setOptionChoices() {  // option choices for spinner that will invoke all methods
        var spinner: Int = R.layout.spinner_options_item_properties
        val options = arrayOf(
            "Today",
            "This Three Days",
            "This Week",
            "This Two Weeks",
            "This Month",
            "This Three Months",
            "This Six Months",
            "This Year",
            "All Time"
        )

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            spinner = R.layout.dark_spinner_options_item_properties
        }

        spinner_options_statistics.adapter =
            ArrayAdapter(context!!, spinner, options)

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
                    // all methods that was being pointed
                    setUpPieChart(options[position])
                    setMonetaryDistribution(options[position])
                    setUpLineChart(options[position])
                    setUpBarChart(options[position])
                }
            }


    }

    // Pie chart
    private fun setUpPieChart(optionSelected: String) {
        statistics_pie_chart.setUsePercentValues(true)
        statistics_pie_chart.description.isEnabled = false
        statistics_pie_chart.setExtraOffsets(5f, 5f, 5f, 0f)

        statistics_pie_chart.dragDecelerationFrictionCoef = 0.99f

        statistics_pie_chart.isDrawHoleEnabled = true

        statistics_pie_chart.setHoleColor(Color.parseColor("#58D68D"))  // pale green
        statistics_pie_chart.transparentCircleRadius = 61f

        val yValues = ArrayList<PieEntry>()


        // initialize values on expenditure (used as variable to combine values based on itemType)
        var beveragesValue: Float = 0.toFloat()
        var billsValue: Float = 0.toFloat()
        var cosmeticsValue: Float = 0.toFloat()
        var entertainmentValue: Float = 0.toFloat()
        var transportationValue: Float = 0.toFloat()
        var fitnessValue: Float = 0.toFloat()
        var foodValue: Float = 0.toFloat()
        var healthValue: Float = 0.toFloat()
        var hygieneValue: Float = 0.toFloat()
        var miscellaneousValue: Float = 0.toFloat()
        var educationValue: Float = 0.toFloat()
        var shoppingValue: Float = 0.toFloat()
        var utilitiesValue: Float = 0.toFloat()

        fun setWhenITemType(thisItem: ItemModel) {
            when (thisItem.itemType) {
                getString(R.string.beverages) -> beveragesValue += thisItem.itemValue!!.toFloat()  // "Beverages"
                getString(R.string.bills) -> billsValue += thisItem.itemValue!!.toFloat()  // "Bills"
                getString(R.string.cosmetics) -> cosmeticsValue += thisItem.itemValue!!.toFloat()  // "Cosmetics"
                getString(R.string.entertainment) -> entertainmentValue += thisItem.itemValue!!.toFloat()  // "Entertainment"
                getString(R.string.transportation) -> transportationValue += thisItem.itemValue!!.toFloat()  // fareValue  // Fare
                getString(R.string.fitness) -> fitnessValue += thisItem.itemValue!!.toFloat()  // "Fitness"
                getString(R.string.food) -> foodValue += thisItem.itemValue!!.toFloat()  // "Food"
                getString(R.string.health) -> healthValue += thisItem.itemValue!!.toFloat()  // "Health"
                getString(R.string.hygiene) -> hygieneValue += thisItem.itemValue!!.toFloat()  // "Hygiene"
                getString(R.string.miscellaneous) -> miscellaneousValue += thisItem.itemValue!!.toFloat()  // "Miscellaneous"
                getString(R.string.education) -> educationValue += thisItem.itemValue!!.toFloat()  //schoolExpensesValue // "School Expenses"
                getString(R.string.shopping) -> shoppingValue += thisItem.itemValue!!.toFloat()  // "Shopping"
                getString(R.string.utilities) -> utilitiesValue += thisItem.itemValue!!.toFloat()  // "Utilities"

            }

        }  // made it into a function to save lines of code  // when(thisItem.itemType!!)

        setOptionsSelected(optionSelected, ::setWhenITemType)


        // adds a Pie Entry if any of these value is greater than 0
        if (beveragesValue > 0f) yValues.add(PieEntry(beveragesValue, getString(R.string.beverages))) // "Beverages"
        if (billsValue > 0f) yValues.add(PieEntry(billsValue, getString(R.string.bills)))  // "Bills"
        if (cosmeticsValue > 0f) yValues.add(PieEntry(cosmeticsValue, getString(R.string.cosmetics)))  // "Cosmetics"
        if (entertainmentValue > 0f) yValues.add(PieEntry(entertainmentValue, getString(R.string.entertainment)))  // "Entertainment"
        if (transportationValue > 0f) yValues.add(PieEntry(transportationValue, getString(R.string.transportation)))  // "Fare"
        if (fitnessValue > 0f) yValues.add(PieEntry(fitnessValue, getString(R.string.fitness)))  // "Fitness"
        if (foodValue > 0f) yValues.add(PieEntry(foodValue, getString(R.string.food)))  // "Food"
        if (healthValue > 0f) yValues.add(PieEntry(healthValue, getString(R.string.health)))  // "Health"
        if (hygieneValue > 0f) yValues.add(PieEntry(hygieneValue, getString(R.string.hygiene)))  // "Hygiene"
        if (miscellaneousValue > 0f) yValues.add(PieEntry(miscellaneousValue, getString(R.string.miscellaneous)))  // "Miscellaneous"
        if (educationValue > 0f) yValues.add(PieEntry(educationValue, getString(R.string.education)))  // "School Expenses"
        if (shoppingValue > 0f) yValues.add(PieEntry(shoppingValue, getString(R.string.shopping)))  // "Shopping"
        if (utilitiesValue > 0f) yValues.add(PieEntry(utilitiesValue, getString(R.string.utilities)))  // "Utilities"


        val dataSet = PieDataSet(yValues, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 10f

        val colors = ArrayList<Int>()
        val colorsList = ArrayList<Int>()

        colorsList.add(Color.parseColor("#E74C3C")) // red
        colorsList.add(Color.parseColor("#F39C12")) // orange
        colorsList.add(Color.parseColor("#F1C40F")) // yellow
        colorsList.add(Color.parseColor("#3498DB")) // blue
        colorsList.add(Color.parseColor("#34495E")) // darkest blue
        colorsList.add(Color.parseColor("#7F8C8D")) // darker grey
        colorsList.add(Color.parseColor("#9B59B6")) // purple

        for (color in colorsList)
            colors.add(color)

        dataSet.colors = colors

        val data = PieData((dataSet))
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.WHITE)


        statistics_pie_chart.legend.isEnabled = false
        statistics_pie_chart.animateY(1000, Easing.EaseInOutQuad)

        statistics_pie_chart.data = data


    }


    // Pick Data for specific date
    private fun pickADate() {
        val theme: Int

        when (themeMode!!.itemType == getString(R.string.dark_mode)) {
            true -> theme = R.style.date_picker_dark_theme
            false -> theme = R.style.date_picker_light_theme
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


    // monetary distribution
    private fun setMonetaryDistribution(optionSelected: String) {

        var expenses = 0.0
        var balances = 0.0
        var beveragesValue = 0.0
        var billsValue = 0.0
        var cosmeticsValue = 0.0
        var entertainmentValue = 0.0
        var transportationValue = 0.0
        var fitnessValue = 0.0
        var foodValue = 0.0
        var healthValue = 0.0
        var hygieneValue = 0.0
        var miscellaneousValue = 0.0
        var educationValue = 0.0
        var shoppingValue = 0.0
        var utilitiesValue = 0.0

        fun setWhenItemType(thisItem: ItemModel) {
            when (thisItem.itemType) {
                getString(R.string.income) -> balances += thisItem.itemValue!!  // "Cash Deposit"
                else -> expenses += thisItem.itemValue!!
            }
            when (thisItem.itemType) {
                getString(R.string.beverages) -> beveragesValue += thisItem.itemValue!!  // "Beverages"
                getString(R.string.bills) -> billsValue += thisItem.itemValue!!  // "Bills"
                getString(R.string.cosmetics) -> cosmeticsValue += thisItem.itemValue!!  // "Cosmetics"
                getString(R.string.entertainment) -> entertainmentValue += thisItem.itemValue!!  // "Entertainment"
                getString(R.string.transportation) -> transportationValue += thisItem.itemValue!!  // "Fare"
                getString(R.string.fitness) -> fitnessValue += thisItem.itemValue!!  // "Fitness"
                getString(R.string.food) -> foodValue = thisItem.itemValue!!  // "Food"
                getString(R.string.health) -> healthValue += thisItem.itemValue!!  // "Health"
                getString(R.string.hygiene) -> hygieneValue += thisItem.itemValue!!  // "Hygiene"
                getString(R.string.miscellaneous) -> miscellaneousValue += thisItem.itemValue!!  // "Miscellaneous"
                getString(R.string.education) -> educationValue += thisItem.itemValue!!  // "School Expenses"
                getString(R.string.shopping) -> shoppingValue += thisItem.itemValue!!  // "Shopping"
                getString(R.string.utilities) -> utilitiesValue += thisItem.itemValue!!  // "Utilities"
            }
        }

        fun setProgressValue() {
            // setting values on each progress bar and on type values
            statistics_progress_cash_deposit.progress =
                ((balances - expenses) / balances * 100).toInt()  // cash deposit
            statistics_cash_deposit_value.text =
                DataClass.prettyCount(balances)  // balances.toString()

            statistics_progress_beverages.progress =
                (beveragesValue / balances * 100).toInt()  // beverages
            statistics_beverages_value.text =
                DataClass.prettyCount(beveragesValue)  // beveragesValue.toString()

            statistics_progress_bills.progress =
                (billsValue / balances * 100).toInt()  // bills
            statistics_bills_value.text =
                DataClass.prettyCount(billsValue)  // billsValue.toString()

            statistics_progress_cosmetics.progress =
                (cosmeticsValue / balances * 100).toInt()  // cosmetics
            statistics_cosmetics_value.text =
                DataClass.prettyCount(cosmeticsValue) // cosmeticsValue.toString()

            statistics_progress_entertainment.progress =
                (entertainmentValue / balances * 100).toInt()  // entertainment
            statistics_entertainment_value.text =
                DataClass.prettyCount(entertainmentValue) // entertainmentValue.toString()

            statistics_progress_fare.progress = (transportationValue / balances * 100).toInt()  // transportation
            statistics_fare_value.text = DataClass.prettyCount(transportationValue) // fareValue.toString()

            statistics_progress_fitness.progress =
                (fitnessValue / balances * 100).toInt()  // fitness
            statistics_fitness_value.text =
                DataClass.prettyCount(fitnessValue)  // fitnessValue.toString()

            statistics_progress_food.progress = (foodValue / balances * 100).toInt()  // food
            statistics_food_value.text = DataClass.prettyCount(foodValue)  // foodValue.toString()

            statistics_progress_health.progress = (healthValue / balances * 100).toInt()  // health
            statistics_health_value.text =
                DataClass.prettyCount(healthValue)  // healthValue.toString()

            statistics_progress_hygiene.progress =
                (hygieneValue / balances * 100).toInt()  // hygiene
            statistics_hygiene_value.text =
                DataClass.prettyCount(hygieneValue)  // hygieneValue.toString()

            statistics_progress_miscellaneous.progress =
                (miscellaneousValue / balances * 100).toInt()  // miscellaneous
            statistics_miscellaneous_value.text =
                DataClass.prettyCount(miscellaneousValue)  // miscellaneousValue.toString()

            statistics_progress_school_expenses.progress =
                (educationValue / balances * 100).toInt()  // school expenses
            statistics_school_expenses_value.text =
                DataClass.prettyCount(educationValue)  // schoolExpensesValue.toString()

            statistics_progress_shopping.progress =
                (shoppingValue / balances * 100).toInt()  // shopping
            statistics_shopping_value.text =
                DataClass.prettyCount(shoppingValue)  // shoppingValue.toString()

            statistics_progress_utilities.progress =
                (utilitiesValue / balances * 100).toInt()  // utilities
            statistics_utilities_value.text =
                DataClass.prettyCount(utilitiesValue)  // utilitiesValue.toString()

        }

        setOptionsSelected(optionSelected, ::setWhenItemType)
        setProgressValue()

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            monetary_distribution_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_cash_deposit_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_cash_deposit_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_beverages_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_beverages_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_bills_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            statistics_bills_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            statistics_cosmetics_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_cosmetics_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_entertainment_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_entertainment_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_fare_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            statistics_fare_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            statistics_fitness_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            statistics_fitness_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            statistics_food_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            statistics_food_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            statistics_health_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            statistics_health_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            statistics_hygiene_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            statistics_hygiene_value.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            statistics_miscellaneous_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_miscellaneous_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_school_expenses_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_school_expenses_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_shopping_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_shopping_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )

            statistics_utilities_title.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
            statistics_utilities_value.setTextColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.white
                )
            )
        }

    }

    // Line Chart (Expenditure Activity)
    private fun setUpLineChart(optionSelected: String) {
        val timeFormat = SimpleDateFormat("hh a", Locale.US)
        fun setAmLineChart() {
            var twelveAM = 0f
            var twoAM = 0f
            var fourAM = 0f
            var sixAM = 0f
            var eightAM = 0f
            var tenAM = 0f
            var twelvePM = 0f

            fun setWhenItemType(thisItem: ItemModel) {
                if (thisItem.itemType != getString(R.string.income)) {
                    when (timeFormat.format(thisItem.itemTime!!.time)) {
                        "12 AM" -> twelveAM += thisItem.itemValue!!.toFloat()
                        "01 AM" -> twoAM += thisItem.itemValue!!.toFloat()
                        "02 AM" -> twoAM += thisItem.itemValue!!.toFloat()
                        "03 AM" -> fourAM += thisItem.itemValue!!.toFloat()
                        "04 AM" -> fourAM += thisItem.itemValue!!.toFloat()
                        "05 AM" -> sixAM += thisItem.itemValue!!.toFloat()
                        "06 AM" -> sixAM += thisItem.itemValue!!.toFloat()
                        "07 AM" -> eightAM += thisItem.itemValue!!.toFloat()
                        "08 AM" -> eightAM += thisItem.itemValue!!.toFloat()
                        "09 AM" -> tenAM += thisItem.itemValue!!.toFloat()
                        "10 AM" -> tenAM += thisItem.itemValue!!.toFloat()
                        "11 AM" -> twelvePM += thisItem.itemValue!!.toFloat()
                    }
                }

            }

            setOptionsSelected(optionSelected, ::setWhenItemType)

            val amXValues = ArrayList<String>()
            amXValues.add("12 am")
            amXValues.add("2 am")
            amXValues.add("4 am")
            amXValues.add("6 am")
            amXValues.add("8 am")
            amXValues.add("10 am")
            amXValues.add("11 am")

            val amEntries = ArrayList<Entry>()
            amEntries.add(Entry(0.toFloat(), twelveAM))
            amEntries.add(Entry(1.toFloat(), twoAM))
            amEntries.add(Entry(2.toFloat(), fourAM))
            amEntries.add(Entry(3.toFloat(), sixAM))
            amEntries.add(Entry(4.toFloat(), eightAM))
            amEntries.add(Entry(5.toFloat(), tenAM))
            amEntries.add(Entry(6.toFloat(), twelvePM))

            val dataSet = LineDataSet(amEntries, "")
            dataSet.color = ContextCompat.getColor(context!!, R.color.white)
            dataSet.fillColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.fillAlpha = 100
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircleHole(true)
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.isHighlightEnabled = false
            dataSet.lineWidth = 2f


            val amXAxis: XAxis = statistics_am_line_chart.xAxis
            amXAxis.position = XAxis.XAxisPosition.BOTTOM


            amXAxis.granularity = 1f
            amXAxis.valueFormatter = MyValueFormatter(amXValues)


            val amYAxisRight: YAxis = statistics_am_line_chart.axisRight
            amYAxisRight.isEnabled = false

            val amYAxisLeft: YAxis = statistics_am_line_chart.axisLeft
            amYAxisLeft.granularity = 1f

            val data = LineData(dataSet)
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
            val pmXValues = ArrayList<String>()
            pmXValues.add("12 pm")
            pmXValues.add("2 pm")
            pmXValues.add("4 pm")
            pmXValues.add("6 pm")
            pmXValues.add("8 pm")
            pmXValues.add("10 pm")
            pmXValues.add("11 pm")

            var twelvePM = 0f
            var twoPM = 0f
            var fourPM = 0f
            var sixPM = 0f
            var eightPM = 0f
            var tenPM = 0f
            var twelveAM = 0f

            fun setWhenItemType(thisItem: ItemModel) {
                if (thisItem.itemType!! != getString(R.string.income)) {
                    when (timeFormat.format(thisItem.itemTime!!.time)) {
                        "12 PM" -> twelvePM += thisItem.itemValue!!.toFloat()
                        "01 PM" -> twoPM += thisItem.itemValue!!.toFloat()
                        "02 PM" -> twoPM += thisItem.itemValue!!.toFloat()
                        "03 PM" -> fourPM += thisItem.itemValue!!.toFloat()
                        "04 PM" -> fourPM += thisItem.itemValue!!.toFloat()
                        "05 PM" -> sixPM += thisItem.itemValue!!.toFloat()
                        "06 PM" -> sixPM += thisItem.itemValue!!.toFloat()
                        "07 PM" -> eightPM += thisItem.itemValue!!.toFloat()
                        "08 PM" -> eightPM += thisItem.itemValue!!.toFloat()
                        "09 PM" -> tenPM += thisItem.itemValue!!.toFloat()
                        "10 PM" -> tenPM += thisItem.itemValue!!.toFloat()
                        "11 PM" -> twelveAM += thisItem.itemValue!!.toFloat()
                    }
                }

            }

            setOptionsSelected(optionSelected, ::setWhenItemType)

            val pmEntries = ArrayList<Entry>()
            pmEntries.add(Entry(0.toFloat(), twelvePM))
            pmEntries.add(Entry(1.toFloat(), twoPM))
            pmEntries.add(Entry(2.toFloat(), fourPM))
            pmEntries.add(Entry(3.toFloat(), sixPM))
            pmEntries.add(Entry(4.toFloat(), eightPM))
            pmEntries.add(Entry(5.toFloat(), tenPM))
            pmEntries.add(Entry(6.toFloat(), twelveAM))

            val dataSet = LineDataSet(pmEntries, "")
            dataSet.color = ContextCompat.getColor(context!!, R.color.white)
            dataSet.fillColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.fillAlpha = 100
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircleHole(true)
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.white)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.isHighlightEnabled = false
            dataSet.lineWidth = 2f


            val pmXAxis: XAxis = statistics_pm_line_chart.xAxis
            pmXAxis.position = XAxis.XAxisPosition.BOTTOM


            pmXAxis.granularity = 1f
            pmXAxis.valueFormatter = MyValueFormatter(pmXValues)


            val pmYAxisRight: YAxis = statistics_pm_line_chart.axisRight
            pmYAxisRight.isEnabled = false

            val pmYAxisLeft: YAxis = statistics_pm_line_chart.axisLeft
            pmYAxisLeft.granularity = 1f

            val data = LineData(dataSet)
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


    // BarChart (Monetary Status)
    private fun setUpBarChart(optionSelected: String) {

        var asset = 0f
        var expenditures = 0f
        val balance: Float

        fun setWhenItemType(thisItem: ItemModel) {
            when (thisItem.itemType!!) {
                getString(R.string.income) -> asset += thisItem.itemValue!!.toFloat()
                else -> expenditures += thisItem.itemValue!!.toFloat()
            }
        }


        setOptionsSelected(optionSelected, ::setWhenItemType)


        balance = asset - expenditures
        var colorGreenRed = ContextCompat.getColor(context!!, R.color.green)
        if (balance < 0f) {
            colorGreenRed = ContextCompat.getColor(context!!, R.color.red)
        }

        val xValues = arrayListOf(context!!.getString(R.string.asset), context!!.getString(R.string.expenses), context!!.getString(R.string.balance))
        val entries =
            mutableListOf(
                BarEntry(0f, asset),
                BarEntry(1f, expenditures),
                BarEntry(2f, balance)
            )
        val colors = mutableListOf(
            ContextCompat.getColor(context!!, R.color.blue),
            ContextCompat.getColor(context!!, R.color.yellow),
            colorGreenRed
        )
        val dataSet = BarDataSet(entries, "")
        dataSet.colors = colors

        dataSet.valueTextSize = 12f
        dataSet.isHighlightEnabled = false

        val xAxis: XAxis = statistics_monetary_status_bar_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.granularity = 1f
        xAxis.valueFormatter = MyValueFormatter(xValues)


        val yAxisRight: YAxis = statistics_monetary_status_bar_chart.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft: YAxis = statistics_monetary_status_bar_chart.axisLeft
        yAxisLeft.granularity = 1f

        val data = BarData(dataSet)
        statistics_monetary_status_bar_chart.data = data
        statistics_monetary_status_bar_chart.invalidate()
        statistics_monetary_status_bar_chart.legend.isEnabled = false
        statistics_monetary_status_bar_chart.description.isEnabled = false
        statistics_monetary_status_bar_chart.xAxis.setDrawGridLines(false)
        statistics_monetary_status_bar_chart.axisLeft.setDrawGridLines(false)


        setChartAnimation()

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            monetary_status_title.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.white)
            statistics_monetary_status_bar_chart.xAxis.textColor =
                ContextCompat.getColor(context!!, R.color.white)
            statistics_monetary_status_bar_chart.axisLeft.textColor =
                ContextCompat.getColor(context!!, R.color.white)
        } else {
            dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.dark_grey)
            statistics_monetary_status_bar_chart.xAxis.textColor =
                ContextCompat.getColor(context!!, R.color.dark_grey)
            statistics_monetary_status_bar_chart.axisLeft.textColor =
                ContextCompat.getColor(context!!, R.color.dark_grey)
        }


    }


    // Animation in Statistics
    private fun setChartAnimation() {
        statistics_scroll_view.getViewTreeObserver()
            .addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
                override fun onScrollChanged() {
                    statistics_scroll_view?.let {
                        if (statistics_scroll_view.scrollY != null && statistics_scroll_view.height != null) {

                            val scrollBounds = Rect()
                            statistics_scroll_view.getDrawingRect(scrollBounds)
                            val topAM = statistics_am_line_chart.y
                            val topPM = statistics_pm_line_chart.y

                            val bottomAM = topAM + statistics_am_line_chart.height
                            val bottomPM = topPM + statistics_pm_line_chart.height

                            if (scrollBounds.top < topAM && scrollBounds.bottom > bottomAM) {
                                statistics_am_line_chart.animateXY(450, 900, Easing.EaseInOutQuart)
                            } else if (scrollBounds.top < topPM && scrollBounds.bottom > bottomPM) {
                                statistics_pm_line_chart.animateXY(450, 900, Easing.EaseInOutQuart)

                            }

                            if (statistics_scroll_view.scrollY == statistics_scroll_view.getChildAt(
                                    0
                                ).measuredHeight - statistics_scroll_view.measuredHeight
                            ) {
                                //Log.e(logTag, "End of Scroll View")
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


    // Options (Date Range Options)
    private fun setOptionsSelected(
        optionSelected: String,
        setWhenItemType: (thisItem: ItemModel) -> Unit
    ) {
        val allItems = realm.where(ItemModel::class.java).findAll()
        val todayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val thisMonthDateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
        val thisWeekDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
        val thisTwoWeeksDateFormat = SimpleDateFormat("yyyy MMM W", Locale.US)
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

            "This Two Weeks" -> {
                val lastWeek = Calendar.getInstance()
                lastWeek.add(Calendar.WEEK_OF_MONTH, -1)
                val lastWeekDate = lastWeek.time

                allItems.forEach { thisItem ->
                    if ((thisTwoWeeksDateFormat.format(Date()) == thisTwoWeeksDateFormat.format(
                            thisItem.itemDate!!.time
                        )) ||
                        thisTwoWeeksDateFormat.format(lastWeekDate.time) == thisTwoWeeksDateFormat.format(
                            thisItem.itemDate!!.time
                        )
                    ) {
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
                        || (thisThreeDaysDateFormat.format(yesterdayDate.time) == thisThreeDaysDateFormat.format(
                            thisItem.itemDate!!.time
                        )) ||
                        (thisThreeDaysDateFormat.format(dayBeforeYesterdayDate.time) == thisThreeDaysDateFormat.format(
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
                        || (thisThreeMonthsDateFormat.format(onePreviousMonthDate.time) == thisThreeMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisThreeMonthsDateFormat.format(twoPreviousMonthDate.time) == thisThreeMonthsDateFormat.format(
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
                        || (thisSixMonthsDateFormat.format(onePreviousMonthDate.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(twoPreviousMonthDate.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(threePreviousMonthDate.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(fourPreviousMonthDate.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                        || (thisSixMonthsDateFormat.format(fivePreviousMonthDate.time) == thisSixMonthsDateFormat.format(
                            thisItem.itemDate!!.time
                        ))
                    ) {
                        setWhenItemType(thisItem)
                    }
                }
            }

            "All Time" -> {
                allItems.forEach { thisItem ->
                    setWhenItemType(thisItem)
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

    // Scroll View Theme
    private fun setTheme() {
        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            statistics_scroll_view.setBackgroundColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.dark_grey_two
                )
            )
            statistics_relative_layout.setBackgroundColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.dark_grey_two
                )
            )
        }

    }


    // Tutorial in Statistics
    private fun doShowcase() {
        if (DataClass.tutorialStatistics!!.itemValue == 0.0) {
            showcaseBuilder = GuideView.Builder(activity)
                .setTitle("Pick a Date")
                .setContentText(
                    "Computes the statistics of the items \n" +
                            "within that day."
                )
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(buttonPickADate)
                .setGuideListener(object : GuideListener {
                    override fun onDismiss(view: View) {
                        when (view.id) {
                            R.id.buttonPickADate -> {
                                showcaseBuilder.setTargetView(spinner_options_statistics)
                                    .setTitle("Spinner Date Range")
                                    .setDismissType(DismissType.anywhere)
                                    .setContentText("Computes the statistics of items within the date range.")
                                    .build()
                                    .show()

                                DataClass.tutorialRealm.beginTransaction()
                                DataClass.tutorialStatistics!!.itemValue = 0.1
                                DataClass.tutorialRealm.commitTransaction()
                            }
                        }
                    }
                })
            showcaseGuide = showcaseBuilder.build()
            showcaseGuide.show()
        }
    }


    // Formatter?
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

}

