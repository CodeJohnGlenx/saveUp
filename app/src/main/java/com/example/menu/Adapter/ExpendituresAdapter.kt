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
import android.provider.ContactsContract
import android.view.Window
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.menu.Data.DataClass.itemId
import com.example.menu.MainActivity
import com.example.menu.Model.RemovedExpenditure
import com.example.menu.Model.RemovedSupplier
import com.example.menu.Model.Supplier
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fill_up_dialog_layout.*
import kotlinx.android.synthetic.main.item_edit_dialog.*
import kotlinx.android.synthetic.main.item_view_dialog.*
import kotlinx.android.synthetic.main.item_view_dialog.btn_done
import kotlinx.android.synthetic.main.item_view_dialog.tv_date_value
import kotlinx.android.synthetic.main.item_view_dialog.tv_fill_up_type_title
import kotlinx.android.synthetic.main.item_view_dialog.tv_time_value
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ExpendituresAdapter(val context: Context, private val expenditures: List<Expenditure>) :
    RecyclerView.Adapter<ExpendituresAdapter.MyViewHolder>() {
    var globalSavedDate: Date? = null
    var config = RealmConfiguration.Builder().name("items.realm").build()
    var realm = Realm.getInstance(config)
    var removedConfig = RealmConfiguration.Builder().name("removedItems.realm").build()
    var removedRealm = Realm.getInstance(removedConfig)

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
                itemView.txvTitle.text = expenditure.title
                itemView.txvPrice.text = expenditure.price.toString()

                when (expenditure.type) {
                    "Beverages" ->  {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.beverages)
                        expense = expenditure.price
                    }
                    "Cash Deposit" -> {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.cash_deposit)
                        balance = expenditure.price
                    }
                    "Fare" -> {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.fare)
                        expense = expenditure.price
                    }
                    "Food" -> {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.food)
                        expense = expenditure.price
                    }
                    "Health" -> {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.health)

                    }
                    "School Expenses" -> {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.school_expenses)
                        DataClass.expense += expenditure.price
                    }
                    "Miscellaneous" -> {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.miscellaneous)
                        DataClass.expense += expenditure.price
                    }
                    "Entertainment" -> {
                        itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.entertainment)
                        DataClass.expense += expenditure.price
                    }
                    else -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.sample_logo_1)
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
                "Beverages" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.beverages)
                "Cash Deposit" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.cash_deposit)
                "Fare" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.fare)
                "Food" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.food)
                "Health" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.health)
                "School Expenses" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.school_expenses)
                "Miscellaneous" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.miscellaneous)
                "Entertainment" -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.entertainment)
                else -> dialog.iv_item_view_type_icon.setBackgroundResource(com.example.menu.R.drawable.sample_logo_1)
            }

        }
        dialog.show()

        dialog.btn_done.setOnClickListener {
            dialog.dismiss()
        }
    }


    // shows edit dialog when edit button is clicked
    private fun showEditDialog(itemId: String) {
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
            dialog.tv_fill_up_type_title.text = item.itemType
            dialog.et_edit_title.setText(item.itemTitle)
            dialog.et_edit_monetary_value.setText(item.itemValue.toString())

            val date = dateFormat.format(item.itemDate!!.time)
            savedDate = item.itemDate
            dialog.tv_edit_date_value.text = date.toString()

            val time = timeFormat.format(item.itemTime!!.time)
            savedTime = item.itemTime
            dialog.tv_edit_time_value.text = time.toString()

            when (item.itemType.toString()) {
                "Beverages" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.beverages)
                "Cash Deposit" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.cash_deposit)
                "Fare" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.fare)
                "Food" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.food)
                "Health" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.health)
                "School Expenses" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.school_expenses)
                "Miscellaneous" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.miscellaneous)
                "Entertainment" -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.entertainment)
                else -> dialog.iv_edit_item_type_icon.setBackgroundResource(com.example.menu.R.drawable.sample_logo_1)
            }
        }

        dialog.show()

        // dismisses the dialog when close button is clicked
        dialog.btn_edit_close.setOnClickListener {
            dialog.dismiss()
        }


        // set date button
        dialog.btn_edit_set_date.setOnClickListener {
            val selectedDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                context,
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
            val selectedTime = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
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
                            item.itemType = dialog.tv_fill_up_type_title.text.toString()
                            item.itemTitle = dialog.et_edit_title.text.toString()
                            item.itemValue = dialog.et_edit_monetary_value.text.toString().toDouble()
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


    }


}