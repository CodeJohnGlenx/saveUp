package com.example.menu.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menu.Adapter.ExpendituresAdapter
import com.example.menu.Adapter.RemovedExpendituresAdapter
import com.example.menu.Model.Expenditure
import com.example.menu.Model.RemovedExpenditure
import com.example.menu.Model.RemovedSupplier
import com.example.menu.Model.Supplier
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_trash.*
import kotlinx.android.synthetic.main.fragment_trash.recyclerView


// all about trash fragmemt
class TrashFragment : Fragment() {
    var config = RealmConfiguration.Builder().name("removedItems.realm").build()
    var realm = Realm.getInstance(config)
    val TAG = "Trash Fragment"
    override fun onAttach(context: Context) {
        Log.d(TAG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "On Create View")
        return inflater!!.inflate(R.layout.fragment_trash, container,false)  // inflate the fragment_trash on xml in this trash_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onDeleteAllFABClick()
        setupRecyclerView()

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

    fun onDeleteAllFABClick() {
        floatingActionButtonDeleteAll.setOnClickListener {
            AlertDialog.Builder(context!!).setTitle("Confirmation")
                ?.setMessage("Are you sure you want to delete all the items in this trash permanently?")
                ?.setPositiveButton("Yes", { dialog, which ->
                    val allItems = realm.where(ItemModel::class.java).findAll()
                    allItems.forEach { item ->
                        realm.beginTransaction()
                        item.deleteFromRealm()
                        realm.commitTransaction()
                        showTrashFragment()
                    }
                    Toast.makeText(context, "All Items Deleted", Toast.LENGTH_SHORT).show()
                })
                ?.setNegativeButton("No", { dialog, which ->  })
                ?.create()
                ?.show()
        }
    }

    private fun showTrashFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = TrashFragment()

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }






}