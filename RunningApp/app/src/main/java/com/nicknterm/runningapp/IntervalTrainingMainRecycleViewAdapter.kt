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
import kotlinx.android.synthetic.main.interval_training_add_session_dialog.*
import kotlinx.android.synthetic.main.interval_training_edit_session_dialog.*
import kotlinx.android.synthetic.main.internal_training_main_recycle_view_item.view.*

class IntervalTrainingMainRecycleViewAdapter(private val items: ArrayList<IntervalTrainingItem>, private val context: Context):RecyclerView.Adapter<IntervalTrainingMainRecycleViewAdapter.ViewHolder>() {
    // This is the ViewHolder of the RecycleView. This holder just "holds"
    // the UI elements so we can later access them.
    // In this way you can refer a certain UI element in a certain index position
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.ItemTimeText
        val descriptionText: TextView = view.ItemDescriptionText
        val deleteButton: ImageView = view.DeleteButton
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.internal_training_main_recycle_view_item, parent, false))
    }

    // This function is called every time something change or you scroll to more items
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item= items[position]
        holder.timeText.text = "${item.getTime()} Seconds"
        holder.descriptionText.text = item.getDescription()
        holder.deleteButton.setOnClickListener {
            showEditDialog(position)
        }
    }

    // Its just the size of the items
    override fun getItemCount(): Int {
        return items.size
    }

    // Shows the Edit Element Dialog and controls its Buttons
    private fun showEditDialog(position: Int) {
        val editDialog = Dialog(context)
        editDialog.setContentView(R.layout.interval_training_edit_session_dialog)
        editDialog.NoEditDialogButton.setOnClickListener {
            editDialog.dismiss()
        }
        editDialog.YesEditDialogButton.setOnClickListener {
            if(editDialog.DescriptionEditTextInput.text.toString().isNotEmpty() && editDialog.TimeEditTextInput.text.toString().isNotEmpty()) {
                val activityIntervalTraining: IntervalTrainingMainActivity = context as IntervalTrainingMainActivity
                activityIntervalTraining.itemList[position] = IntervalTrainingItem(items[position].getId(),
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