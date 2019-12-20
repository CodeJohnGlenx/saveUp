package com.example.menu.Dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.menu.R
import kotlinx.android.synthetic.main.custom_dialog_layout.*


// all about the custom list view dialog / choose type dialog
class CustomListViewDialog(var activity: FragmentActivity, internal var adapter: RecyclerView.Adapter<*>) : Dialog(activity),
    View.OnClickListener {
    var dialog: Dialog? = null

    internal var recyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_layout)

        recyclerView = recycler_view
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = mLayoutManager
        recyclerView?.adapter = adapter
    }


    override fun onClick(v: View) {
        // this was where btnDone was located before or the btnYes or No
    }
}