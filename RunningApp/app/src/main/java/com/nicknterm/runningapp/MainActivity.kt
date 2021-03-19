package com.nicknterm.runningapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_task.*
import kotlinx.android.synthetic.main.add_task.DescriptionTextInput
import kotlinx.android.synthetic.main.quit_app.*
import com.nicknterm.runningapp.R.id.*
import kotlinx.android.synthetic.main.save_dialog.*
import kotlinx.android.synthetic.main.show_saved_dialog.*


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private var ItemList: ArrayList<TrainItem> = ArrayList<TrainItem>()
    private var mCurrentId: Int = 0
    private var rvAdapter: RvAdapter? = null
    private val dbHandler: DBHandler = DBHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(myToolBar)
        val toggle = ActionBarDrawerToggle(Activity(), mainActivityLayout, myToolBar, R.string.nav_open,R.string.close_nav)
        mainActivityLayout.addDrawerListener(toggle)
        toggle.syncState()
        mainNavBar.setNavigationItemSelectedListener(this)
        rvItems.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvItems)

        CardViewAdd.setOnClickListener {
            showAddDialog()
        }

        addFloatButton.setOnClickListener{
            showAddDialog()
        }

        StartButton.setOnClickListener{
            val intent = Intent(this, ExerciseActivity::class.java)
            intent.putExtra("TrainList", ItemList)
            startActivity(intent)
        }
    }

    fun showAddButtons(){
        rvItems.visibility = View.GONE
        CardViewAdd.visibility = View.VISIBLE
        StartButton.visibility = View.GONE
    }

    private fun hideAddButtons(){
        rvItems.visibility = View.VISIBLE
        CardViewAdd.visibility = View.GONE
        StartButton.visibility = View.VISIBLE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            SaveButton -> {
                if (ItemList.size > 0) {
                    showSaveDialog()
                } else {
                    Toast.makeText(this, "Add a task first", Toast.LENGTH_SHORT).show()
                }
            }
            LoadButton -> showSelectActivityDialog()
        }
        mainActivityLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showSaveDialog() {
        val saveDialog = Dialog(this)
        saveDialog.setContentView(R.layout.save_dialog)
        saveDialog.DialogSaveButton.setOnClickListener{
            if(saveDialog.NameInputSave.text.toString().isNotEmpty()) {
                for (item in ItemList){
                    dbHandler.addTrainTimer(item, saveDialog.NameInputSave.text.toString())
                    Toast.makeText(this,"Saved Successfully",Toast.LENGTH_SHORT).show()
                }
                saveDialog.dismiss()
            }else{
                saveDialog.NameInputSave.error = "Please Enter Name"
            }
        }
        saveDialog.DialogCancelButton.setOnClickListener {
            saveDialog.dismiss()
        }
        saveDialog.show()
    }

    private fun showSelectActivityDialog(){
        val selectDialog = Dialog(this)
        selectDialog.setContentView(R.layout.show_saved_dialog)
        selectDialog.SelectActivityRv.layoutManager = LinearLayoutManager(this)
        var selectDialogAdapter: SelectActivityRvAdapter
        val nameList = dbHandler.readActivityNames()
        if(nameList.isNotEmpty()) {
            val list = ArrayList<String>()
            for(item in nameList){
               if(!list.contains(item)){
                   list.add(item)
               }
            }
            selectDialogAdapter = SelectActivityRvAdapter(list, this)
            selectDialog.SelectActivityRv.adapter = selectDialogAdapter
        }else{
            selectDialog.NoWorkoutText.visibility = View.VISIBLE
            selectDialog.SelectActivityRv.visibility = View.GONE
        }
        selectDialog.DialogShowSavedSelectButton.setOnClickListener {
            if(nameList.isNotEmpty()) {
                val list = ArrayList<String>()
                for(item in nameList){
                    if(!list.contains(item)){
                        list.add(item)
                    }
                }
                selectDialogAdapter = selectDialog.SelectActivityRv.adapter as SelectActivityRvAdapter
                if(selectDialogAdapter.selected!= null) {
                    ItemList.clear()
                    ItemList = dbHandler.readItems(list[selectDialogAdapter.selected!!])
                    rvAdapter = RvAdapter(ItemList, this)
                    rvItems.adapter = rvAdapter
                    hideAddButtons()
                    rvAdapter!!.notifyDataSetChanged()
                    selectDialog.dismiss()
                }
            }
        }
        selectDialog.DialogShowSavedCancelButton.setOnClickListener {
            selectDialog.dismiss()
        }
        selectDialog.show()
    }

    override fun onBackPressed() {
        showQuitDialog()
    }

    private fun showQuitDialog() {
        val quitDialog = Dialog(this)
        quitDialog.setContentView(R.layout.quit_app)
        quitDialog.YesQuitAppButton.setOnClickListener{
            finish()
        }
        quitDialog.NoQuitAppButton.setOnClickListener {
            quitDialog.dismiss()
        }
        quitDialog.show()
    }

    private fun showAddDialog() {
        val addDialog = Dialog(this)
        addDialog.setContentView(R.layout.add_task)
        addDialog.cancel_button_add_dialog.setOnClickListener{
            addDialog.dismiss()
        }
        addDialog.add_button_add_dialog.setOnClickListener{
            if(addDialog.DescriptionTextInput.text.toString().isNotEmpty() && addDialog.TimeTextInput.text.toString().isNotEmpty()) {
                val newItem = TrainItem(mCurrentId, addDialog.DescriptionTextInput.text.toString(), addDialog.TimeTextInput.text.toString().toInt())
                mCurrentId++
                ItemList.add(newItem)
                if (rvAdapter != null) {
                    rvAdapter!!.notifyDataSetChanged()
                } else {
                    rvAdapter = RvAdapter(ItemList, this)
                    rvItems.adapter = rvAdapter
                }
                hideAddButtons()
                addDialog.dismiss()
            }else{
                if(addDialog.DescriptionTextInput.text.toString().isEmpty()){
                    addDialog.DescriptionTextInputLayout.error = "Please Enter Description"
                }
                if(addDialog.TimeTextInput.text.toString().isEmpty()){
                    addDialog.TimeTextInputLayout.error = "Please Enter Time"
                }
            }
        }
        addDialog.show()
    }

    fun removeFromMainRV(adapterPosition: Int) {
        ItemList.removeAt(adapterPosition)
        rvAdapter!!.notifyDataSetChanged()
    }

    private val itemTouchHelperCallback = object: ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Specify the directions of movement
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Notify your adapter that an item is moved from x position to y position
            rvAdapter!!.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            // true: if you want to start dragging on long press
            // false: if you want to handle it yourself
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            ItemList.removeAt(viewHolder.adapterPosition)
            rvAdapter!!.notifyDataSetChanged()
            if(ItemList.size == 0){
                showAddButtons()
            }
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            // Hanlde action state changes
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            // Called by the ItemTouchHelper when the user interaction with an element is over and it also completed its animation
            // This is a good place to send update to your backend about changes
        }
    }
}