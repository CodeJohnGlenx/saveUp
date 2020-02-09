package com.example.menu.Dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.menu.Data.DataClass
import com.example.menu.Fragments.HomeFragment
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.fill_up_dialog_layout.*
import java.text.SimpleDateFormat
import java.util.*
import java.math.RoundingMode
import java.text.DecimalFormat


// all about the fill up dialog

class FillUpDialog(var fragmentActivity: FragmentActivity) : Dialog(fragmentActivity) {
    // variables for saving info on realm db
    val df = DecimalFormat("#.##")
    var dialog: Dialog? = null
    var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
    var savedDate: Date? = null
    var savedTime: Date? = null
    var savedItemType: String? = null
    var savedItemTitle: String? = null
    var savedItemId: String? = null
    var savedItemValue: Double? = null


    init {
        setCancelable(false)  // fill up dialog in not cancelable

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(com.example.menu.R.layout.fill_up_dialog_layout)

        // setting up realm
        Realm.init(fragmentActivity)
        val config = RealmConfiguration.Builder().name("items.realm").build()
        val realm = Realm.getInstance(config)


        getTitleTypeData()
        closeFillUpDialog()
        getDateNow()
        setDateButton()
        getTimeNow()
        setTimeButton()
        addInfo(realm)
        setUpTheme()


    }

    private fun closeFillUpDialog() {  // closes the dialog
        btn_close.setOnClickListener {
            this.dismiss()
        }
    }

    private fun getDateNow() {   // gets the date right now
        var dateNow = Calendar.getInstance()
        dateNow.add(Calendar.MONTH, 0)
        dateNow.add(Calendar.DAY_OF_MONTH, 0)
        dateNow.add(Calendar.YEAR, 0)
        val date = dateFormat.format(dateNow.time)
        savedDate = dateNow.time
        tv_date_value.text = date.toString()

    }

    private fun setDateButton() {  // users can set their own date
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        var theme: Int = 0

        if (themeMode!!.itemType == "Dark Mode") {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_DARK
        } else {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
        }

        btn_set_date.setOnClickListener {
            val selectedDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                fragmentActivity, theme,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val date = dateFormat.format(selectedDate.time)
                    savedDate = selectedDate.time
                    tv_date_value.text = date.toString()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()

        }
    }

    private fun getTimeNow() {  // gets the time right now
        var timeNow = Calendar.getInstance()
        timeNow.add(Calendar.HOUR_OF_DAY, 0)
        timeNow.add(Calendar.MINUTE, 0)
        val time = timeFormat.format(timeNow.time)
        savedTime = timeNow.time
        tv_time_value.text = time.toString()
    }

    private fun setTimeButton() {  // users can set their own time
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        var theme: Int = 0

        if (themeMode!!.itemType == "Dark Mode") {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK
        } else {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
        }


        btn_set_time.setOnClickListener {
            val selectedTime = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                fragmentActivity, theme, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val time = timeFormat.format(selectedTime.time)
                    savedTime = selectedTime.time
                    tv_time_value.text = time.toString()
                },
                selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE), false
            )
            timePicker.show()
        }
    }


    private fun getTitleTypeData() {  // gets the title(typeTitle) from DataClass
        tv_fill_up_type_title.text = DataClass.typeTitle!!.toString()
        savedItemType = DataClass.typeTitle!!.toString()

        when (DataClass.typeTitle!!.toString()) {
            "Beverages" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.beverages)
            "Cash Deposit" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.cash_deposit)
            "Fare" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.fare)
            "Food" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.food)
            "Health" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.health)
            "School Expenses" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.school_expenses)
            "Miscellaneous" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.miscellaneous)
            "Entertainment" -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.entertainment)
            else -> iv_fill_up_type_icon.setBackgroundResource(R.drawable.sample_logo_1)
        }

    }



    private fun addInfo(realm: Realm) {  // adds info to the database
        val errorMessage: String = "Please fill out Title and Monetary Value Correctly"
        val successMessage: String = "Item successfully added"
        df.roundingMode = RoundingMode.CEILING
        btn_done.setOnClickListener {
            try {
                val title: String? = et_title.text.toString()
                savedItemTitle = title
                val monetaryValue: Double? = et_monetary_value!!.text.toString().toDouble()
                savedItemValue = monetaryValue
                val uuid: String = UUID.randomUUID().toString()
                if (title!!.isNotEmpty() && title.isNotBlank()) {  // if title is not empty and not blank then go to next statement
                    monetaryValue?.let { // if monetaryValue is not null execute the following statements:
                        realm.beginTransaction()
                        // saves or adds the info on the realm database
                        val item = realm.createObject(ItemModel::class.java, uuid)
                        item.itemType = savedItemType
                        item.itemTitle = savedItemTitle!!.trim()
                        item.itemValue = df.format(savedItemValue).toDouble()
                        item.itemDate = savedDate
                        item.itemTime = savedTime
                        realm.commitTransaction()

                        Supplier.expenditures.clear()  // clearing the Supplier.expenditures(mutablelist) so we won't show replication of data on our recyclerview on HomeFragment
                        // responsible for adding all info from our database into Supplier.expenditures
                        val allItems = realm.where(ItemModel::class.java).findAll()
                        allItems.forEach { thisItem ->
                            if (dateFormat.format(savedDate!!.time) == dateFormat.format(thisItem.itemDate!!.time))
                            Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }

                        // showHomeFragment is updated then fill up dialog is dismissed
                        showHomeFragment()
                        this.dismiss()
                        Toast.makeText(fragmentActivity, successMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                // if title is empty or title is blank, show error message
                if (title.isEmpty() || title.isBlank()) {
                    Toast.makeText(fragmentActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(fragmentActivity, errorMessage, Toast.LENGTH_SHORT).show()  // if monetary value or any error occurred, show error message
            }
        }
    }

    private fun showHomeFragment() {  // we created our own showHomeFragment here since it is quite complicated to import MainActivity's ShowHomeFragment function. Still, it has the same functionality
        val transaction = fragmentActivity.supportFragmentManager.beginTransaction()
        val fragment = HomeFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setUpTheme() {
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

        if (themeMode!!.itemType == "Dark Mode") {
            fill_up_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
            fill_up_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))

            tv_fill_up_type_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            tv_fill_up_title.setTextColor(ContextCompat.getColor(context, R.color.white))

            et_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            et_title.setHintTextColor(ContextCompat.getColor(context, R.color.grey))

            tv_date.setTextColor(ContextCompat.getColor(context, R.color.white))
            tv_date_value.setTextColor(ContextCompat.getColor(context, R.color.white))

            tv_time.setTextColor(ContextCompat.getColor(context, R.color.white))
            tv_time_value.setTextColor(ContextCompat.getColor(context, R.color.white))

            tv_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            et_monetary_value.setHintTextColor(ContextCompat.getColor(context, R.color.grey))
            et_monetary_value.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}