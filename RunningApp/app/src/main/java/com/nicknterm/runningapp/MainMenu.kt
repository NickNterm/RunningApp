package com.nicknterm.runningapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.main_menu.*
import kotlinx.android.synthetic.main.quit_app_dialog.*

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        setSupportActionBar(MainMenuToolbar)
        StartIntervalTraining.setOnClickListener{
            startActivity(Intent(this, IntervalTrainingMainActivity::class.java))
        }
        StartStopwatch.setOnClickListener {
            startActivity(Intent(this, Stopwatch::class.java))
        }
    }

    // The function that controls the functionality of BackPress
    override fun onBackPressed() {
        showQuitDialog()
    }

    // Shows the Quit From the App Dialog and controls the ClickListeners of the Buttons
    private fun showQuitDialog() {
        /*MaterialAlertDialogBuilder(this)
            .setTitle("Quit?")
            .setMessage("Are you sure you want to leave the app?")
            .setNegativeButton("leave") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Stay") { dialog, which ->
                finish()
            }
            .show()
*/
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
}