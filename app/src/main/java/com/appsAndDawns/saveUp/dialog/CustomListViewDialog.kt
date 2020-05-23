package com.appsAndDawns.saveUp.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.custom_dialog_layout.*

// all about the custom list view dialog / choose type dialog
class CustomListViewDialog(var activity: FragmentActivity, internal var adapter: RecyclerView.Adapter<*>) : Dialog(activity),
    View.OnClickListener {
    var dialog: Dialog? = null

    private var recyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_layout)

        recyclerView = recycler_view
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = mLayoutManager
        recyclerView?.adapter = adapter
        setTheme()

        if (DataClass.tutorialHome!!.itemValue == 0.0) {
            val toast: Toast = Toast.makeText(activity, " choose your desired type", Toast.LENGTH_LONG)
            val toastView: View = toast.view
            val toastMessage = toastView.findViewById(android.R.id.message) as TextView
            toastMessage.textSize = 18f
            toast.show()
        }

    }


    override fun onClick(v: View) {
        // this was where btnDone was located before or the btnYes or No
    }


    private fun setTheme() {

        // type dialog background color
        if (themeMode!!.itemType == activity.getString(R.string.dark_mode)) {
            type_dialog.setBackgroundColor(ContextCompat.getColor(activity, R.color.dark_grey_two))
        } else {
            type_dialog.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
        }
    }



}