package com.example.menu.Adapter

import android.R
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.example.menu.MainActivity
import com.example.menu.Model.Bookmark
import com.example.menu.Model.BookmarkSupplier
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier.expenditures
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_fill_up_dialog.*
import kotlinx.android.synthetic.main.bookmark_item_edit_dialog.*
import kotlinx.android.synthetic.main.bookmark_item_view_dialog.*
import kotlinx.android.synthetic.main.bookmark_list_item.view.*
import kotlinx.android.synthetic.main.card_view_bookmark_dialog.view.*
import kotlinx.android.synthetic.main.item_options_dialog.*
import kotlinx.android.synthetic.main.item_view_dialog.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class BookmarksDialogAdapter(val context: Context, private val bookmarks: List<Bookmark>) :
    RecyclerView.Adapter<BookmarksDialogAdapter.MyViewHolder>() {
    var savedItemType: String? = null
    var savedItemTitle: String? = null
    var savedItemValue: Double? = null
    val df = DecimalFormat("#.##")
    val itemConfig = RealmConfiguration.Builder().name("items.realm").build()
    val itemRealm = Realm.getInstance(itemConfig)
    var globalSavedDate: Date? = null
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
            LayoutInflater.from(context)
                .inflate(com.example.menu.R.layout.card_view_bookmark_dialog, parent, false)
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
                    val uuid: String = UUID.randomUUID().toString()
                    val timeNow = Calendar.getInstance()
                    timeNow.add(Calendar.HOUR_OF_DAY, 0)
                    timeNow.add(Calendar.MINUTE, 0)

                    var dateNow = Calendar.getInstance()
                    dateNow.add(Calendar.MONTH, 0)
                    dateNow.add(Calendar.DAY_OF_MONTH, 0)
                    dateNow.add(Calendar.YEAR, 0)


                    itemRealm.beginTransaction()
                    val item = realm.createObject(ItemModel::class.java, uuid)
                    item.itemTitle = currentBookmark!!.title
                    item.itemType = currentBookmark!!.type
                    item.itemValue = currentBookmark!!.price
                    item.itemTime = timeNow.time
                    item.itemDate = dateNow.time
                    itemRealm.commitTransaction()
                    showHomeFragment()

                }

            }

            itemView.setOnLongClickListener {
                currentBookmark?.let {

                }
                false
            }

        }

        fun setData(bookmark: Bookmark?, position: Int) {
            var balance: Double? = 0.0
            var expense: Double? = 0.0

            bookmark?.let {
                // list card theme
                if (themeMode!!.itemType == "Dark Mode") {
                    itemView.card_view_bookmark_dialog_card_view.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            context,
                            com.example.menu.R.color.card_view_bookmark_dark_state
                        )
                    )
                    itemView.card_view_bookmark_dialog_title.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            com.example.menu.R.color.item_text_color_dark_theme
                        )
                    )

                }

                when (bookmark.type) {
                    "Beverages" -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.beverages)
                    }
                    "Bills" -> itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.bills)
                    "Cash Deposit" -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.cash_deposit)
                    }
                    "Cosmetics" -> itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.cosmetics)
                    "Entertainment" -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.entertainment)
                    }
                    "Fare" -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.fare)
                    }
                    "Fitness" -> itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.fitness)
                    "Food" -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.food)
                    }
                    "Health" -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.health)
                    }
                    "Hygiene" -> itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.hygiene)
                    "Miscellaneous" -> {
                    itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.miscellaneous)
                    }
                    "School Expenses" -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.school_expenses)
                    }
                    "Shopping" -> itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.shopping)
                    "Utilities" -> itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.utilities)

                    else -> itemView.card_view_bookmark_dialog_image_view.setImageResource(com.example.menu.R.drawable.sample_logo_1)
                }

                itemView.card_view_bookmark_dialog_title.text = bookmark.title


            }

            this.currentBookmark = bookmark
            this.currentPosition = position
        }
    }

    private fun showHomeFragment() {  // refreshes Main Activity
        val intent =
            Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}