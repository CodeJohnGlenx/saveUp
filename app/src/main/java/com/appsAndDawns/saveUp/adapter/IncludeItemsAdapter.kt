package com.appsAndDawns.saveUp.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appsAndDawns.saveUp.fragment.SettingsFragment
import com.appsAndDawns.saveUp.model.IncludeItemsModel
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.confirmation_dialog.*
import kotlinx.android.synthetic.main.selection_list_item.view.*


// About Include Items in Settings
class IncludeItemsAdapter(val context: Context, private val includeItemsModel: List<IncludeItemsModel>) : RecyclerView.Adapter<IncludeItemsAdapter.MyViewHolder>() {
    val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm = Realm.getInstance(themeConfig)
    var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
    private val includeItemsConfig: RealmConfiguration = RealmConfiguration.Builder().name("includeItems.realm").build()
    val includeItemsRealm: Realm = Realm.getInstance(includeItemsConfig)
    var includeItems = includeItemsRealm.where(ItemModel::class.java).equalTo("itemId", "includeItems").findFirst()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.selection_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return includeItemsModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val includeItemsModel = includeItemsModel[position]
        holder.setData(includeItemsModel, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentSelection : IncludeItemsModel? = null
        private var currentPosition : Int = 0

        init {
            itemView.setOnClickListener {
                val confirmationMessage: String
                val excludeBalanceValue: String

                when(itemView.tv_selection_title.text) {
                    "within this day" -> {
                        confirmationMessage = "Are you sure you want to include items within this day?"
                        excludeBalanceValue = "within this day"
                    }
                    "within three days" -> {
                        confirmationMessage = "Are you sure you want to include items within three days?"
                        excludeBalanceValue = "within three days"
                    }
                    "within this week" ->  {
                        confirmationMessage = "Are you sure you want to include items within this week?"
                        excludeBalanceValue = "within this week"
                    }
                    "within two weeks" ->  {
                        confirmationMessage = "Are you sure you want to include items within two weeks?"
                        excludeBalanceValue = "within two weeks"
                    }
                    "within this month" ->  {
                        confirmationMessage = "Are you sure you want to include items within this month?"
                        excludeBalanceValue = "within this month"
                    }
                    "within three months" ->  {
                        confirmationMessage = "Are you sure you want to include items within three months?"
                        excludeBalanceValue = "within three months"
                    }
                    "within six months" ->  {
                        confirmationMessage = "Are you sure you want to include items within six months?"
                        excludeBalanceValue = "within six months"
                    }
                    "within this year" ->  {
                        confirmationMessage = "Are you sure you want to include items within this year?"
                        excludeBalanceValue = "within this year"
                    }
                    "include all items" ->  {
                        confirmationMessage = "Are you sure you want to include all items?"
                        excludeBalanceValue = "include all items"
                    }
                    else -> {
                        confirmationMessage = "Error occurred"
                        excludeBalanceValue = "error"
                    }

                }


                // confirmation to change include items options selected
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.confirmation_dialog)
                dialog.tv_confirmation_text_confirmation_dialog.text = confirmationMessage
                dialog.setCancelable(true)
                dialog.show()

                dialog.tv_confirmation_no_confirmation_dialog.setOnClickListener{
                        dialog.dismiss()
                }

                dialog.tv_confirmation_yes_confirmation_dialog.setOnClickListener {
                    Toast.makeText(context, "Changes successfully made", Toast.LENGTH_SHORT).show()
                    includeItemsRealm.beginTransaction()
                    includeItems!!.itemTitle = excludeBalanceValue
                    includeItems!!.itemType = excludeBalanceValue
                    includeItemsRealm.commitTransaction()
                    dialog.dismiss()
                    showSettingsFragment()
                }

                if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                    dialog.tv_confirmation_confirmation_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))
                    dialog.relative_layout_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                    dialog.tv_confirmation_text_confirmation_dialog.setTextColor(ContextCompat.getColor(context, R.color.white))
                    dialog.tv_confirmation_text_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                    dialog.tv_confirmation_no_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                    dialog.tv_confirmation_yes_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_three))
                }
            }


        }
        fun setData(includeItemsModel: IncludeItemsModel?, position: Int) {
            includeItemsModel?.let {
                itemView.tv_selection_title.text = includeItemsModel.selectionString
                itemView.img_settings_selection.setImageResource(includeItemsModel.image)


            }

            if (themeMode!!.itemType == context.getString(R.string.dark_mode)) {
                itemView.tv_selection_title.setTextColor(ContextCompat.getColor(context, R.color.white))
                itemView.img_settings_selection.setColorFilter(ContextCompat.getColor(context, R.color.white))
                itemView.selection_card_view.setCardBackgroundColor(ContextCompat.getColorStateList(context, R.color.settings_card_view_dark_mode_state))
            }


            this.currentSelection = includeItemsModel
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