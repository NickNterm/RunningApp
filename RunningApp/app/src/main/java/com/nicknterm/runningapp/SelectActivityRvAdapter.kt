package com.nicknterm.runningapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.saved_rv_item.view.*

class SelectActivityRvAdapter(private val items: ArrayList<String>, private val context: Context):
    RecyclerView.Adapter<SelectActivityRvAdapter.ViewHolder>(){
    var selected:Int? = null
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.ItemActivityNameText
        val parentLL: LinearLayout = view.parentLayout
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectActivityRvAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.saved_rv_item,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item= items[position]

        holder.nameText.text = item
        if(position != selected){
            holder.parentLL.setBackgroundResource(R.color.bgSecondary)
        }
        holder.parentLL.setOnClickListener {
            selected = position
            holder.parentLL.setBackgroundResource(R.drawable.rv_item_selected)
            notifyDataSetChanged()
        }
    }

}