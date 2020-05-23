package com.appsAndDawns.saveUp.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.fragment.HomeFragment
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.fill_up_dialog_layout.*
import java.text.SimpleDateFormat
import java.util.*
import java.math.RoundingMode
import java.text.DecimalFormat


// all about the fill up dialog

class FillUpDialog(private var fragmentActivity: FragmentActivity) : Dialog(fragmentActivity) {
    // variables for saving info on realm db
    private val df = DecimalFormat("#.##")
    private var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
    private var savedDate: Date? = null
    private var savedTime: Date? = null
    private var savedItemType: String? = null
    private var savedItemTitle: String? = null
    private var savedItemValue: Double? = null

    init {
        setCancelable(false)  // fill up dialog in not cancelable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.fill_up_dialog_layout)

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

        showToast()
    }

    private fun closeFillUpDialog() {  // closes the dialog
        btn_close.setOnClickListener {
            this.dismiss()
        }
    }


    private fun getDateNow() {   // gets the date right now
        val dateNow = Calendar.getInstance()
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
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        val theme: Int

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            theme = R.style.date_picker_dark_theme
        } else {
            theme = R.style.date_picker_light_theme
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
        val timeNow = Calendar.getInstance()
        timeNow.add(Calendar.HOUR_OF_DAY, 0)
        timeNow.add(Calendar.MINUTE, 0)
        val time = timeFormat.format(timeNow.time)
        savedTime = timeNow.time
        tv_time_value.text = time.toString()
    }

    private fun setTimeButton() {  // users can set their own time
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        val theme: Int

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            theme = R.style.time_picker_dark_theme
        } else {
            theme = R.style.time_picker_light_theme
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
            getString(R.string.beverages) -> setImageResource(R.drawable.beverages)  // Beverages
            getString(R.string.bills) -> setImageResource(R.drawable.bills)  // bills
            getString(R.string.income) -> setImageResource(R.drawable.income)  // Cash Deposit
            getString(R.string.cosmetics) -> setImageResource(R.drawable.cosmetics)  // cosmetics
            getString(R.string.entertainment) -> setImageResource(R.drawable.entertainment)  // entertainment
            getString(R.string.transportation) -> setImageResource(R.drawable.transportation)  // transportation  // Fare
            getString(R.string.fitness) -> setImageResource(R.drawable.fitness)  // fitness
            getString(R.string.food) -> setImageResource(R.drawable.food)  // food
            getString(R.string.health) -> setImageResource(R.drawable.health)  // health
            getString(R.string.hygiene) -> setImageResource(R.drawable.hygiene)  // hygiene
            getString(R.string.miscellaneous) -> setImageResource(R.drawable.miscellaneous)  // miscellaneous
            getString(R.string.education) -> setImageResource(R.drawable.education)  // education
            getString(R.string.shopping) -> setImageResource(R.drawable.shopping)  // shopping
            getString(R.string.utilities) -> setImageResource(R.drawable.utilities)  // utilities
            else ->setImageResource(R.drawable.sample_logo_1)
        }

    }


    // adds item to realm if requirements are met
    private fun addInfo(realm: Realm) {  // adds info to the database
        val errorMessage = "Please fill out Item Name and Item Value Correctly"
        val successMessage = "Item successfully added"
        df.roundingMode = RoundingMode.CEILING
        btn_done.setOnClickListener {
            try {
                val title: String? = et_title.text.toString()
                savedItemTitle = title
                val monetaryValue: Double? = et_monetary_value!!.text.toString().toDouble()
                savedItemValue = monetaryValue
                val uuid: String = UUID.randomUUID().toString()

                /*
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

                        DataClass.insertSupplierItems(DataClass.realm, DataClass.includeItems)



                        // i've updated it to 0.1 to dismiss the repetition of previous tutorial in home fragment
                        if (DataClass.tutorialHome!!.itemValue == 0.0) {
                            DataClass.tutorialRealm.beginTransaction()
                            DataClass.tutorialHome!!.itemValue = 0.1
                            DataClass.tutorialRealm.commitTransaction()
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

                 */

                if (et_title.text.toString().isNotEmpty() && et_title.text.toString().isNotBlank() &&
                            et_monetary_value.text.toString().isNotEmpty() && et_monetary_value.text.toString().isNotBlank()  && et_monetary_value.text.toString() != ".")
                {
                    realm.beginTransaction()
                    // saves or adds the info on the realm database
                    val item = realm.createObject(ItemModel::class.java, uuid)
                    item.itemType = savedItemType
                    item.itemTitle = savedItemTitle!!.trim()
                    item.itemValue = df.format(savedItemValue).toDouble()
                    item.itemDate = savedDate
                    item.itemTime = savedTime
                    realm.commitTransaction()

                    DataClass.insertSupplierItems(DataClass.realm, DataClass.includeItems)

                    // i've updated it to 0.1 to dismiss the repetition of previous tutorial in home fragment
                    if (DataClass.tutorialHome!!.itemValue == 0.0) {
                        DataClass.tutorialRealm.beginTransaction()
                        DataClass.tutorialHome!!.itemValue = 0.1
                        DataClass.tutorialRealm.commitTransaction()
                    }

                    // showHomeFragment is updated then fill up dialog is dismissed
                    showHomeFragment()
                    this.dismiss()
                    Toast.makeText(fragmentActivity, successMessage, Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(fragmentActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }


            } catch (e: Exception) {
                Toast.makeText(fragmentActivity, e.message, Toast.LENGTH_SHORT).show()  // if monetary value or any error occurred, show error message
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
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
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

    private fun showToast() {
        val message = "fill up the info about your item"
        if (DataClass.tutorialHome!!.itemValue == 0.0) {
            val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            val toastView: View = toast.view
            val toastMessage = toastView.findViewById(android.R.id.message) as TextView
            toastMessage.textSize = 18f
            toast.show()
        }
    }

    private fun getString(stringId: Int): String {
        return context.getString(stringId)
    }

    private fun setImageResource(imageId: Int) {
        iv_fill_up_type_icon.setImageResource(imageId)
    }

}