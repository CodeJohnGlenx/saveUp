package com.example.menu.Adapter

import android.R
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
import com.example.menu.Data.DataClass
import com.example.menu.Fragments.SettingsFragment
import com.example.menu.Model.Bookmark
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_item_edit_dialog.*
import kotlinx.android.synthetic.main.bookmark_item_view_dialog.*
import kotlinx.android.synthetic.main.bookmark_list_item.view.*
import kotlinx.android.synthetic.main.item_options_dialog.*
import java.math.RoundingMode
import java.text.DecimalFormat

class BookmarksAdapter(val context: Context, private val bookmarks: List<Bookmark>) :
    RecyclerView.Adapter<BookmarksAdapter.MyViewHolder>() {
    var savedItemTitle: String? = null
    var savedItemValue: Double? = null
    val df = DecimalFormat("#.##")
    var config = RealmConfiguration.Builder().name("items.realm").build()
    var realm = Realm.getInstance(config)
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode =
        themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    val bookmarkItemsConfig = RealmConfiguration.Builder().name("bookmarkItemsRealm.realm").build()
    val bookmarkItemsRealm = Realm.getInstance(bookmarkItemsConfig)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(com.example.menu.R.layout.bookmark_list_item, parent, false)
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

        var currentBookmark: Bookmark? = null
        var currentPosition: Int = 0
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
                if (themeMode!!.itemType == "Dark Mode") {
                    itemView.card_view_bookmark_list_item.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            context,
                            com.example.menu.R.color.settings_card_view_dark_mode_state
                        )
                    )
                    itemView.tv_price_bookmark_list_item.setTextColor(ContextCompat.getColor(context, R.color.white))
                    itemView.tv_title_bookmark_list_item.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    itemView.card_view_bookmark_list_item.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            context,
                            com.example.menu.R.color.settings_card_view_light_mode_state
                        )
                    )
                    itemView.tv_price_bookmark_list_item.setTextColor(
                        ContextCompat.getColor(
                            context,
                            com.example.menu.R.color.dark_grey
                        )
                    )
                    itemView.tv_title_bookmark_list_item.setTextColor(
                        ContextCompat.getColor(
                            context,
                            com.example.menu.R.color.dark_grey
                        )
                    )
                }


                itemView.tv_title_bookmark_list_item.text = bookmark.title
                itemView.tv_price_bookmark_list_item.text = DataClass.prettyCount(bookmark.price)


                when (bookmark.type) {
                    "Beverages" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.beverages)

                    }
                    "Bills" -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.bills)
                    "Cash Deposit" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.cash_deposit)

                    }
                    "Cosmetics" -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.cosmetics)
                    "Entertainment" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.entertainment)
                    }
                    "Fare" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.fare)

                    }
                    "Fitness" -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.fitness)
                    "Food" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.food)

                    }
                    "Health" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.health)
                    }
                    "Hygiene" -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.hygiene)
                    "Miscellaneous" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.miscellaneous)
                    }
                    "School Expenses" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.school_expenses)
                    }
                    "Shopping" -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.shopping)
                    "Utilities" -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.utilities)
                    else -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.sample_logo_1)
                }


            }

            this.currentBookmark = bookmark
            this.currentPosition = position
        }
    }

    private fun showBookmarkItemDialog(bookmarkItemId: String) {

         val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.menu.R.layout.bookmark_item_view_dialog)
        dialog.setCancelable(true)

        val bookmarkItem = bookmarkItemsRealm.where(ItemModel::class.java).equalTo("itemId", bookmarkItemId).findFirst()
        dialog.bookmark_tv_item_view_dialog_fill_up_type_title.text = bookmarkItem!!.itemType.toString()
        dialog.bookmark_tv_title_item_view_dialog.text = bookmarkItem.itemTitle.toString()
        dialog.bookmark_tv_monetary_value_item_view_dialog.text = bookmarkItem.itemValue.toString()
        dialog.show()


        dialog.bookmark_btn_done_item_view_dialog.setOnClickListener {
            dialog.dismiss()
        }

        when (bookmarkItem!!.itemType.toString()) {
            "Beverages" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.beverages)
            "Bills" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.bills)
            "Cash Deposit" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.cash_deposit)
            "Cosmetics" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.cosmetics)
            "Entertainment" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.entertainment)
            "Fare" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.fare)
            "Fitness" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.fitness)
            "Food" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.food)
            "Health" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.health)
            "Hygiene" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.hygiene)
            "Miscellaneous" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.miscellaneous)
            "School Expenses" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.school_expenses)
            "Shopping" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.shopping)
            "Utilities" -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.utilities)
            else -> dialog.bookmark_iv_item_view_dialog_type_icon.setImageResource(com.example.menu.R.drawable.sample_logo_1)
        }

        if (themeMode!!.itemType == "Dark Mode") {
            dialog.bookmark_item_view_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))
            dialog.bookmark_item_view_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))

            dialog.bookmark_tv_item_view_dialog_fill_up_type_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.bookmark_tv_view_title_item_view_dialog.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.bookmark_tv_title_item_view_dialog.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.bookmark_tv_value_value_item_view_dialog.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.bookmark_tv_monetary_value_item_view_dialog.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
        }
    }

    private fun showBookmarkOptionsDialog(bookmarkItemId: String) {
        val dialog = Dialog(context)
        dialog.setContentView(com.example.menu.R.layout.item_options_dialog)

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
        val bookmarkItem = bookmarkItemsRealm.where(ItemModel::class.java).equalTo("itemId", "$bookmarkItemId").findFirst()
        bookmarkItemsRealm.beginTransaction()
        bookmarkItem!!.deleteFromRealm()
        bookmarkItemsRealm.commitTransaction()

        showSettingsFragment()
    }

    private fun showSettingsFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        transaction.replace(com.example.menu.R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showEditDialog(bookmarkItemId: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.example.menu.R.layout.bookmark_item_edit_dialog)
        dialog.setCancelable(false)

        if (themeMode!!.itemType == "Dark Mode") {
            dialog.bookmark_item_edit_dialog_scroll_view.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))
            dialog.bookmark_item_edit_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, com.example.menu.R.color.dark_grey_three))

            dialog.bookmark_item_edit_dialog_tv_type.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))

            dialog.bookmark_item_edit_dialog_title.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.bookmark_item_edit_dialog_title_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.bookmark_item_edit_dialog_title_value.setHintTextColor(ContextCompat.getColor(context, com.example.menu.R.color.grey))

            dialog.bookmark_item_edit_dialog_monetary_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.bookmark_item_edit_dialog_monetary_value_value.setTextColor(ContextCompat.getColor(context, com.example.menu.R.color.white))
            dialog.bookmark_item_edit_dialog_monetary_value_value.setHintTextColor(ContextCompat.getColor(context, com.example.menu.R.color.grey))
        }

        dialog.bookmark_item_edit_dialog_btn_close.setOnClickListener {
            dialog.dismiss()
        }


        val bookmarkItem = bookmarkItemsRealm.where(ItemModel::class.java).equalTo("itemId", bookmarkItemId).findFirst()
        when (bookmarkItem!!.itemType) {
            "Beverages" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.beverages)
            "Bills" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.bills)
            "Cash Deposit" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.cash_deposit)
            "Cosmetics" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.cosmetics)
            "Entertainment" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.entertainment)
            "Fare" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.fare)
            "Fitness" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.fitness)
            "Food" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.food)
            "Health" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.health)
            "Hygiene" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.hygiene)
            "Miscellaneous" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.miscellaneous)
            "School Expenses" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.school_expenses)
            "Shopping" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.shopping)
            "Utilities" -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.utilities)
            else -> dialog.bookmark_item_edit_dialog_img_icon.setImageResource(com.example.menu.R.drawable.sample_logo_1)
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
                if (savedItemTitle!!.isNotBlank()  && savedItemTitle!!.isNotEmpty()) {
                    bookmarkItemsRealm.beginTransaction()
                    bookmarkItemTwo!!.itemTitle = savedItemTitle!!.trim()
                    bookmarkItemTwo!!.itemValue = savedItemValue
                    bookmarkItemsRealm.commitTransaction()
                    dialog.dismiss()
                    showSettingsFragment()
                } else {
                    Toast.makeText(context, "please fill out the forms accordingly", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(context, "please fill out forms accordingly", Toast.LENGTH_SHORT).show()
            }

        }

        dialog.show()

    }
}