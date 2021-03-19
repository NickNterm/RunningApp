package com.nicknterm.runningapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_task.*
import kotlinx.android.synthetic.main.delete_train.*
import kotlinx.android.synthetic.main.rv_item.view.*

class RvAdapter(private val items: ArrayList<TrainItem>, private val context: Context):RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.ItemTimeText
        val descriptionText: TextView = view.ItemDescriptionText
        val deleteButton: ImageView = view.DeleteButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item= items[position]
        holder.timeText.text = "${item.getTime()} Seconds"
        holder.descriptionText.text = item.getDescription()
        holder.deleteButton.setOnClickListener {
            showDeleteDialog(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    private fun showDeleteDialog(item: TrainItem) {
        val deleteDialog = Dialog(context)
        deleteDialog.setContentView(R.layout.delete_train)
        deleteDialog.NoDeleteButton.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.YesDeleteButton.setOnClickListener {
            if(items.size == 1){
                val activity: MainActivity = context as MainActivity
                activity.showAddButtons()
            }
            items.remove(item)
            notifyDataSetChanged()
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }
}