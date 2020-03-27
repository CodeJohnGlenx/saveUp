package com.example.menu.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.menu.Data.DataClass
import com.example.menu.Fragments.HomeFragment
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier.expenditures
import kotlinx.android.synthetic.main.item_options_dialog.*
import kotlinx.android.synthetic.main.list_item.view.*
import androidx.core.content.ContextCompat.startActivity
import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.provider.ContactsContract
import android.view.Window
import androidx.core.content.ContextCompat
import com.example.menu.MainActivity
import com.example.menu.Model.RemovedExpenditure
import com.example.menu.Model.RemovedSupplier
import com.example.menu.Model.Supplier
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.item_edit_dialog.*
import kotlinx.android.synthetic.main.item_view_dialog.*
import kotlinx.android.synthetic.main.item_view_dialog.btn_done
import kotlinx.android.synthetic.main.item_view_dialog.tv_date_value
import kotlinx.android.synthetic.main.item_view_dialog.tv_fill_up_type_title
import kotlinx.android.synthetic.main.item_view_dialog.tv_time_value
import java.lang.Exception
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class ExpendituresAdapter(val context: Context, private val expenditures: List<Expenditure>) :
    RecyclerView.Adapter<ExpendituresAdapter.MyViewHolder>() {
    var globalSavedDate: Date? = null
    var config = RealmConfiguration.Builder().name("items.realm").build()
    var realm = Realm.getInstance(config)
    var removedConfig = RealmConfiguration.Builder().name("removedItems.realm").build()
    var removedRealm = Realm.getInstance(removedConfig)
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    val df = DecimalFormat("#.##")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(com.example.menu.R.layout.list_item, parent, false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return expenditures.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val expenditure = expenditures[position]
        holder.setData(expenditure, position)



    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var currentExpenditure: Expenditure? = null
        var currentPosition: Int = 0


        init {
            Realm.init(context)


            itemView.setOnClickListener {
                currentExpenditure?.let {
                    DataClass.itemId = currentExpenditure!!.id
                    showItemDialog(DataClass.itemId!!)

                }

            }

            itemView.setOnLongClickListener {
                currentExpenditure?.let {
                    DataClass.itemId = currentExpenditure!!.id
                    showOptionsDialog(DataClass.itemId!!)

                }
                false
            }

        }

        fun setData(expenditure: Expenditure?, position: Int) {
            var balance: Double? = 0.0
            var expense: Double? = 0.0

            expenditure?.let {
                // list card theme
                if (themeMode!!.itemType == "Dark Mode") {
                    itemView.list_item_card.setCardBackgroundColor(ContextCompat.getColorStateList(context, com.example.menu.R.color.list_card_state_dark_theme))
                    itemView.txvPrice.setTextColor(ContextCompat.getColor(context, R.color.white))
                    itemView.txvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    itemView.list_item_card.setCardBackgroundColor(ContextCompat.getColorStateList(context, com.example.menu.R.color.list_card_state_light_theme))
                    itemView.txvPrice.setTextColor(ContextCompat.getColorStateList(context, com.example.menu.R.color.list_card_text_state_light_theme))
                    itemView.txvTitle.setTextColor(ContextCompat.getColorStateList(context, com.example.menu.R.color.list_card_text_state_light_theme))
                }


                itemView.txvTitle.text = expenditure.title
                itemView.txvPrice.text = DataClass.prettyCount(expenditure.price)


                when (expenditure.type) {
                    "Beverages" ->  {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.beverages)
                        expense = expenditure.price
                    }
                    "Bills" -> itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.bills)
                    "Cash Deposit" -> {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.cash_deposit)
                        balance = expenditure.price
                    }
                    "Cosmetics" -> itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.cosmetics)
                    "Entertainment" -> {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.entertainment)
                        DataClass.expense += expenditure.price
                    }
                    "Fare" -> {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.fare)
                        expense = expenditure.price
                    }
                    "Fitness" -> itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.fitness)
                    "Food" -> {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.food)
                        expense = expenditure.price
                    }
                    "Health" -> {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.health)

                    }
                    "Hygiene" -> itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.hygiene)
                    "Miscellaneous" -> {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.miscellaneous)
                        DataClass.expense += expenditure.price
                    }
                    "School Expenses" -> {
                        itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.school_expenses)
                        DataClass.expense += expenditure.price
                    }
                    "Shopping" -> itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.shopping)
                    "Utilities" -> itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.utilities)
                    else -> itemView.item_list_type_icon.setImageResource(com.example.menu.R.drawable.sample_logo_1)
                }


            }

            this.currentExpenditure = expenditure
            this.currentPosition = position
        }
    }


    // shows edit and delete options
    private fun showOptionsDialog(itemId: String) {
        var dialog = Dialog(context)
        dialog.setContentView(com.example.menu.R.layout.item_options_dialog)

        // options dialog
        if (themeMode!!.itemType == "Dark Mode") {
            dialog.setContentView(com.example.menu.R.layout.item_options_dark_dialog)
            dialog.item_options_dialog_id.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_two))
        } else {
            dialog.setContentView(com.example.menu.R.layout.item_options_dialog)
            dialog.item_options_dialog_id.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

        }


        dialog.setTitle("Options")
        dialog.setCancelable(true)
        dialog.show()


        dialog.btnEdit.setOnClickListener {
            showEditDialog(itemId)
        }

        dialog.btnView.setOnClickListener {
            showItemDialog(itemId)
        }

        dialog.btnRemove.setOnClickListener {
            // deletes the item itself
            val removeItem =
                realm.where(ItemModel::class.java).equalTo("itemId", "$itemId").findAll()
            removeItem.forEach { i ->

                // transfers removeItem information to another Builder by replicating its information then deletes the information of the item being replicated
                removedRealm.beginTransaction()
                val transferItem = removedRealm.createObject(ItemModel::class.java, i.itemId)
                transferItem.itemType = i.itemType
                transferItem.itemTitle = i.itemTitle
                transferItem.itemValue = i.itemValue
                transferItem.itemDate = i.itemDate
                DataClass.selectedDate = i.itemDate
                transferItem.itemTime = i.itemTime
                removedRealm.commitTransaction()

                realm.beginTransaction()
                i.deleteFromRealm()
                realm.commitTransaction()
            }
            RemovedSupplier.removedExpenditures.clear()
            val allItems = removedRealm.where(ItemModel::class.java).findAll()
            allItems.forEach { thisItem ->
                RemovedSupplier.removedExpenditures.add(0, RemovedExpenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
            }


            showHomeFragment()
            Toast.makeText(context, "Item Removed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHomeFragment() {  // refreshes Main Activity
        val intent =
            Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    // shows the item information when the item is clicked
    private fun showItemDialog(itemId: String) {
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        var dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.menu.R.layout.item_view_dialog)
        dialog.setCancelable(true)

        val queryItem = realm.where(ItemModel::class.java).equalTo("itemId", itemId).findAll()
        queryItem.forEach { item ->
            dialog.tv_fill_up_type_title.text = item.itemType.toString()
            dialog.tv_title.text = item.itemTitle.toString()
            val date = dateFormat.format(item.itemDate!!.time)
            dialog.tv_date_value.text = date.toString()
            val time = timeFormat.format(item.itemTime!!.time)
            dialog.tv_time_value.text = time.toString()
            dialog.tv_monetary_value.text = item.itemValue.toString()

            when (item.itemType.toString()) {
                "Beverages" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.beverages)
                "Bills" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.bills)
                "Cash Deposit" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.cash_deposit)
                "Cosmetics" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.cosmetics)
                "Entertainment" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.entertainment)
                "Fare" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.fare)
                "Fitness" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.fitness)
                "Food" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.food)
                "Health" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.health)
                "Hygiene" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.hygiene)
                "Miscellaneous" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.miscellaneous)
                "School Expenses" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.school_expenses)
                "Shopping" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.shopping)
                "Utilities" -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.utilities)
                else -> dialog.iv_item_view_type_icon.setImageResource(com.example.menu.R.drawable.sample_logo_1)
            }

        }
        dialog.show()

        dialog.btn_done.setOnClickListener {
            dialog.dismiss()
        }

        if (themeMode!!.itemType == "Dark Mode") {
            dialog.item_view_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))
            dialog.item_view_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))

            dialog.tv_fill_up_type_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.tv_view_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.tv_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.tv_view_date.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.tv_date_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.tv_view_time.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.tv_time_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.tv_value_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.tv_monetary_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
        }


    }


    // shows edit dialog when edit button is clicked
    private fun showEditDialog(itemId: String) {
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

        var savedDate: Date? = null
        var savedTime: Date? = null
        var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        var dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.menu.R.layout.item_edit_dialog)
        dialog.setCancelable(false)


        // gets the current information of an item
        val getItem = realm.where(ItemModel::class.java).equalTo("itemId", "$itemId").findAll()
        getItem.forEach { item ->
            dialog.tv_edit_fill_up_type_title.text = item.itemType
            dialog.et_edit_title.setText(item.itemTitle)
            dialog.et_edit_monetary_value.setText(item.itemValue.toString())

            val date = dateFormat.format(item.itemDate!!.time)
            savedDate = item.itemDate
            dialog.tv_edit_date_value.text = date.toString()

            val time = timeFormat.format(item.itemTime!!.time)
            savedTime = item.itemTime
            dialog.tv_edit_time_value.text = time.toString()

            when (item.itemType.toString()) {
                "Beverages" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.beverages)
                "Bills" ->  dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.bills)
                "Cash Deposit" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.cash_deposit)
                "Cosmetics" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.cosmetics)
                "Entertainment" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.entertainment)
                "Fare" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.fare)
                "Fitness" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.fitness)
                "Food" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.food)
                "Health" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.health)
                "Hygiene" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.hygiene)
                "Miscellaneous" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.miscellaneous)
                "School Expenses" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.school_expenses)
                "Shopping" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.shopping)
                "Utilities" -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.utilities)
                else -> dialog.iv_edit_item_type_icon.setImageResource(com.example.menu.R.drawable.sample_logo_1)
            }
        }

        dialog.show()

        // dismisses the dialog when close button is clicked
        dialog.btn_edit_close.setOnClickListener {
            dialog.dismiss()
        }


        // set date button
        dialog.btn_edit_set_date.setOnClickListener {
            val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
            val themeRealm = Realm.getInstance(themeConfig)
            var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
            var theme: Int = 0

            if (themeMode!!.itemType == "Dark Mode") {
                theme = DatePickerDialog.THEME_DEVICE_DEFAULT_DARK
            } else {
                theme = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
            }

            val selectedDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                context, theme,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val date = dateFormat.format(selectedDate.time)
                    savedDate = selectedDate.time
                    dialog.tv_edit_date_value.text = date.toString()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }


        // set time button
        dialog.btn_edit_set_time.setOnClickListener {
            val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
            val themeRealm = Realm.getInstance(themeConfig)
            var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
            var theme: Int = 0

            if (themeMode!!.itemType == "Dark Mode") {
                theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK
            } else {
                theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
            }
            val selectedTime = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                context, theme, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val time = timeFormat.format(selectedTime.time)
                    savedTime = selectedTime.time
                    dialog.tv_edit_time_value.text = time.toString()
                },
                selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE), false
            )
            timePicker.show()
        }


        // done button
        dialog.btn_edit_done.setOnClickListener {
            try {
                if (dialog.et_edit_title.text.toString().isNotEmpty() && dialog.et_edit_title.text.toString().isNotBlank()) {

                    dialog.et_edit_monetary_value?.let {
                        val editItem =
                            realm.where(ItemModel::class.java).equalTo("itemId", "$itemId")
                                .findAll()
                        editItem.forEach { item ->
                            realm.beginTransaction()
                            item.itemType = dialog.tv_edit_fill_up_type_title.text.toString()
                            item.itemTitle = dialog.et_edit_title.text.toString()
                            item.itemValue = df.format(dialog.et_edit_monetary_value.text.toString().toDouble()).toDouble()
                            item.itemDate = savedDate
                            DataClass.selectedDate = savedDate
                            item.itemTime = savedTime
                            realm.commitTransaction()
                        }

                        showHomeFragment()
                        Toast.makeText(context, "Saved Changes", Toast.LENGTH_SHORT).show()
                        /*
                        Supplier.expenditures.clear()  // clearing the Supplier.expenditures(mutablelist) so we won't show replication of data on our recyclerview on HomeFragment
                        // responsible for adding all info from our database into Supplier.expenditures
                        var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                        val allItems = realm.where(ItemModel::class.java).findAll()
                        allItems.forEach { thisItem ->
                            if (dateFormat.format(savedDate!!.time) == dateFormat.format(thisItem.itemDate!!.time))
                                Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                        }
                        */

                    }
                }

            } catch (exception: Exception) {

            }

        }

        if (themeMode!!.itemType == "Dark Mode") {
            dialog.item_edit_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))
            dialog.item_edit_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))

            dialog.tv_edit_fill_up_type_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.tv_edit_fill_up_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.et_edit_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.et_edit_title.setHintTextColor(ContextCompat.getColor(context, com.example.menu.R.color.grey))

            dialog.tv_edit_date.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.tv_edit_date_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.tv_edit_time.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.tv_edit_time_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.tv_edit_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.et_edit_monetary_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.et_edit_monetary_value.setHintTextColor(ContextCompat.getColor(context, com.example.menu.R.color.grey))


        }


    }


}