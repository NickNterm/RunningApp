package com.nicknterm.runningapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.main_menu.*

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        StartIntervalTraining.setOnClickListener{
            startActivity(Intent(this, IntervalTrainingMainActivity::class.java))
        }
    }
}