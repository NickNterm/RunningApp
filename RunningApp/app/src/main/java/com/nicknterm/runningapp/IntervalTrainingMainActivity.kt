package com.nicknterm.runningapp

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nicknterm.runningapp.R.id.*
import kotlinx.android.synthetic.main.interval_training_main_activity.*
import kotlinx.android.synthetic.main.interval_training_add_session.*
import kotlinx.android.synthetic.main.quit_app_dialog.*
import kotlinx.android.synthetic.main.interval_training_save_dialog.*
import kotlinx.android.synthetic.main.interval_training_load_session_dialog.*


class IntervalTrainingMainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    var itemList: ArrayList<IntervalTrainingItem> = ArrayList<IntervalTrainingItem>()
    private var mCurrentId: Int = 0
    private var intervalTrainingMainRecycleViewAdapter: IntervalTrainingMainRecycleViewAdapter? = null
    private val dbHandler: DBHandler = DBHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.MyAppTheme)
        setContentView(R.layout.interval_training_main_activity)
        setSupportActionBar(myToolBar) //set Toolbar

        val toggle = ActionBarDrawerToggle(Activity(),
            mainActivityLayout,
            myToolBar,
            R.string.nav_open,
            R.string.close_nav)
        mainActivityLayout.addDrawerListener(toggle)
        toggle.syncState() //add toggle button for the Side Navigation
        mainNavBar.setNavigationItemSelectedListener(this)
        rvItems.layoutManager = LinearLayoutManager(this) //set RecycleView layout

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvItems)// use ItemTouchHelper to th RecycleView

        // Just Show the Add Dialog
        CardViewAdd.setOnClickListener {
            showAddDialog()
        }

        // Just Show the Add Dialog
        addFloatButton.setOnClickListener{
            showAddDialog()
        }

        // Go to The ExerciseActivity and push ItemList in the Activity
        StartButton.setOnClickListener{
            val intent = Intent(this, IntervalTrainingExerciseActivity::class.java)
            intent.putExtra("TrainList", itemList)
            startActivity(intent)
        }
    }

    // This function shows the Add Button and hides the Start Button and the main RecycleViewer
    fun showAddButtons(){
        rvItems.visibility = View.GONE
        CardViewAdd.visibility = View.VISIBLE
        StartButton.visibility = View.GONE
    }

    // This function hides the Add Button and shows the Start Button and the main RecycleViewer
    private fun hideAddButtons(){
        rvItems.visibility = View.VISIBLE
        CardViewAdd.visibility = View.GONE
        StartButton.visibility = View.VISIBLE
    }

    // Basically is a OnClickListener of the Items that get clicked in the Navigation Bar
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            SaveButton -> {
                if (itemList.size > 0) {
                    showSaveDialog()
                } else {
                    Snackbar.make(SnackBarLayout,
                        "Cannot save and Empty activity",
                        Snackbar.LENGTH_LONG)
                        .setTextColor(resources.getColor((R.color.textColor)))
                        .setBackgroundTint(resources.getColor(R.color.bgSecondary))
                        .show()
                }
            }
            LoadButton -> showSelectActivityDialog()
        }
        mainActivityLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Shows the Save Dialog and controls the ClickListeners of the Buttons
    private fun showSaveDialog() {
        val saveDialog = Dialog(this)
        saveDialog.setContentView(R.layout.interval_training_save_dialog)
        saveDialog.DialogSaveButton.setOnClickListener{
            if(saveDialog.NameInputSave.text.toString().isNotEmpty()) {
                for (item in itemList){
                    dbHandler.saveIntervalTrainingItem(item, saveDialog.NameInputSave.text.toString())
                    Snackbar.make(SnackBarLayout, "Saved Successfully", Snackbar.LENGTH_LONG)
                        .setTextColor(resources.getColor((R.color.textColor)))
                        .setBackgroundTint(resources.getColor(R.color.bgSecondary))
                        .show()
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

    // Shows the Select Activity Dialog and controls the ClickListeners of the Buttons
    private fun showSelectActivityDialog(){
        val selectDialog = Dialog(this)
        selectDialog.setContentView(R.layout.interval_training_load_session_dialog)
        selectDialog.SelectActivityRv.layoutManager = LinearLayoutManager(this)
        var selectDialogAdapter: IntervalTrainingLoadRecycleViewAdapter
        val nameList = dbHandler.intervalTrainingSessionNames()
        if(nameList.isNotEmpty()) {
            val list = ArrayList<String>()
            for(item in nameList){
               if(!list.contains(item)){
                   list.add(item)
               }
            }
            selectDialogAdapter = IntervalTrainingLoadRecycleViewAdapter(list, this)
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
                selectDialogAdapter = selectDialog.SelectActivityRv.adapter as IntervalTrainingLoadRecycleViewAdapter
                if(selectDialogAdapter.selected!= null) {
                    itemList.clear()
                    itemList = dbHandler.intervalTrainingItemsInSession(list[selectDialogAdapter.selected!!])
                    intervalTrainingMainRecycleViewAdapter = IntervalTrainingMainRecycleViewAdapter(itemList, this)
                    rvItems.adapter = intervalTrainingMainRecycleViewAdapter
                    hideAddButtons()
                    intervalTrainingMainRecycleViewAdapter!!.notifyDataSetChanged()
                    selectDialog.dismiss()
                }
            }
        }
        selectDialog.DialogShowSavedCancelButton.setOnClickListener {
            selectDialog.dismiss()
        }
        selectDialog.show()
    }

    // The function that controls the functionality of BackPress
    override fun onBackPressed() {
        showQuitDialog()
    }

    // Shows the Quit From the App Dialog and controls the ClickListeners of the Buttons
    private fun showQuitDialog() {
        val quitDialog = Dialog(this)
        quitDialog.setContentView(R.layout.quit_app_dialog)
        quitDialog.YesQuitAppButton.setOnClickListener{
            finish()
        }
        quitDialog.NoQuitAppButton.setOnClickListener {
            quitDialog.dismiss()
        }
        quitDialog.show()
    }

    // Shows the Add Activity Dialog and controls the ClickListeners of the Buttons
    private fun showAddDialog() {
        val addDialog = Dialog(this)
        addDialog.setContentView(R.layout.interval_training_add_session)
        addDialog.cancel_button_add_dialog.setOnClickListener{
            addDialog.dismiss()
        }
        addDialog.add_button_add_dialog.setOnClickListener{
            if(addDialog.DescriptionTextInput.text.toString().isNotEmpty() && addDialog.TimeTextInput.text.toString().isNotEmpty()) {
                val newItem = IntervalTrainingItem(mCurrentId,
                    addDialog.DescriptionTextInput.text.toString(),
                    addDialog.TimeTextInput.text.toString().toInt())
                mCurrentId++
                itemList.add(newItem)
                if (intervalTrainingMainRecycleViewAdapter != null) {
                    intervalTrainingMainRecycleViewAdapter!!.notifyDataSetChanged()
                } else {
                    intervalTrainingMainRecycleViewAdapter = IntervalTrainingMainRecycleViewAdapter(itemList, this)
                    rvItems.adapter = intervalTrainingMainRecycleViewAdapter
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

    // This object controls the drag and drop, the swipe functionality of the main RecycleView
    private val itemTouchHelperCallback = object: ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
        ): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            intervalTrainingMainRecycleViewAdapter!!.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            itemList[viewHolder.adapterPosition] = itemList[target.adapterPosition].also {itemList[target.adapterPosition] =  itemList[viewHolder.adapterPosition]}
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val removedItem = itemList[viewHolder.adapterPosition]
            val position = viewHolder.adapterPosition
            itemList.removeAt(position)
            intervalTrainingMainRecycleViewAdapter!!.notifyDataSetChanged()
            Snackbar.make(SnackBarLayout, "Item Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    itemList.add(position, removedItem)
                    intervalTrainingMainRecycleViewAdapter!!.notifyDataSetChanged()
                    hideAddButtons()
                }
                .setTextColor(resources.getColor((R.color.textColor)))
                .setBackgroundTint(resources.getColor(R.color.bgSecondary))
                .setActionTextColor(resources.getColor(R.color.cyan))
                .show()
            if(itemList.size == 0){
                showAddButtons()
            }
        }
    }
}