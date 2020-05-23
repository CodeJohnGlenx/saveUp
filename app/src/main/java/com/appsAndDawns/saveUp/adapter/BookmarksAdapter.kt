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
import com.appsAndDawns.saveUp.fragment.SettingsFragment
import com.appsAndDawns.saveUp.model.Bookmark
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_item_edit_dialog.*
import kotlinx.android.synthetic.main.bookmark_item_view_dialog.*
import kotlinx.android.synthetic.main.bookmark_list_item.view.*
import kotlinx.android.synthetic.main.item_options_dialog.*
import java.math.RoundingMode
import java.text.DecimalFormat


// All about bookmarks as an item. Can be viewed, edited, and removed
class BookmarksAdapter(val context: Context, private val bookmarks: List<Bookmark>) :
    RecyclerView.Adapter<BookmarksAdapter.MyViewHolder>() {
    private var savedItemTitle: String? = null
    private var savedItemValue: Double? = null
    private val df = DecimalFormat("#.##")
    var config: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    var realm: Realm = Realm.getInstance(config)
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode =
        themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    private val bookmarkItemsConfig: RealmConfiguration = RealmConfiguration.Builder().name("bookmarkItemsRealm.realm").build()
    private val bookmarkItemsRealm: Realm = Realm.getInstance(bookmarkItemsConfig)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.bookmark_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookmarks.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookmark = bookmarks[position]
        holder.setData(bookmark, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var currentBookmark: Bookmark? = null
        private var currentPosition: Int = 0
        init {
            Realm.init(context)

            itemView.setOnClickListener {
                currentBookmark?.let {
                    showBookmarkItemDialog(currentBookmark!!.id) }

            }

            itemView.setOnLongClickListener {
                currentBookmark?.let {
                    showBookmarkOptionsDialog(currentBookmark!!.id)

                }
                false
            }

        }

        fun setData(bookmark: Bookmark?, position: Int) {

            bookmark?.let {
                // list card theme
                if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                    itemView.card_view_bookmark_list_item.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.settings_card_view_dark_mode_state
                        )
                    )
                    itemView.tv_price_bookmark_list_item.setTextColor(ContextCompat.getColor(context, R.color.white))
                    itemView.tv_title_bookmark_list_item.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    itemView.card_view_bookmark_list_item.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.settings_card_view_light_mode_state
                        )
                    )
                    itemView.tv_price_bookmark_list_item.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.dark_grey
                        )
                    )
                    itemView.tv_title_bookmark_list_item.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.dark_grey
                        )
                    )
                }


                itemView.tv_title_bookmark_list_item.text = bookmark.title
                itemView.tv_price_bookmark_list_item.text = DataClass.prettyCount(bookmark.price)


                when (bookmark.type) {
                    context.getString(R.string.beverages) -> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.beverages)  // "Beverages"

                    }
                    context.getString(R.string.bills) -> itemView.img_bookmark_list_item.setImageResource(R.drawable.bills)  // "Bills"
                    context.getString(R.string.income) -> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.income)  // "Cash Deposit"

                    }
                    context.getString(R.string.cosmetics) -> itemView.img_bookmark_list_item.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
                    context.getString(R.string.entertainment) -> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.entertainment)  // "Entertainment"
                    }
                    context.getString(R.string.transportation) -> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.transportation)  //  "Fare"

                    }
                    context.getString(R.string.fitness) -> itemView.img_bookmark_list_item.setImageResource(R.drawable.fitness)  // "Fitness"
                    context.getString(R.string.food) -> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.food)  // "Food"

                    }
                    context.getString(R.string.health) -> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.health)  // "Health"
                    }
                    context.getString(R.string.hygiene) -> itemView.img_bookmark_list_item.setImageResource(R.drawable.hygiene)  // "Hygiene"
                    context.getString(R.string.miscellaneous)-> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
                    }
                    context.getString(R.string.education)-> {
                        itemView.img_bookmark_list_item.setImageResource(R.drawable.education)  // "School Expenses"
                    }
                    context.getString(R.string.shopping) -> itemView.img_bookmark_list_item.setImageResource(R.drawable.shopping)  // "Shopping"
                    context.getString(R.string.utilities) -> itemView.img_bookmark_list_item.setImageResource(R.drawable.utilities)  // "Utilities"
                    else -> itemView.img_bookmark_list_item.setImageResource(R.drawable.sample_logo_1)
                }


            }

            this.currentBookmark = bookmark
            this.currentPosition = position
        }
    }


    // Bookmark Item View Dialog
    private fun showBookmarkItemDialog(bookmarkItemId: String) {

         val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bookmark_item_view_dialog)
        dialog.setCancelable(true)

        val bookmarkItem = bookmarkItemsRealm.where(ItemModel::class.java).equalTo("itemId", bookmarkItemId).findFirst()
        dialog.bookmark_tv_item_view_dialog_fill_up_type_title.text = bookmarkItem!!.itemType.toString()
        dialog.bookmark_tv_title_item_view_dialog.text = bookmarkItem.itemTitle.toString()
        dialog.bookmark_tv_monetary_value_item_view_dialog.text = bookmarkItem.itemValue.toString()
        dialog.show()

        dialog.bookmark_btn_done_item_view_dialog.setOnClickListener {
            dialog.dismiss()
        }

        when (bookmarkItem.itemType.toString()) {
            context.getString(R.string.beverages) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.beverages)  // "Beverages"
            context.getString(R.string.bills) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.bills)  // "Bills"
            context.getString(R.string.income) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.income)  // "Cash Deposit"
            context.getString(R.string.cosmetics) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
            context.getString(R.string.entertainment) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.entertainment)  //  "Entertainment"
            context.getString(R.string.transportation) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.transportation) // "Fare"
            context.getString(R.string.fitness) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.fitness)  // "Fitness"
            context.getString(R.string.food) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.food)  // "Food"
            context.getString(R.string.health) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.health)  // "Health"
            context.getString(R.string.hygiene) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.hygiene)  // "Hygiene"
            context.getString(R.string.miscellaneous) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
            context.getString(R.string.education) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.education)  // "School Expenses"
            context.getString(R.string.shopping) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.shopping)  // "Shopping"
            context.getString(R.string.utilities) -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.utilities)  // "Utilities"
            else -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(R.drawable.sample_logo_1)
        }

        if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
            dialog.bookmark_item_view_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
            dialog.bookmark_item_view_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))

            dialog.bookmark_tv_item_view_dialog_fill_up_type_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.bookmark_tv_view_title_item_view_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.bookmark_tv_title_item_view_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.bookmark_tv_value_value_item_view_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.bookmark_tv_monetary_value_item_view_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    // Bookmark Options: View, Edit, Remove Dialog
    private fun showBookmarkOptionsDialog(bookmarkItemId: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.item_options_dialog)

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

        dialog.btnView.setOnClickListener {
            showBookmarkItemDialog(bookmarkItemId)
        }

        dialog.btnRemove.setOnClickListener {
            deleteBookmarkItem(bookmarkItemId)
            dialog.dismiss()
        }

        dialog.btnEdit.setOnClickListener {
            showEditDialog(bookmarkItemId)
            dialog.dismiss()
        }
    }

    private fun deleteBookmarkItem(bookmarkItemId: String) {
        val bookmarkItem = bookmarkItemsRealm.where(ItemModel::class.java).equalTo("itemId", bookmarkItemId).findFirst()
        bookmarkItemsRealm.beginTransaction()
        bookmarkItem!!.deleteFromRealm()
        bookmarkItemsRealm.commitTransaction()

        showSettingsFragment()
    }

    private fun showSettingsFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    // Bookmark Item Edit Dialog
    private fun showEditDialog(bookmarkItemId: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bookmark_item_edit_dialog)
        dialog.setCancelable(false)

        if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
            dialog.bookmark_item_edit_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
            dialog.bookmark_item_edit_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))

            dialog.bookmark_item_edit_dialog_tv_type.setTextColor(ContextCompat.getColor(context, R.color.white))

            dialog.bookmark_item_edit_dialog_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.bookmark_item_edit_dialog_title_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.bookmark_item_edit_dialog_title_value.setHintTextColor(ContextCompat.getColor(context, R.color.grey))

            dialog.bookmark_item_edit_dialog_monetary_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.bookmark_item_edit_dialog_monetary_value_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            dialog.bookmark_item_edit_dialog_monetary_value_value.setHintTextColor(ContextCompat.getColor(context, R.color.grey))
        }

        dialog.bookmark_item_edit_dialog_btn_close.setOnClickListener {
            dialog.dismiss()
        }


        val bookmarkItem = bookmarkItemsRealm.where(ItemModel::class.java).equalTo("itemId", bookmarkItemId).findFirst()
        when (bookmarkItem!!.itemType) {
            context.getString(R.string.beverages) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.beverages)  // "Beverages"
            context.getString(R.string.bills) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.bills)  // "Bills"
            context.getString(R.string.income) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.income)  // "Cash Deposit"
            context.getString(R.string.cosmetics) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.cosmetics)  //  "Cosmetics"
            context.getString(R.string.entertainment) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.entertainment)  // "Entertainment"
            context.getString(R.string.transportation) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.transportation)  // "Fare"
            context.getString(R.string.fitness) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.fitness)  // "Fitness"
            context.getString(R.string.food) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.food)  // "Food"
            context.getString(R.string.health) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.health)  // "Health"
            context.getString(R.string.hygiene) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.hygiene)  // "Hygiene"
            context.getString(R.string.miscellaneous) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
            context.getString(R.string.education) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.education)  // "School Expenses"
            context.getString(R.string.shopping) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.shopping)  // "Shopping"
            context.getString(R.string.utilities) -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.utilities)  // "Utilities"
            else -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(R.drawable.sample_logo_1)
        }

        dialog.bookmark_item_edit_dialog_tv_type.text = bookmarkItem.itemType
        dialog.bookmark_item_edit_dialog_title_value.setText(bookmarkItem.itemTitle)
        dialog.bookmark_item_edit_dialog_monetary_value_value.setText(bookmarkItem.itemValue.toString())
        df.roundingMode = RoundingMode.CEILING

        dialog.bookmark_item_edit_dialog_btn_done.setOnClickListener {
            try {
                val bookmarkItemTwo = bookmarkItemsRealm.where(ItemModel::class.java).equalTo("itemId", bookmarkItemId).findFirst()
                savedItemTitle = dialog.bookmark_item_edit_dialog_title_value.text.toString()
                savedItemValue = dialog.bookmark_item_edit_dialog_monetary_value_value.text.toString().toDouble()
                if (dialog.bookmark_item_edit_dialog_title_value.text.toString().isNotBlank()  &&
                    dialog.bookmark_item_edit_dialog_title_value.text.toString().isNotEmpty() &&
                        dialog.bookmark_item_edit_dialog_monetary_value_value.text.toString().isNotEmpty() &&
                    dialog.bookmark_item_edit_dialog_monetary_value_value.text.toString().isNotBlank() && dialog.bookmark_item_edit_dialog_monetary_value_value.text.toString() != ".")
                {
                    bookmarkItemsRealm.beginTransaction()
                    bookmarkItemTwo!!.itemTitle = savedItemTitle!!.trim()
                    bookmarkItemTwo.itemValue = savedItemValue
                    bookmarkItemsRealm.commitTransaction()
                    dialog.dismiss()
                    Toast.makeText(context, "Bookmark Item successfully edited", Toast.LENGTH_SHORT).show()
                    showSettingsFragment()
                } else
                {
                    Toast.makeText(context, "Please fill out Item Name and Item Value Correctly", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

        }

        dialog.show()
    }
}