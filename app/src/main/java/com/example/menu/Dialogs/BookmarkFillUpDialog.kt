package com.example.menu.Dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.menu.Data.DataClass
import com.example.menu.Fragments.HomeFragment
import com.example.menu.Fragments.SettingsFragment
import com.example.menu.Model.Bookmark
import com.example.menu.Model.BookmarkSupplier
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_fill_up_dialog.*
import kotlinx.android.synthetic.main.fill_up_dialog_layout.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class BookmarkFillUpDialog(var fragmentActivity: FragmentActivity) : Dialog(fragmentActivity) {
    val df = DecimalFormat("#.##")
    var savedItemType: String? = null
    var savedItemTitle: String? = null
    var savedItemValue: Double? = null

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
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

        if (themeMode!!.itemType == "Dark Mode") {
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
            "Beverages" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.beverages)
            "Bills" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.bills)
            "Cash Deposit" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.cash_deposit)
            "Cosmetics" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.cosmetics)
            "Entertainment" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.entertainment)
            "Fare" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.fare)
            "Fitness" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.fitness)
            "Food" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.food)
            "Health" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.health)
            "Hygiene" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.hygiene)
            "Miscellaneous" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.miscellaneous)
            "School Expenses" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.school_expenses)
            "Shopping" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.shopping)
            "Utilities" -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.utilities)
            else -> img_bookmark_fill_up_type_icon.setImageResource(R.drawable.sample_logo_1)
        }



    }

    private fun addBookmark(bookmarkRealm: Realm) {
        val errorMessage: String = "Please fill out Title and Monetary Value Correctly"
        val successMessage: String = "Bookmark item successfully added"
        df.roundingMode = RoundingMode.CEILING
        bookmark_btn_done.setOnClickListener {
            try {
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

                        showSettingsFragment()
                        this.dismiss()
                        Toast.makeText(fragmentActivity, successMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                if (title.isEmpty() || title.isBlank()) {
                    Toast.makeText(fragmentActivity, errorMessage, Toast.LENGTH_SHORT).show()
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