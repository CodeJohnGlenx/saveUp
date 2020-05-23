package com.appsAndDawns.saveUp.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsAndDawns.saveUp.adapter.RemovedExpendituresAdapter
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.model.RemovedExpenditure
import com.appsAndDawns.saveUp.model.RemovedSupplier
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.confirmation_dialog.*
import kotlinx.android.synthetic.main.fragment_trash.*
import kotlinx.android.synthetic.main.fragment_trash.recyclerView
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener


// all about trash fragment
class TrashFragment : Fragment() {
    var config: RealmConfiguration? = RealmConfiguration.Builder().name("removedItems.realm").build()
    var realm: Realm? = Realm.getInstance(config!!)
    //private val logTag = "Trash Fragment"
    val themeConfig: RealmConfiguration? = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm? = Realm.getInstance(themeConfig!!)
    var themeMode = themeRealm!!.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()


    private lateinit var showcaseGuide: GuideView
    lateinit var showcaseBuilder: GuideView.Builder

    /*
    override fun onAttach(context: Context) {
        Log.d(logTag, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Log.d(logTag, "On Create")
        //Realm.init(activity!!)
        super.onCreate(savedInstanceState)
    }

     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Log.d(logTag, "On Create View")
        return inflater.inflate(R.layout.fragment_trash, container,false)  // inflate the fragment_trash on xml in this trash_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onDeleteAllFABClick()
        setupRecyclerView()
        setTheme()
        doShowcase()

    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Activity Created")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        Log.d(logTag, "On Resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(logTag, "On Pause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(logTag, "On Stop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(logTag, "On Destroy View")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(logTag, "On Destroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(logTag, "On Detach")
        super.onDetach()
    }
     */

    private fun setupRecyclerView() {  // setting up the recycler viewer  // get all removed items
        val layoutManager = LinearLayoutManager(context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        RemovedSupplier.removedExpenditures.clear()
        val allItems = realm!!.where(ItemModel::class.java).findAll()
        allItems.forEach { thisItem ->
            RemovedSupplier.removedExpenditures.add(0, RemovedExpenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
        }

        val adapter = RemovedExpendituresAdapter(context!!, RemovedSupplier.removedExpenditures)
        recyclerView.adapter = adapter
    }

    private fun onDeleteAllFABClick() {  // opens confirmation dialog if user wants to delete all removedItems

        floatingActionButtonDeleteAll.setOnClickListener {
            val dialog = Dialog(activity!!)
            dialog.setContentView(R.layout.confirmation_dialog)
            dialog.setCancelable(true)
            dialog.show()

            dialog.tv_confirmation_text_confirmation_dialog.text = "Are you sure you want to delete all items in Trash?"


            dialog.tv_confirmation_no_confirmation_dialog.setOnClickListener { dialog.cancel() }

            dialog.tv_confirmation_yes_confirmation_dialog.setOnClickListener {  // delete all removedItems
                val allItems = realm!!.where(ItemModel::class.java).findAll()
                allItems.forEach { item ->
                    realm!!.beginTransaction()
                    item.deleteFromRealm()
                    realm!!.commitTransaction()
                }
                Toast.makeText(activity!!, "All Items Deleted", Toast.LENGTH_SHORT).show()
                dialog.cancel()
                showTrashFragment()

            }

            if (themeMode!!.itemType == getString(R.string.dark_mode)) {
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
        if (themeMode!!.itemType == getString(R.string.dark_mode)){
            fragment_trash_relative_layout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))
        }
    }


    private fun doShowcase() {  // show trash tutorial
        if (DataClass.tutorialTrash!!.itemValue == 0.0) {
            showcaseBuilder = GuideView.Builder(activity)
                .setTitle("Trash")
                .setContentText("Shows the removed items. \n" +
                        "Pressing will show the removed item's info; \n" +
                        "Long Pressing will allow you to choose between \n" +
                        "viewing, restoring, or deleting the item permanently.")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(trash_view)
                .setGuideListener(object : GuideListener {
                    override fun onDismiss(view: View) {
                        when (view.id) {
                            R.id.trash_view ->  {
                                showcaseBuilder.setTargetView(floatingActionButtonDeleteAll)
                                    .setTitle("Delete All")
                                    .setDismissType(DismissType.anywhere)
                                    .setContentText("Deletes all the removed items permanently.")
                                    .build()
                                    .show()
                            }
                            R.id.floatingActionButtonDeleteAll -> {
                                DataClass.tutorialRealm.beginTransaction()
                                DataClass.tutorialTrash!!.itemValue = 0.1
                                DataClass.tutorialRealm.commitTransaction()
                                return
                            }
                        }
                    }
                })
            showcaseGuide = showcaseBuilder.build()
            showcaseGuide.show()
        }
    }

}