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
import kotlinx.android.synthetic.main.add_task.*
import kotlinx.android.synthetic.main.edit_dialog.*
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
            showDeleteDialog(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    private fun showDeleteDialog(position: Int) {
        val editDialog = Dialog(context)
        editDialog.setContentView(R.layout.edit_dialog)
        editDialog.NoEditDialogButton.setOnClickListener {
            editDialog.dismiss()
        }
        editDialog.YesEditDialogButton.setOnClickListener {
            if(editDialog.DescriptionEditTextInput.text.toString().isNotEmpty() && editDialog.TimeEditTextInput.text.toString().isNotEmpty()) {
                val activity: MainActivity = context as MainActivity
                activity.ItemList[position] = TrainItem(items[position].getId(),
                    editDialog.DescriptionEditTextInput.text.toString(),
                    editDialog.TimeEditTextInput.text.toString().toInt())
                notifyDataSetChanged()
                editDialog.dismiss()
            }else{
                if(editDialog.DescriptionEditTextInput.text.toString().isEmpty()){
                    editDialog.DescriptionTextInputLayout.error = "Please Enter Description"
                }
                if(editDialog.TimeEditTextInput.text.toString().isEmpty()){
                    editDialog.TimeTextEditInputLayout.error = "Please Enter Time"
                }
            }
        }
        editDialog.show()
    }
}