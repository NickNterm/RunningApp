package com.nicknterm.runningapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.quit_train.*


class ExerciseActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var timer: CountDownTimer? = null
    private var trainList: ArrayList<TrainItem> = ArrayList()
    private var pauseSecond: Int = 0
    private var isPaused: Boolean = false
    private var position: Int = 0
    private var canPress: Boolean = false
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        trainList = intent.getParcelableArrayListExtra<TrainItem>("TrainList") as ArrayList<TrainItem>

        setTimerFor(position)

        buttonsDisabled()

        player = MediaPlayer.create(this, R.raw.ring)

        LockButton.setOnLongClickListener {
            if(!canPress) {
                buttonsEnabled()
            }else{
                buttonsDisabled()
            }
            return@setOnLongClickListener true
        }

        SkipButton.setOnClickListener{
            if(canPress) {
                if (position < trainList.size - 1) {
                    position++
                    setTimerFor(position)
                    TimerPausedProgressBar.visibility = View.GONE
                    TimerProgressBar.visibility = View.VISIBLE
                    TimerProgressBar.max = TimerProgressBar.max
                    TimerProgressBar.progress = TimerPausedProgressBar.progress
                    pauseSecond = 0
                    buttonsDisabled()
                    ResumeButton.visibility = View.GONE
                    PauseButton.visibility = View.VISIBLE
                    isPaused = false
                } else {
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    intent.putExtra("TrainList", trainList)
                    startActivity(intent)
                    finish()
                }
                buttonsDisabled()
            }
        }

        PauseButton.setOnClickListener {
            if(canPress) {
                if (timer != null) {
                    TimerPausedProgressBar.visibility = View.VISIBLE
                    TimerProgressBar.visibility = View.GONE
                    TimerPausedProgressBar.max = TimerProgressBar.max
                    TimerPausedProgressBar.progress = TimerProgressBar.progress
                    pauseSecond = TimerProgressBar.progress
                    ResumeButton.visibility = View.VISIBLE
                    PauseButton.visibility = View.GONE
                    isPaused = true
                }
            }
        }

        ResumeButton.setOnClickListener {
            if(canPress) {
                if (timer != null) {
                    TimerPausedProgressBar.visibility = View.GONE
                    TimerProgressBar.visibility = View.VISIBLE
                    TimerProgressBar.max = TimerPausedProgressBar.max
                    TimerProgressBar.progress = TimerPausedProgressBar.progress
                    pauseSecond = TimerProgressBar.progress
                    isPaused = false
                    setTimerFor(position, pauseSecond)
                    ResumeButton.visibility = View.GONE
                    PauseButton.visibility = View.VISIBLE
                }
                buttonsDisabled()
            }
        }
    }

    private fun buttonsEnabled(){
        LockButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        SkipButton.setBackgroundResource(R.drawable.text_view_ripple_button)
        PauseButton.setBackgroundResource(R.drawable.text_view_ripple_button)
        ResumeButton.setBackgroundResource(R.drawable.text_view_ripple_button)
        canPress = true
    }

    private fun buttonsDisabled(){
        LockButton.setBackgroundResource(R.drawable.text_view_ripple_button)
        SkipButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        PauseButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        ResumeButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        canPress = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player!!.stop()
        timer!!.cancel()
    }

    override fun onBackPressed() {
        showQuitDialog()
    }

    private fun showQuitDialog(){
        val quitDialog = Dialog(this)
        quitDialog.setContentView(R.layout.quit_train)

        quitDialog.NoQuitButton.setOnClickListener{
            quitDialog.dismiss()
        }

        quitDialog.YesQuitButton.setOnClickListener {
            finish()
        }

        quitDialog.show()
    }

    private fun startTimer(time: Int, progressPar: Int = 0) {
        var progress = progressPar
        TimerProgressBar.max = time
        timer = object : CountDownTimer(((time-progress) * 1000).toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if(progressPar >= 0) {
                    if (!isPaused) {
                        progress++
                        TimerProgressBar.progress = progress
                        if ((time - progress) % 60 < 10) {
                            TimerText.text = "${(time - progress) / 60}:0${(time - progress) % 60}"
                        } else {
                            TimerText.text = "${(time - progress) / 60}:${(time - progress) % 60}"
                        }
                    } else {
                        cancel()
                    }
                }
            }

            override fun onFinish() {
                if (progressPar >= 0) {
                    if (position < trainList.size - 1) {
                        position++
                        player!!.start()
                        setTimerFor(position)
                    } else {
                        player = MediaPlayer.create(this@ExerciseActivity, R.raw.finish_workout)
                        player!!.start()
                        startTimer(5, -1)

                    }
                }else{
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    intent.putExtra("TrainList", trainList)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

    fun setTimerFor(index: Int, progress:Int = 0) {
        if (timer != null) {
            timer!!.cancel()
        }
        DescriptionText.text = trainList[index].getDescription()
        startTimer(trainList[index].getTime(),progress)
    }
}