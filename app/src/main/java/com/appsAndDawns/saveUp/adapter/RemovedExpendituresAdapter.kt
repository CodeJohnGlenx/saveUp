package com.appsAndDawns.saveUp.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.fragment.TrashFragment
import com.appsAndDawns.saveUp.model.Expenditure
import com.appsAndDawns.saveUp.model.RemovedExpenditure
import com.appsAndDawns.saveUp.model.Supplier
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.item_options_dialog.btnView
import kotlinx.android.synthetic.main.item_view_dialog.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.trash_item_options_dialog.*
import java.text.SimpleDateFormat
import java.util.*


// All about Removed Items
class RemovedExpendituresAdapter(val context: Context, private val removedExpenditures: List<RemovedExpenditure>) :
    RecyclerView.Adapter<RemovedExpendituresAdapter.MyViewHolder>() {


    private var removedConfig: RealmConfiguration = RealmConfiguration.Builder().name("removedItems.realm").build()
    private var removedRealm: Realm = Realm.getInstance(removedConfig)
    var config: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    var realm: Realm = Realm.getInstance(config)
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
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

        private var currentExpenditure: RemovedExpenditure? = null
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

        fun setData(removedExpenditure: RemovedExpenditure?, position: Int) {

            removedExpenditure?.let {
                itemView.txvTitle.text = removedExpenditure.title
                itemView.txvPrice.text = DataClass.prettyCount(removedExpenditure.price)

                if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                    itemView.list_item_card.setCardBackgroundColor(ContextCompat.getColorStateList(context, R.color.list_card_state_dark_theme))
                    itemView.txvPrice.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    itemView.txvTitle.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                }

                when (removedExpenditure.type) {
                    context.getString(R.string.beverages) -> itemView.item_list_type_icon.setImageResource(R.drawable.beverages)  // "Beverages"
                    context.getString(R.string.bills) -> itemView.item_list_type_icon.setImageResource(R.drawable.bills)  // "Bills"
                    context.getString(R.string.income) -> itemView.item_list_type_icon.setImageResource(R.drawable.income)  // "Cash Deposit"
                    context.getString(R.string.cosmetics) -> itemView.item_list_type_icon.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
                    context.getString(R.string.entertainment) -> itemView.item_list_type_icon.setImageResource(R.drawable.entertainment)  // "Entertainment"
                    context.getString(R.string.transportation) -> itemView.item_list_type_icon.setImageResource(R.drawable.transportation)  // "Fare"
                    context.getString(R.string.fitness) -> itemView.item_list_type_icon.setImageResource(R.drawable.fitness)  //  "Fitness"
                    context.getString(R.string.food) -> itemView.item_list_type_icon.setImageResource(R.drawable.food)  // "Food"
                    context.getString(R.string.health) -> itemView.item_list_type_icon.setImageResource(R.drawable.health)  // "Health"
                    context.getString(R.string.hygiene) -> itemView.item_list_type_icon.setImageResource(R.drawable.hygiene)  // "Hygiene"
                    context.getString(R.string.miscellaneous) -> itemView.item_list_type_icon.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
                    context.getString(R.string.education) -> itemView.item_list_type_icon.setImageResource(R.drawable.education)  // "School Expenses"
                    context.getString(R.string.shopping) -> itemView.item_list_type_icon.setImageResource(R.drawable.shopping)  // "Shopping"
                    context.getString(R.string.utilities) -> itemView.item_list_type_icon.setImageResource(R.drawable.utilities)  // "Utilities"
                    else -> itemView.item_list_type_icon.setImageResource(R.drawable.sample_logo_1)
                }

            }

            this.currentExpenditure = removedExpenditure
            this.currentPosition = position
        }
    }

    // Shows Removed Item Info
    private fun showItemDialog(itemId: String) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.item_view_dialog)
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
                context.getString(R.string.beverages) -> dialog.iv_item_view_type_icon.setImageResource(R.drawable.beverages)  // "Beverages"
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


    // Shows Options about Removed Item: View, Restore, Delete
    private fun showOptionsDialog(itemId: String) {
        val dialog = Dialog(context)

        if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
            dialog.setContentView(R.layout.dark_trash_item_options_dialog)
        } else {
            dialog.setContentView(R.layout.trash_item_options_dialog)
        }

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