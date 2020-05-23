package com.appsAndDawns.saveUp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.MainActivity
import com.appsAndDawns.saveUp.model.Bookmark
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.card_view_bookmark_dialog.view.*
import java.util.*

class BookmarksDialogAdapter(val context: Context, private val bookmarks: List<Bookmark>) :
    RecyclerView.Adapter<BookmarksDialogAdapter.MyViewHolder>() {
    private val itemConfig: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    val itemRealm: Realm = Realm.getInstance(itemConfig)
    var config: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
    var realm: Realm = Realm.getInstance(config)
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode =
        themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.card_view_bookmark_dialog, parent, false)
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
                    val uuid: String = UUID.randomUUID().toString()
                    val timeNow = Calendar.getInstance()
                    timeNow.add(Calendar.HOUR_OF_DAY, 0)
                    timeNow.add(Calendar.MINUTE, 0)

                    val dateNow = Calendar.getInstance()
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


                    if (DataClass.tutorialHome!!.itemValue == 0.2) {
                        DataClass.tutorialRealm.beginTransaction()
                        DataClass.tutorialHome!!.itemValue = 0.3
                        DataClass.tutorialRealm.commitTransaction()
                    }

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

            bookmark?.let {
                // list card theme
                if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                    itemView.card_view_bookmark_dialog_card_view.setCardBackgroundColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.card_view_bookmark_dark_state
                        )
                    )
                    itemView.card_view_bookmark_dialog_title.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.item_text_color_dark_theme
                        )
                    )

                }

                when (bookmark.type) {
                    context.getString(R.string.beverages) -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.beverages)  // "Beverages"
                    }
                    context.getString(R.string.bills)-> itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.bills)  // "Bills"
                    context.getString(R.string.income) -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.income)  // "Cash Deposit"
                    }
                    context.getString(R.string.cosmetics) -> itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
                    context.getString(R.string.entertainment) -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.entertainment)  // "Entertainment"
                    }
                    context.getString(R.string.transportation) -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.transportation)  //  "Fare"
                    }
                    context.getString(R.string.fitness) -> itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.fitness)  //  "Fitness"
                    context.getString(R.string.food) -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.food)  // "Food"
                    }
                    context.getString(R.string.health) -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.health)  // "Health"
                    }
                    context.getString(R.string.hygiene) -> itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.hygiene)  // "Hygiene"
                    context.getString(R.string.miscellaneous) -> {
                    itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
                    }
                    context.getString(R.string.education) -> {
                        itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.education)  // "School Expenses"
                    }
                    context.getString(R.string.shopping) -> itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.shopping)  // "Shopping"
                    context.getString(R.string.utilities) -> itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.utilities)  // "Utilities"

                    else -> itemView.card_view_bookmark_dialog_image_view.setImageResource(R.drawable.sample_logo_1)
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