package com.example.menu.Adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.menu.Fragments.SettingsFragment
import com.example.menu.Fragments.TrashFragment
import com.example.menu.Model.SettingsAutomaticallyDeleteSelectionModel
import com.example.menu.R
import com.example.menu.RealmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.confirmation_dialog.*
import kotlinx.android.synthetic.main.selection_list_item.view.*

class SettingsSelectionAdapter(val context: Context, private val settingsAutomaticallyDeleteSelectionModels: List<SettingsAutomaticallyDeleteSelectionModel>) : RecyclerView.Adapter<SettingsSelectionAdapter.MyViewHolder>() {
    val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    val deleteDateSelectionConfig = RealmConfiguration.Builder().name("deleteDateSelection.realm").build()
    val deleteDateSelectionRealm = Realm.getInstance(deleteDateSelectionConfig)
    var deleteDateSelection = deleteDateSelectionRealm.where(ItemModel::class.java).equalTo("itemId", "deleteDateSelection").findFirst()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.selection_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return settingsAutomaticallyDeleteSelectionModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val settingsAutomaticallyDeleteSelectionModel = settingsAutomaticallyDeleteSelectionModels[position]
        holder.setData(settingsAutomaticallyDeleteSelectionModel, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var currentSelection : SettingsAutomaticallyDeleteSelectionModel? = null
        var currentPosition : Int = 0

        init {
            itemView.setOnClickListener {
                var confirmationMessage: String = "Hello"
                var deleteDateSelectionValue: String = "never"

                when(itemView.tv_selection_title.text) {
                    "over this day" -> {
                        confirmationMessage = "Are you sure you want to automatically delete data over this day?"
                        deleteDateSelectionValue = "over this day"
                    }
                    "over three days" -> {
                        confirmationMessage = "Are you sure you want to automatically delete data over three days?"
                        deleteDateSelectionValue = "over three days"
                    }
                    "over this week" ->  {
                        confirmationMessage = "Are you sure you want to automatically delete data over this week?"
                        deleteDateSelectionValue = "over this week"
                    }
                    "over two weeks" ->  {
                        confirmationMessage = "Are you sure you want to automatically delete data over two weeks?"
                        deleteDateSelectionValue = "over two weeks"
                    }
                    "over this month" ->  {
                        confirmationMessage = "Are you sure you want to automatically delete data over this month?"
                        deleteDateSelectionValue = "over this month"
                    }
                    "over three months" ->  {
                        confirmationMessage = "Are you sure you want to automatically delete data over three months?"
                        deleteDateSelectionValue = "over three months"
                    }
                    "over six months" ->  {
                        confirmationMessage = "Are you sure you want to automatically delete data over six months?"
                        deleteDateSelectionValue = "over six months"
                    }
                    "over this year" ->  {
                        confirmationMessage = "Are you sure you want to automatically delete data over this year?"
                        deleteDateSelectionValue = "over this year"
                    }
                    "never" ->  {
                        confirmationMessage = "Are you sure you want to don't want to automatically delete data?"
                        deleteDateSelectionValue = "never"
                    }
                    else -> {
                        confirmationMessage = "Error occurred"
                        deleteDateSelectionValue = "error"
                    }

                }


                var dialog = Dialog(context)
                dialog.setContentView(R.layout.confirmation_dialog)
                dialog.tv_confirmation_text_confirmation_dialog.text = confirmationMessage
                dialog.setCancelable(true)
                dialog.show()

                dialog.tv_confirmation_no_confirmation_dialog.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        dialog.dismiss()
                    }
                })

                dialog.tv_confirmation_yes_confirmation_dialog.setOnClickListener(object  : View.OnClickListener {
                    override fun onClick(view: View) {
                        Toast.makeText(context, "Changes successfully made", Toast.LENGTH_SHORT).show()
                        deleteDateSelectionRealm.beginTransaction()
                        deleteDateSelection!!.itemTitle = deleteDateSelectionValue
                        deleteDateSelection!!.itemType = deleteDateSelectionValue
                        deleteDateSelectionRealm.commitTransaction()
                        dialog.dismiss()
                        showSettingsFragment()
                    }
                })

                if (themeMode!!.itemType == "Dark Mode") {
                    dialog.tv_confirmation_confirmation_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))
                    dialog.relative_layout_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                    dialog.tv_confirmation_text_confirmation_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))
                    dialog.tv_confirmation_text_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                    dialog.tv_confirmation_no_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                    dialog.tv_confirmation_yes_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                }
            }


        }
        fun setData(settingsAutomaticallyDeleteSelectionModel: SettingsAutomaticallyDeleteSelectionModel?, position: Int) {
            settingsAutomaticallyDeleteSelectionModel?.let {
                itemView.tv_selection_title.text = settingsAutomaticallyDeleteSelectionModel.selectionString
                itemView.img_settings_selection.setImageResource(settingsAutomaticallyDeleteSelectionModel.image)


            }

            if (themeMode!!.itemType == "Dark Mode") {
                itemView.tv_selection_title.setTextColor(ContextCompat.getColor(context, R.color.white))
                itemView.img_settings_selection.setColorFilter(ContextCompat.getColor(context, R.color.white))
                itemView.selection_card_view.setCardBackgroundColor(ContextCompat.getColorStateList(context, R.color.settings_card_view_dark_mode_state))
            }


            this.currentSelection = settingsAutomaticallyDeleteSelectionModel
            this.currentPosition = position
        }

    }

    private fun showSettingsFragment() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}