package com.example.menu.Fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.graphics.drawable.toDrawable

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menu.Adapter.ExpendituresAdapter
import com.example.menu.Adapter.TypeDataAdapter
import com.example.menu.Data.DataClass
import com.example.menu.Dialogs.CustomListViewDialog
import com.example.menu.Dialogs.FillUpDialog
import com.example.menu.Model.Supplier
import com.example.menu.R
import kotlinx.android.synthetic.main.fragment_home.*


// all about Home Fragment
class HomeFragment : Fragment(),
    TypeDataAdapter.RecyclerViewItemClickListener {  // responsible for handling click on every item on recycler view


    var customDialog: CustomListViewDialog? = null  // this is the Choose Type dialog
    var customFillUpDialog: FillUpDialog? = null  // this is the Fill Up dialog


    val TAG = "Home Fragment"
    override fun onAttach(context: Context) {
        Log.d(TAG, "On Create")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "On Create View")


        return inflater!!.inflate(
            com.example.menu.R.layout.fragment_home,
            container,
            false
        )  // inflates the fragment_home in this home fragment

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFABClick()  // instantiates the floating action button
        setupRecyclerView()  // instantiates the recycler viewer
        displayProgressBar()


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Activity Created")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        Log.d(TAG, "On Resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "On Pause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "On Stop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(TAG, "On Destroy View")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "On Destroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "On Detach")
        super.onDetach()
    }

    private fun setupRecyclerView() {  // setting up the recycler viewer
        val layoutManager = LinearLayoutManager(context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val adapter = ExpendituresAdapter(context!!, Supplier.expenditures)
        recyclerView.adapter = adapter
    }

    override fun clickOnItem(data: String) {  // when item on custom dialog/ choose type dialog is clicked
        if (customDialog != null) {
            //customDialog!!.dismiss()
            Handler().postDelayed(
                {
                    customDialog!!.dismiss()
                },
                200
            )  // delayed action to show the item being clicked

            DataClass.typeTitle =
                data  // setting the DataClass.typeTitle's value on the value(text) of the choose type item clicked

            // responsible for showing the fill up dialog
            customFillUpDialog = FillUpDialog(activity!!)
            customFillUpDialog!!.show()
        }
    }

    fun onFABClick() {  // responsible for setting the options on choose type dialog/ custom dialog
        floatingActionButton.setOnClickListener {
            val items = arrayOf(
                "Beverages",
                "Cash Deposit",
                "Fare",
                "Food",
                "Health",
                "Miscellaneous",
                "School Expenses",
                "Entertainment"

            )


            // responsible for setting up items on the custom dialog
            val dataAdapter = TypeDataAdapter(items, this)
            customDialog = CustomListViewDialog(activity!!, dataAdapter)
            customDialog!!.show()
            customDialog!!.setCanceledOnTouchOutside(true)


        }
    }

    private fun displayProgressBar() {
        var fundValue: Double = 0.0
        var expensesValue: Double = 0.0
        var balanceValue: Double = 0.0
        var expenses: Double = 0.0
        var balances: Double = 0.0


        for (item in Supplier.expenditures) {
            if (item.type != "Cash Deposit") {
                expenses = expenses + item.price
                expensesValue = expensesValue + item.price
            } else {
                balances = balances + item.price
                fundValue = fundValue + item.price
            }
        }
        balanceValue = fundValue - expensesValue

        tv_fund_value.text = fundValue.toString()
        tv_expenses_value.text = expensesValue.toString()
        tv_balance_value.text = balanceValue.toString()

        Log.e(TAG, "$expenses")
        Log.e(TAG, "$balances")


        progressBar.progress = ((balances - expenses) / balances * 100).toInt()
        if (progressBar.progress >= 60) {
           // progressBar.progressDrawable = com.example.menu.R.drawable.custom_progress_bar_green.toDrawable()
        } else if (progressBar.progress >= 40) {
           // progressBar.progressDrawable = com.example.menu.R.drawable.custom_progress_bar_yellow.toDrawable()
        } else if (progressBar.progress >= 25) {
           // progressBar.progressDrawable = com.example.menu.R.drawable.custom_progress_bar_orange.toDrawable()
        } else {
           // progressBar.progressDrawable = com.example.menu.R.drawable.custom_progress_bar_red.toDrawable()
        }


    }

}
