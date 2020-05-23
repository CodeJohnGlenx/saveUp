package com.appsAndDawns.saveUp.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.fragment.SettingsFragment
import com.appsAndDawns.saveUp.model.Bookmark
import com.appsAndDawns.saveUp.model.BookmarkSupplier
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_fill_up_dialog.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class BookmarkFillUpDialog(private var fragmentActivity: FragmentActivity) : Dialog(fragmentActivity) {
    private val df = DecimalFormat("#.##")
    private var savedItemType: String? = null
    private var savedItemTitle: String? = null
    private var savedItemValue: Double? = null

    init {
        setCancelable(false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.bookmark_fill_up_dialog)

        Realm.init(fragmentActivity)
        val bookmarkItemsConfig = RealmConfiguration.Builder().name("bookmarkItemsRealm.realm").build()
        val bookmarkItemsRealm = Realm.getInstance(bookmarkItemsConfig)

        closeFillUpDialog()
        setUpTheme()
        getTitleTypeData()
        addBookmark(bookmarkItemsRealm)
    }


    private fun closeFillUpDialog() {
        bookmark_btn_close.setOnClickListener {
            this.dismiss()
        }
    }

    private fun setUpTheme() {
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

        if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
            scroll_view_bookmark_fill_up_dialog_settings_fragment.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
            bookmark_fill_up_dialog_relative_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))

            tv_bookmark_fill_up_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            tv_bookmark_fill_up_type_title.setTextColor(ContextCompat.getColor(context, R.color.white))

            et_bookmark_title.setTextColor(ContextCompat.getColor(context, R.color.white))
            et_bookmark_title.setHintTextColor(ContextCompat.getColor(context, R.color.grey))

            tv_bookmark_value.setTextColor(ContextCompat.getColor(context, R.color.white))
            et_bookmark_monetary_value.setHintTextColor(ContextCompat.getColor(context, R.color.grey))
            et_bookmark_monetary_value.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    private fun getTitleTypeData() {  // gets the title(typeTitle) from DataClass
        tv_bookmark_fill_up_type_title.text = DataClass.typeTitle!!.toString()
        savedItemType = DataClass.typeTitle!!.toString()


        when (DataClass.typeTitle!!.toString()) {
            context.getString(R.string.beverages) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.beverages)  //  "Beverages"
            context.getString(R.string.bills) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.bills)  // "Bills"
            context.getString(R.string.income) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.income)  // "Cash Deposit"
            context.getString(R.string.cosmetics) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
            context.getString(R.string.entertainment) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.entertainment)  // "Entertainment"
            context.getString(R.string.transportation) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.transportation)  // "Fare"
            context.getString(R.string.fitness) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.fitness)  // "Fitness"
            context.getString(R.string.food) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.food)  // "Food"
            context.getString(R.string.health) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.health)  // "Health"
            context.getString(R.string.hygiene) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.hygiene)  //  "Hygiene"
            context.getString(R.string.miscellaneous) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
            context.getString(R.string.education) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.education)  // "School Expenses"
            context.getString(R.string.shopping) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.shopping)  // "Shopping"
            context.getString(R.string.utilities) -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.utilities)  // "Utilities"
            else -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.sample_logo_1)
        }



    }


    // add bookmark
    private fun addBookmark(bookmarkRealm: Realm) {
        val errorMessage = "Please fill out Item Name and Item Value correctly"
        val successMessage = "Bookmark item successfully added"
        df.roundingMode = RoundingMode.CEILING
        bookmark_btn_done.setOnClickListener {
            try {
                /*
                val title: String? = et_bookmark_title.text.toString()
                savedItemTitle = title
                val monetaryValue: Double? = et_bookmark_monetary_value.text.toString().toDouble()
                savedItemValue = monetaryValue
                val uuid: String = UUID.randomUUID().toString()

                if (title!!.isNotEmpty() && title.isNotBlank()) {
                    monetaryValue?.let {
                        bookmarkRealm.beginTransaction()
                        val bookmarkItem = bookmarkRealm.createObject(ItemModel::class.java, uuid)
                        bookmarkItem.itemType = savedItemType
                        bookmarkItem.itemTitle = savedItemTitle!!.trim()
                        bookmarkItem.itemValue = df.format(savedItemValue).toDouble()
                        bookmarkRealm.commitTransaction()

                        BookmarkSupplier.bookmarks.clear()
                        val allBookmarkItems = bookmarkRealm.where(ItemModel::class.java).findAll()
                        allBookmarkItems.forEach { thisBookmarkItem ->
                            BookmarkSupplier.bookmarks.add(0, Bookmark(thisBookmarkItem.itemTitle!!, thisBookmarkItem.itemValue!!, thisBookmarkItem.itemId!!, thisBookmarkItem.itemType!!))
                        }


                        if (DataClass.tutorialSettings!!.itemValue == 0.1) {
                            DataClass.tutorialRealm.beginTransaction()
                            DataClass.tutorialSettings!!.itemValue = 0.2
                            DataClass.tutorialRealm.commitTransaction()
                        }

                        showSettingsFragment()
                        this.dismiss()
                        Toast.makeText(fragmentActivity, successMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                if (title.isEmpty() || title.isBlank()) {
                    Toast.makeText(fragmentActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                */

                val title: String? = et_bookmark_title.text.toString()
                savedItemTitle = title
                val monetaryValue: Double? = et_bookmark_monetary_value.text.toString().toDouble()
                savedItemValue = monetaryValue
                val uuid: String = UUID.randomUUID().toString()

                if (et_bookmark_title.text.toString().isNotEmpty() && et_bookmark_title.text.toString().isNotBlank()
                    && et_bookmark_monetary_value.text.toString().isNotEmpty() && et_bookmark_monetary_value.text.toString().isNotBlank() && et_bookmark_monetary_value.text.toString() != ".") {
                    bookmarkRealm.beginTransaction()
                    val bookmarkItem = bookmarkRealm.createObject(ItemModel::class.java, uuid)
                    bookmarkItem.itemType = savedItemType
                    bookmarkItem.itemTitle = savedItemTitle!!.trim()
                    bookmarkItem.itemValue = df.format(savedItemValue).toDouble()
                    bookmarkRealm.commitTransaction()

                    BookmarkSupplier.bookmarks.clear()
                    val allBookmarkItems = bookmarkRealm.where(ItemModel::class.java).findAll()
                    allBookmarkItems.forEach { thisBookmarkItem ->
                        BookmarkSupplier.bookmarks.add(
                            0,
                            Bookmark(
                                thisBookmarkItem.itemTitle!!,
                                thisBookmarkItem.itemValue!!,
                                thisBookmarkItem.itemId!!,
                                thisBookmarkItem.itemType!!
                            )
                        )
                    }


                    if (DataClass.tutorialSettings!!.itemValue == 0.1) {
                        DataClass.tutorialRealm.beginTransaction()
                        DataClass.tutorialSettings!!.itemValue = 0.2
                        DataClass.tutorialRealm.commitTransaction()
                    }

                    showSettingsFragment()
                    this.dismiss()
                    Toast.makeText(fragmentActivity, successMessage, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(fragmentActivity, errorMessage, Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun showSettingsFragment() {
        val transaction = fragmentActivity.supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}