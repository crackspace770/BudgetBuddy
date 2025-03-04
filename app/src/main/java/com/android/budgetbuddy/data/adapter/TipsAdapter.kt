package com.android.budgetbuddy.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.model.Tips

import com.bumptech.glide.Glide

class TipsAdapter(private val listCountry:ArrayList<Tips>): RecyclerView.Adapter<TipsAdapter.TipsViewHolder>()  {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_tips, parent, false)
        return TipsViewHolder(view)
    }

    override fun getItemCount(): Int = listCountry.size

    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        val (name, description, photo) = listCountry[position]

        holder.imgTips.setImageResource(photo)
        holder.tvName.text = name
        holder.tvDescription.text = description

        //holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listCountry[holder.adapterPosition]) }
    }

    class TipsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val imgTips: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName : TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Tips)
    }

}