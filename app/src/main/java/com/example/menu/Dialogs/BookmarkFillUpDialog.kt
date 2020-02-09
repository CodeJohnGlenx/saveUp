package com.example.menu.Dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.menu.Data.DataClass
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.bookmark_fill_up_dialog.*
import kotlinx.android.synthetic.main.fill_up_dialog_layout.*

class BookmarkFillUpDialog(var fragmentActivity: FragmentActivity) : Dialog(fragmentActivity) {


    init {
        setCancelable(false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.bookmark_fill_up_dialog)

        closeFillUpDialog()
        setUpTheme()
        getTitleTypeData()
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
        //savedItemType = DataClass.typeTitle!!.toString()


        when (DataClass.typeTitle!!.toString()) {
            "Beverages" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.beverages)
            "Cash Deposit" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.cash_deposit)
            "Fare" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.fare)
            "Food" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.food)
            "Health" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.health)
            "School Expenses" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.school_expenses)
            "Miscellaneous" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.miscellaneous)
            "Entertainment" -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.entertainment)
            else -> img_bookmark_fill_up_type_icon.setBackgroundResource(R.drawable.sample_logo_1)
        }



    }
}