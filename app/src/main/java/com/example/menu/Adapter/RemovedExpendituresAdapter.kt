package com.example.menu.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.menu.Data.DataClass
import com.example.menu.Fragments.HomeFragment
import com.example.menu.Fragments.TrashFragment
import com.example.menu.MainActivity
import com.example.menu.Model.Expenditure
import com.example.menu.Model.RemovedExpenditure
import com.example.menu.Model.RemovedSupplier
import com.example.menu.Model.RemovedSupplier.removedExpenditures
import com.example.menu.Model.Supplier
import com.example.menu.Model.Supplier.expenditures
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.item_options_dialog.*
import kotlinx.android.synthetic.main.item_options_dialog.btnView
import kotlinx.android.synthetic.main.item_view_dialog.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.trash_item_options_dialog.*
import java.text.SimpleDateFormat
import java.util.*

class RemovedExpendituresAdapter(val context: Context, private val removedExpenditures: List<RemovedExpenditure>) :
    RecyclerView.Adapter<RemovedExpendituresAdapter.MyViewHolder>() {


    var removedConfig = RealmConfiguration.Builder().name("removedItems.realm").build()
    var removedRealm = Realm.getInstance(removedConfig)
    var config = RealmConfiguration.Builder().name("items.realm").build()
    var realm = Realm.getInstance(config)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(com.example.menu.R.layout.list_item, parent, false)
        return MyViewHolder(view)


    }

    override fun getItemCount(): Int {
        return removedExpenditures.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val removedExpenditure = removedExpenditures[position]
        holder.setData(removedExpenditure, position)


    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var currentExpenditure: RemovedExpenditure? = null
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

        fun setData(removedExpenditure: RemovedExpenditure?, position: Int) {

            removedExpenditure?.let {
                itemView.txvTitle.text = removedExpenditure.title
                itemView.txvPrice.text = removedExpenditure.price.toString()

                when (removedExpenditure.type) {
                    "Beverages" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.beverages)
                    "Cash Deposit" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.cash_deposit)
                    "Fare" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.fare)
                    "Food" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.food)
                    "Health" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.health)
                    "School Expenses" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.school_expenses)
                    "Miscellaneous" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.miscellaneous)
                    "Entertainment" -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.entertainment)
                    else -> itemView.item_list_type_icon.setBackgroundResource(com.example.menu.R.drawable.sample_logo_1)
                }

            }

            this.currentExpenditure = removedExpenditure
            this.currentPosition = position
        }
    }

    private fun showItemDialog(itemId: String) {
        var dateFormat = SimpleDateFormat("MMM dd, yyyy `", Locale.US)
        var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        var dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.menu.R.layout.item_view_dialog)
        dialog.setCancelable(true)

        val queryItem = removedRealm.where(ItemModel::class.java).equalTo("itemId", itemId).findAll()
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

    private fun showOptionsDialog(itemId: String) {
        var dialog = Dialog(context)
        dialog.setContentView(com.example.menu.R.layout.trash_item_options_dialog)
        dialog.setTitle("Options")
        dialog.setCancelable(true)
        dialog.show()

        dialog.btnView.setOnClickListener {
            showItemDialog(itemId)
        }

        dialog.btnRestore.setOnClickListener {

            val restoreItem = removedRealm.where(ItemModel::class.java).equalTo("itemId", itemId).findAll()
            restoreItem.forEach {i ->

                realm.beginTransaction()
                val transferItem = realm.createObject(ItemModel::class.java, i.itemId)
                transferItem.itemType = i.itemType
                transferItem.itemTitle = i.itemTitle
                transferItem.itemValue = i.itemValue
                transferItem.itemDate = i.itemDate
                transferItem.itemTime = i.itemTime
                realm.commitTransaction()

                removedRealm.beginTransaction()
                i.deleteFromRealm()
                removedRealm.commitTransaction()
            }

            Supplier.expenditures.clear()
            val allItems = realm.where(ItemModel::class.java).findAll()
            allItems.forEach { thisItem ->
                Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
            }

            dialog.dismiss()
            showTrashFragment()
            Toast.makeText(context, "Item Restored", Toast.LENGTH_SHORT).show()
        }

        dialog.btnDelete.setOnClickListener {
            val deleteItem = removedRealm.where(ItemModel::class.java).equalTo("itemId", itemId).findAll()
            deleteItem.forEach { thisItem ->
                removedRealm.beginTransaction()
                thisItem.deleteFromRealm()
                removedRealm.commitTransaction()
            }

            dialog.dismiss()
            showTrashFragment()
            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
        }


    }

    private fun showTrashFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = TrashFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}