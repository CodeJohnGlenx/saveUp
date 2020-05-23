package com.appsAndDawns.saveUp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.type_list_item.view.*


// TypeDataAdapter connects temporary data to CustomListViewDialog in a form of recycler view
// Each item you see on Choose Type... well this is it
// Still now, I can't understand some of the code here, but progress is being made  // I somehow understand most of it yay! ^ ~ ^
class TypeDataAdapter(
private val mDataset: Array<String>,
internal var recyclerViewItemClickListener: RecyclerViewItemClickListener
) : RecyclerView.Adapter<TypeDataAdapter.TypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): TypeViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.type_list_item, parent, false)
        return TypeViewHolder(v)

    }

    override fun onBindViewHolder(typeViewHolder: TypeViewHolder, i: Int) {
        typeViewHolder.mTextView.text = mDataset[i]

        when (mDataset[i]) {
            mDataset[0] -> typeViewHolder.mImageView.setImageResource(R.drawable.beverages)  // "Beverages"
            mDataset[1] ->  typeViewHolder.mImageView.setImageResource(R.drawable.bills)  // "Bills"
            mDataset[2] ->  typeViewHolder.mImageView.setImageResource(R.drawable.income)  // "Cash Deposit"
            mDataset[3] ->  typeViewHolder.mImageView.setImageResource(R.drawable.cosmetics)  // "Cosmetics"
            mDataset[4] ->  typeViewHolder.mImageView.setImageResource(R.drawable.entertainment)  // "Entertainment"
            mDataset[5] ->  typeViewHolder.mImageView.setImageResource(R.drawable.transportation)  // "Fare"
            mDataset[6] ->  typeViewHolder.mImageView.setImageResource(R.drawable.fitness)  // "Fitness"
            mDataset[7] ->  typeViewHolder.mImageView.setImageResource(R.drawable.food)  // "Food"
            mDataset[8]->  typeViewHolder.mImageView.setImageResource(R.drawable.health)  // "Health"
            mDataset[9] ->  typeViewHolder.mImageView.setImageResource(R.drawable.hygiene)  // "Hygiene"
            mDataset[10] ->   typeViewHolder.mImageView.setImageResource(R.drawable.miscellaneous)  // "Miscellaneous"
            mDataset[11] ->  typeViewHolder.mImageView.setImageResource(R.drawable.education) // "School Expenses"
            mDataset[12] ->  typeViewHolder.mImageView.setImageResource(R.drawable.shopping)  // "Shopping"
            mDataset[13] ->  typeViewHolder.mImageView.setImageResource(R.drawable.utilities)  // "Utilities"
            else -> typeViewHolder.mImageView.setImageResource(R.drawable.sample_logo_1)
        }



    }

    override fun getItemCount(): Int {
        return mDataset.size
    }


    inner class TypeViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val themeConfig: RealmConfiguration = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm: Realm = Realm.getInstance(themeConfig)
        var themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

        var mTextView: TextView
        var mImageView: ImageView

        init {

            // type dialog card view
            if (themeMode!!.itemType == v.context.getString(R.string.dark_mode)) {
                v.type_list_card.setCardBackgroundColor(ContextCompat.getColorStateList(v.context, R.color.list_card_state_dark_theme))
                v.textView.setTextColor(ContextCompat.getColor(v.context, R.color.white))
            } else {
                v.type_list_card.setCardBackgroundColor(ContextCompat.getColorStateList(v.context, R.color.list_card_state_light_theme))
                v.textView.setTextColor(ContextCompat.getColorStateList(v.context, R.color.list_card_text_state_light_theme))
            }

            mTextView = v.textView
            mImageView = v.type_icon
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            recyclerViewItemClickListener.clickOnItem(mDataset[this.adapterPosition])
            v.setBackgroundResource(R.color.colorPrimary)  // makes the background temporarily colorPrimary
            v.textView.setTextColor(Color.WHITE)  // makes the foreground temporarily white
        }




    }

    interface RecyclerViewItemClickListener {
        fun clickOnItem(data: String)
    }




    
}