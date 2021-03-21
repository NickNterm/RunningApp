package com.nicknterm.runningapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.stopwatch_lap_recycle_view_item.view.*

class StopwatchLapRecycleViewAdapter(private val items: ArrayList<StopwatchLapItem>, private val context: Context):
    RecyclerView.Adapter<StopwatchLapRecycleViewAdapter.ViewHolder>() {
    // This is the ViewHolder of the RecycleView. This holder just "holds"
    // the UI elements so we can later access them.
    // In this way you can refer a certain UI element in a certain index position
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val lapId: TextView = view.IdOfLap
        val splitTime: TextView = view.SplitTime
        val lapTime: TextView = view.LapTime
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.stopwatch_lap_recycle_view_item, parent, false))
    }

    // This function is called every time something change or you scroll to more items
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item= items[(items.size-1) - position]
        holder.lapId.text = String.format("%02d",item.getId())
        holder.splitTime.text = longToString(item.getSplitTime())
        holder.lapTime.text = longToString(item.getLapTime())
        if (item.getIsTheBest()){
            holder.lapId.setTextColor(ContextCompat.getColor(context, R.color.cyan))
            holder.splitTime.setTextColor(ContextCompat.getColor(context, R.color.cyan))
            holder.lapTime.setTextColor(ContextCompat.getColor(context, R.color.cyan))
        }else if(item.getIsTheWorst()){
            holder.lapId.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.splitTime.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.lapTime.setTextColor(ContextCompat.getColor(context, R.color.red))
        }else{
            holder.lapId.setTextColor(ContextCompat.getColor(context, R.color.textColor))
            holder.splitTime.setTextColor(ContextCompat.getColor(context, R.color.textColor))
            holder.lapTime.setTextColor(ContextCompat.getColor(context, R.color.textColor))
        }
    }

    private fun longToString(l: Long): String{
        var millis = l
        var seconds: Int = (millis / 1000).toInt()
        val minutes = seconds / 60
        seconds %= 60
        millis %= 1000
        millis /= 10
        return String.format("%d:%02d.%02d", minutes, seconds, millis)
    }

    // Its just the size of the items
    override fun getItemCount(): Int {
        return items.size
    }
}