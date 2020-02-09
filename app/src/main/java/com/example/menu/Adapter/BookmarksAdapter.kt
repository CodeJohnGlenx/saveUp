package com.example.menu.Adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.menu.Data.DataClass
import com.example.menu.Model.Bookmark
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier.expenditures
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_list_item.view.*
import kotlinx.android.synthetic.main.item_view_dialog.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*

class BookmarksAdapter(val context: Context, private val bookmarks: List<Bookmark>) :
    RecyclerView.Adapter<BookmarksAdapter.MyViewHolder>() {
    var globalSavedDate: Date? = null
    var config = RealmConfiguration.Builder().name("items.realm").build()
    var realm = Realm.getInstance(config)
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode =
        themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

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
                itemView.tv_price_bookmark_list_item.text = bookmark.price.toString()


                when (bookmark.type) {
                    "Beverages" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.beverages)

                    }
                    "Cash Deposit" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.cash_deposit)

                    }
                    "Fare" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.fare)

                    }
                    "Food" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.food)

                    }
                    "Health" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.health)
                    }
                    "School Expenses" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.school_expenses)
                    }
                    "Miscellaneous" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.miscellaneous)
                    }
                    "Entertainment" -> {
                        itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.entertainment)
                    }
                    else -> itemView.img_bookmark_list_item.setImageResource(com.example.menu.R.drawable.sample_logo_1)
                }


            }

            this.currentBookmark = bookmark
            this.currentPosition = position
        }
    }
}