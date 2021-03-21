package com.nicknterm.runningapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.interval_training_load_recycle_view_item.view.*

class IntervalTrainingLoadRecycleViewAdapter(private val items: ArrayList<String>, private val context: Context):
    RecyclerView.Adapter<IntervalTrainingLoadRecycleViewAdapter.ViewHolder>(){
    var selected:Int? = null

    // This is the ViewHolder of the RecycleView. This holder just "holds"
    // the UI elements so we can later access them.
    // In this way you can refer a certain UI element in a certain index position
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.ItemActivityNameText
        val parentLL: LinearLayout = view.parentLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.interval_training_load_recycle_view_item,parent,false))
    }

    // Its just the size of the items
    override fun getItemCount(): Int {
        return items.size
    }

    // This function is called every time something change or you scroll to more items
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item= items[position]

        holder.nameText.text = item
        if(position != selected){
            holder.parentLL.setBackgroundResource(R.color.bgSecondary)
        }
        holder.parentLL.setOnClickListener {
            selected = position
            holder.parentLL.setBackgroundResource(R.drawable.recycle_view_item_selected)
            notifyDataSetChanged()
        }
    }

}