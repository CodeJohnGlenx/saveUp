package com.appsAndDawns.saveUp.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.model.Expenditure
import kotlinx.android.synthetic.main.item_options_dialog.*
import kotlinx.android.synthetic.main.list_item.view.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.Window
import androidx.core.content.ContextCompat
import com.appsAndDawns.saveUp.MainActivity
import com.appsAndDawns.saveUp.model.RemovedExpenditure
import com.appsAndDawns.saveUp.model.RemovedSupplier
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
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


// Adapter about Items
class ExpendituresAdapter(val context: Context, private val expenditures: List<Expenditure>) :
    RecyclerView.Adapter<ExpendituresAdapter.MyViewHolder>() {
    var config: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    var realm: Realm = Realm.getInstance(config)
    private var removedConfig: RealmConfiguration = RealmConfiguration.Builder().name("removedItems.realm").build()
    private var removedRealm: Realm = Realm.getInstance(removedConfig)
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    private val df = DecimalFormat("#.##")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
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

        private var currentExpenditure: Expenditure? = null
        private var currentPosition: Int = 0


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


            expenditure?.let {
                // list card theme
                if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                    itemView.list_item_card.setCardBackgroundColor(ContextCompat.getColorStateList(context, R.color.list_card_state_dark_theme))
                    itemView.txvPrice.setTextColor(ContextCompat.getColor(context, R.color.white))
                    itemView.txvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    itemView.list_item_card.setCardBackgroundColor(ContextCompat.getColorStateList(context, R.color.list_card_state_light_theme))
                    itemView.txvPrice.setTextColor(ContextCompat.getColorStateList(context, R.color.list_card_text_state_light_theme))
                    itemView.txvTitle.setTextColor(ContextCompat.getColorStateList(context, R.color.list_card_text_state_light_theme))
                }


                itemView.txvTitle.text = expenditure.title
                itemView.txvPrice.text = DataClass.prettyCount(expenditure.price)


                when (expenditure.type) {
                    context.getString(R.string.beverages) ->  {
                        itemView.item_list_type_icon.setImageResource(R.drawable.beverages)  // "Beverages"
                    }
                    context.getString(R.string.bills) -> itemView.item_list_type_icon.setImageResource(R.drawable.bills)  //  "Bills"
                    context.getString(R.string.income) -> {
                        itemView.item_list_type_icon.setImageResource(R.drawable.income)  //  "Cash Deposit"
                    }
                   context.getString(R.string.cosmetics) -> itemView.item_list_type_icon.setImageResource(R.drawable.cosmetics)  //  "Cosmetics"
                    context.getString(R.string.entertainment) -> {
                        itemView.item_list_type_icon.setImageResource(R.drawable.entertainment)  //  "Entertainment"
                    }
                    context.getString(R.string.transportation) -> {
                        itemView.item_list_type_icon.setImageResource(R.drawable.transportation)  //  "Fare"
                    }
                    context.getString(R.string.fitness) -> itemView.item_list_type_icon.setImageResource(R.drawable.fitness)  // "Fitness"
                    context.getString(R.string.food) -> {
                        itemView.item_list_type_icon.setImageResource(R.drawable.food)  // "Food"
                    }
                   context.getString(R.string.health) -> {
                        itemView.item_list_type_icon.setImageResource(R.drawable.health)  //  "Health"

                    }
                    context.getString(R.string.hygiene) -> itemView.item_list_type_icon.setImageResource(R.drawable.hygiene)  // "Hygiene"
                    context.getString(R.string.miscellaneous) -> {
                        itemView.item_list_type_icon.setImageResource(R.drawable.miscellaneous)  //  "Miscellaneous"
                    }
                   context.getString(R.string.education) -> {
                        itemView.item_list_type_icon.setImageResource(R.drawable.education)  // "School Expenses"
                    }
                    context.getString(R.string.shopping) -> itemView.item_list_type_icon.setImageResource(R.drawable.shopping)  // "Shopping"
                    context.getString(R.string.utilities) -> itemView.item_list_type_icon.setImageResource(R.drawable.utilities)  // "Utilities"
                    else -> itemView.item_list_type_icon.setImageResource(R.drawable.sample_logo_1)
                }


            }

            this.currentExpenditure = expenditure
            this.currentPosition = position
        }
    }


    // shows edit and delete options
    private fun showOptionsDialog(itemId: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.item_options_dialog)

        // options dialog
        if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
            dialog.setContentView(R.layout.item_options_dark_dialog)
            dialog.item_options_dialog_id.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_two))
        } else {
            dialog.setContentView(R.layout.item_options_dialog)
            dialog.item_options_dialog_id.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

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
                realm.where(ItemModel::class.java).equalTo("itemId", itemId).findAll()
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
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.item_view_dialog)
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
                context.getString(R.string.beverages) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.beverages)  // Beverages
                context.getString(R.string.bills) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.bills)  // "Bills"
                context.getString(R.string.income) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.income)  // "Cash Deposit"
                context.getString(R.string.cosmetics) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
                context.getString(R.string.entertainment) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.entertainment)  // "Entertainment"
                context.getString(R.string.transportation) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.transportation)  // "Fare"
                context.getString(R.string.fitness) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.fitness)  // "Fitness"
                context.getString(R.string.food) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.food)  // "Food"
                context.getString(R.string.health) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.health)  // "Health"
                context.getString(R.string.hygiene) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.hygiene)  // "Hygiene"
                context.getString(R.string.miscellaneous) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
                context.getString(R.string.education) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.education)  // "School Expenses"
                context.getString(R.string.shopping) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.shopping)  // "Shopping"
                context.getString(R.string.utilities) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.utilities)  // "Utilities"
                else -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.sample_logo_1)
            }

        }
        dialog.show()

        dialog.btn_done.setOnClickListener {
            dialog.dismiss()
        }

        if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
            dialog.item_view_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
            dialog.item_view_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))

            dialog.tv_fill_up_type_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.tv_view_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.tv_title.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.tv_view_date.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.tv_date_value.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.tv_view_time.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.tv_time_value.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.tv_value_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.tv_monetary_value.setTextColor(ContextCompat.getColor(context, R.color.white))
        }


    }


    // shows edit dialog when edit button is clicked
    private fun showEditDialog(itemId: String) {
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

        var savedDate: Date? = null
        var savedTime: Date? = null
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.item_edit_dialog)
        dialog.setCancelable(false)


        // gets the current information of an item
        val getItem = realm.where(ItemModel::class.java).equalTo("itemId", itemId).findAll()
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
                context.getString(R.string.beverages) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.beverages)  // "Beverages"
                context.getString(R.string.bills) ->  dialog.iv_edit_item_type_icon.setImageResource(R.drawable.bills)  // "Bills"
                context.getString(R.string.income) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.income)  // "Cash Deposit"
                context.getString(R.string.cosmetics) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
                context.getString(R.string.entertainment) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.entertainment)   // "Entertainment"
                context.getString(R.string.transportation) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.transportation)  // "Fare"
                context.getString(R.string.fitness) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.fitness)  // "Fitness"
                context.getString(R.string.food) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.food)  // "Food"
                context.getString(R.string.health) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.health)  // "Health"
                context.getString(R.string.hygiene) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.hygiene)  // "Hygiene"
                context.getString(R.string.miscellaneous) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
                context.getString(R.string.education) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.education)  // "School Expenses"
                context.getString(R.string.shopping) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.shopping)  // "Shopping"
                context.getString(R.string.utilities) -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.utilities)  // "Utilities"
                else -> dialog.iv_edit_item_type_icon.setImageResource(R.drawable.sample_logo_1)
            }
        }

        dialog.show()

        // dismisses the dialog when close button is clicked
        dialog.btn_edit_close.setOnClickListener {
            dialog.dismiss()
        }


        // set date button
        dialog.btn_edit_set_date.setOnClickListener {
            val theme: Int

            if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                theme = R.style.date_picker_dark_theme
            } else {
                theme = R.style.date_picker_light_theme
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
            val theme: Int

            if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                theme = R.style.time_picker_dark_theme
            } else {
                theme = R.style.time_picker_light_theme
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
                if (dialog.et_edit_title.text.toString().isNotEmpty() && dialog.et_edit_title.text.toString().isNotBlank()
                    && dialog.et_edit_monetary_value.text.toString().isNotEmpty() && dialog.et_edit_monetary_value.text.toString().isNotBlank() &&
                    dialog.et_edit_monetary_value.text.toString() != ".")
                {
                        val editItem =
                            realm.where(ItemModel::class.java).equalTo("itemId", itemId).findFirst()
                            realm.beginTransaction()
                            editItem!!.itemType = dialog.tv_edit_fill_up_type_title.text.toString()
                            editItem.itemTitle = dialog.et_edit_title.text.toString().trim()
                            editItem.itemValue = (df.format(dialog.et_edit_monetary_value.text.toString().toDouble())).toDouble()
                            editItem.itemDate = savedDate
                            DataClass.selectedDate = savedDate
                            editItem.itemTime = savedTime
                            realm.commitTransaction()


                        showHomeFragment()
                        Toast.makeText(context, "Saved Changes", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(context,  "Please fill up Item Name and Item Value correctly", Toast.LENGTH_SHORT).show()
                }

            } catch (exception: Exception) {
                Toast.makeText(context,  exception.message, Toast.LENGTH_SHORT).show()
            }

        }

        if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
            dialog.item_edit_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
            dialog.item_edit_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))

            dialog.tv_edit_fill_up_type_title.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.tv_edit_fill_up_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.et_edit_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.et_edit_title.setHintTextColor(ContextCompat.getColor(context, R.color.grey))

            dialog.tv_edit_date.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.tv_edit_date_value.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.tv_edit_time.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.tv_edit_time_value.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.tv_edit_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.et_edit_monetary_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.et_edit_monetary_value.setHintTextColor(ContextCompat.getColor(context, R.color.grey))


        }


    }


}