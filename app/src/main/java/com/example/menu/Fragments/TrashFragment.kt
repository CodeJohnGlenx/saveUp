package com.example.menu.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menu.Adapter.RemovedExpendituresAdapter
import com.example.menu.Model.RemovedExpenditure
import com.example.menu.Model.RemovedSupplier
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.confirmation_dialog.*
import kotlinx.android.synthetic.main.fragment_trash.*
import kotlinx.android.synthetic.main.fragment_trash.recyclerView


// all about trash fragmemt
class TrashFragment : Fragment() {
    var config = RealmConfiguration.Builder().name("removedItems.realm").build()
    var realm = Realm.getInstance(config)
    val TAG = "Trash Fragment"
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

    override fun onAttach(context: Context) {
        Log.d(TAG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        Realm.init(activity!!)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "On Create View")
        return inflater.inflate(R.layout.fragment_trash, container,false)  // inflate the fragment_trash on xml in this trash_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onDeleteAllFABClick()
        setupRecyclerView()
        setTheme()

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

        RemovedSupplier.removedExpenditures.clear()
        val allItems = realm.where(ItemModel::class.java).findAll()
        allItems.forEach { thisItem ->
            RemovedSupplier.removedExpenditures.add(0, RemovedExpenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
        }

        val adapter = RemovedExpendituresAdapter(context!!, RemovedSupplier.removedExpenditures)
        recyclerView.adapter = adapter
    }

    private fun onDeleteAllFABClick() {

        floatingActionButtonDeleteAll.setOnClickListener {
            val dialog = Dialog(activity!!)
            dialog.setContentView(R.layout.confirmation_dialog)
            dialog.setCancelable(true)
            dialog.show()

            dialog.tv_confirmation_no_confirmation_dialog.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    dialog.cancel()
                }
            })

            dialog.tv_confirmation_yes_confirmation_dialog.setOnClickListener(object  : View.OnClickListener {
                override fun onClick(view: View) {
                    val allItems = realm.where(ItemModel::class.java).findAll()
                    allItems.forEach { item ->
                        realm.beginTransaction()
                        item.deleteFromRealm()
                        realm.commitTransaction()
                        showTrashFragment()
                    }
                    Toast.makeText(activity!!, "All Items Deleted", Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                }
            })

            if (themeMode!!.itemType == "Dark Mode") {
                dialog.tv_confirmation_confirmation_dialog.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                dialog.relative_layout_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                dialog.tv_confirmation_text_confirmation_dialog.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                dialog.tv_confirmation_text_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                dialog.tv_confirmation_no_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
                dialog.tv_confirmation_yes_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_three))
            }
        }
    }

    private fun showTrashFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = TrashFragment()

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setTheme() {
        if (themeMode!!.itemType == "Dark Mode"){
            fragment_trash_relative_layout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))
        }
    }

}