package com.example.menu.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.menu.R
import kotlinx.android.synthetic.main.type_list_item.view.*


// TypeDataAdapter connects temporary data to CustomListViewDialog in a form of recycler view
// Still now, I can't understand some of the code here, but progress is being made
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
            "Beverages" -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.beverages)
            "Cash Deposit" -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.cash_deposit)
            "Fare" -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.fare)
            "Food" -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.food)
            "Health" -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.health)
            "School Expenses" -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.school_expenses)
            "Miscellaneous" ->  typeViewHolder.mImageView.setBackgroundResource(R.drawable.miscellaneous)
            "Entertainment" -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.entertainment)
            else -> typeViewHolder.mImageView.setBackgroundResource(R.drawable.sample_logo_1)
        }


    }

    override fun getItemCount(): Int {
        return mDataset.size
    }


    inner class TypeViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        var mTextView: TextView
        var mImageView: ImageView

        init {
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