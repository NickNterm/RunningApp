package com.nicknterm.runningapp

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.interval_training_exercise_activity.*
import kotlinx.android.synthetic.main.quit_training_dialog.*


class IntervalTrainingExerciseActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var timer: CountDownTimer? = null
    private var intervalTrainingList: ArrayList<IntervalTrainingItem> = ArrayList()
    private var pauseSecond: Int = 0
    private var isPaused: Boolean = false
    private var position: Int = 0
    private var canPress: Boolean = false


    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interval_training_exercise_activity)
        intervalTrainingList = intent.getParcelableArrayListExtra<IntervalTrainingItem>("TrainList") as ArrayList<IntervalTrainingItem>

        startTimer(position)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

        // Skip Activity functionality
        // Sets timer for the next activity and resets progress bar, buttons
        SkipButton.setOnClickListener{
            if(canPress) {
                if (position < intervalTrainingList.size - 1) {
                    position++
                    startTimer(position)
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
                    val intent = Intent(this@IntervalTrainingExerciseActivity, FinishActivity::class.java)
                    intent.putExtra("TrainList", intervalTrainingList)
                    startActivity(intent)
                    finish()
                }
                buttonsDisabled()
            }
        }

        // Pauses timer and change the progress bar color by showing other progress bar
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

        // Resumes timer and change the progress bar color by showing first progress bar
        ResumeButton.setOnClickListener {
            if(canPress) {
                if (timer != null) {
                    TimerPausedProgressBar.visibility = View.GONE
                    TimerProgressBar.visibility = View.VISIBLE
                    TimerProgressBar.max = TimerPausedProgressBar.max
                    TimerProgressBar.progress = TimerPausedProgressBar.progress
                    pauseSecond = TimerProgressBar.progress
                    isPaused = false
                    startTimer(position, pauseSecond)
                    ResumeButton.visibility = View.GONE
                    PauseButton.visibility = View.VISIBLE
                }
                buttonsDisabled()
            }
        }
    }

    // This function creates a notification with a specific Title and Message
    private fun refreshNotifications(message: String, Title:String) {
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Sets an ID for the notification, so it can be updated
        val notifyID = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,
                description,
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.nav_image)
                .setContentTitle(Title)
                .setContentText(message)
                .setAutoCancel(true)
        } else {
            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.nav_image)
                .setContentTitle(Title)
                .setContentText(message)
                .setAutoCancel(true)
        }
        mNotificationManager.notify(
            notifyID,
            builder.build())
    }
    

    // Enables the buttons and Disable the lock button
    private fun buttonsEnabled(){
        LockButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        SkipButton.setBackgroundResource(R.drawable.text_view_button_background_ripple)
        PauseButton.setBackgroundResource(R.drawable.text_view_button_background_ripple)
        ResumeButton.setBackgroundResource(R.drawable.text_view_button_background_ripple)
        canPress = true
    }

    // Disables the buttons and Enables the lock button
    private fun buttonsDisabled(){
        LockButton.setBackgroundResource(R.drawable.text_view_button_background_ripple)
        SkipButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        PauseButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        ResumeButton.setBackgroundResource(R.drawable.text_view_button_disabled)
        canPress = false
    }

    // Make sure that when the activity ends the timer, players are stopped
    override fun onDestroy() {
        player!!.stop()
        timer!!.cancel()
        super.onDestroy()
    }

    // Controls the BackPress
    override fun onBackPressed() {
        showQuitDialog()
    }

    // Shows the Quit Activity Dialog and controls the ClickListeners of the buttons
    private fun showQuitDialog(){
        val quitDialog = Dialog(this)
        quitDialog.setContentView(R.layout.quit_training_dialog)

        quitDialog.NoQuitButton.setOnClickListener{
            quitDialog.dismiss()
        }

        quitDialog.YesQuitButton.setOnClickListener {
            finish()
        }

        quitDialog.show()
    }

    // The main Timer structure. time is the whole Exercise time
    // and progressPar parameter is for staring the timer not always from the start
    // if progressPar is negative after the timer ends it starts the next activity
    // plus it doesn't show the progress bar progress
    private fun startTimer(index: Int, progressPar: Int = 0) {
        val time:Int
        if(progressPar>= 0) {
            time = intervalTrainingList[index].getTime()
            DescriptionText.text = intervalTrainingList[index].getDescription()
        }else{
            time = index
        }
        if (timer != null) {
            timer!!.cancel()
        }

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
                            refreshNotifications("${(time - progress) / 60}:0${(time - progress) % 60}", "Activity Started")
                        } else {
                            TimerText.text = "${(time - progress) / 60}:${(time - progress) % 60}"
                            refreshNotifications("${(time - progress) / 60}:${(time - progress) % 60}", "Activity Started")
                        }
                    } else {
                        cancel()
                    }
                }
            }

            override fun onFinish() {
                if (progressPar >= 0) {
                    if (position < intervalTrainingList.size - 1) {
                        position++
                        player!!.start()
                        startTimer(position)
                    } else {
                        player = MediaPlayer.create(this@IntervalTrainingExerciseActivity, R.raw.final_sound)
                        player!!.start()
                        startTimer(2, -1)
                        refreshNotifications("Congratulations!!!", "Workout Finished")
                    }
                }else{
                    val intent = Intent(this@IntervalTrainingExerciseActivity, FinishActivity::class.java)
                    intent.putExtra("TrainList", intervalTrainingList)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }
}