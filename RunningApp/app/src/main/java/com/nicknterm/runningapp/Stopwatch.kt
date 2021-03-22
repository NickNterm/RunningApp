package com.nicknterm.runningapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.stopwatch_main_activity.*
import kotlin.collections.ArrayList

class Stopwatch : AppCompatActivity() {
    private var startTime: Long = 0
    private var pausedTime: Long = 0
    private var timeHandler: Handler? = null
    private var isPaused: Boolean = false
    private var timeToStart: Boolean = true
    private var lapTimes: ArrayList<StopwatchLapItem> = ArrayList<StopwatchLapItem>()
    private var lapId:Int = -1
    private var bestLapId = 0
    private var worstLapId = 0
    private var lastLapTime:Long = 0

    private var RvAdapter:StopwatchLapRecycleViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stopwatch_main_activity)
        timeHandler = Handler()
        StopwatchRecycleView.layoutManager = LinearLayoutManager(this)
        RvAdapter = StopwatchLapRecycleViewAdapter(lapTimes, this)
        StopwatchRecycleView.adapter = RvAdapter

        StopwatchTimerText.setOnClickListener{
            if(PauseButton.text.toString() == "Start"){
                PauseButton.text = "Pause"
            }
            if(timeToStart) {
                startStopwatch(pausedTime)
            }else{
                pauseStopwatch()
            }
            timeToStart = !timeToStart
        }
        PauseButton.setOnClickListener {
            if(timeToStart) {
                PauseButton.text = "Pause"
                startStopwatch(pausedTime)
            }else{
                PauseButton.text = "Resume"
                pauseStopwatch()

            }
            timeToStart = !timeToStart
        }
        LapButton.setOnClickListener {
            createLap()
        }
    }

    private fun createLap() {
        if(!isPaused) {
            StopwatchRecycleView.smoothScrollToPosition(0)
            lapId++
            if(lapTimes.size > 0) {
                var isBest: Boolean = false
                var isWorst: Boolean = false
                if (lapTimes[bestLapId].getLapTime() > (System.currentTimeMillis() - startTime) - lastLapTime) {
                    lapTimes[worstLapId].setIsTheWorst(true)
                    lapTimes[bestLapId].setIsTheBest(false)
                    bestLapId = lapId
                    isBest = true
                } else if (lapTimes[worstLapId].getLapTime() < (System.currentTimeMillis() - startTime) - lastLapTime) {
                    lapTimes[bestLapId].setIsTheBest(true)
                    lapTimes[worstLapId].setIsTheWorst(false)
                    worstLapId = lapId
                    isWorst = true
                }

                lapTimes.add(StopwatchLapItem(lapId,
                    System.currentTimeMillis() - startTime,
                    (System.currentTimeMillis() - startTime) - lastLapTime,
                    isBest,
                    isWorst))
                lastLapTime = System.currentTimeMillis() - startTime

                RvAdapter!!.notifyDataSetChanged()
            }else{
                lapTimes.add(StopwatchLapItem(lapId,
                    System.currentTimeMillis() - startTime,
                    (System.currentTimeMillis() - startTime) - lastLapTime,
                    false,
                    false))
                lastLapTime = System.currentTimeMillis() - startTime
                lapTimes.reverse()
                RvAdapter!!.notifyDataSetChanged()
                lapTimes.reverse()
            }
        }
    }

    private fun startStopwatch(startFrom: Long = 0){
        StopWatchProgressBar.visibility = View.VISIBLE
        StopWatchPausedProgressBar.visibility = View.GONE
        LapButton.visibility = View.VISIBLE
        isPaused = false
        startTime = System.currentTimeMillis() - startFrom
        val myRunnable: Runnable = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                if(!isPaused) {
                    var millis = System.currentTimeMillis() - startTime
                    var seconds: Int = (millis / 1000).toInt()
                    val minutes = seconds / 60
                    seconds %= 60
                    millis %= 1000
                    millis /= 10
                    StopwatchTimerText.text =
                        String.format("%d:%02d.%02d", minutes, seconds, millis)
                    timeHandler!!.postDelayed(this, 10)
                }else{
                    timeHandler!!.removeCallbacks(this)
                }
            }
        }
        timeHandler!!.post(myRunnable)
    }

    private fun pauseStopwatch(){
        LapButton.visibility = View.GONE
        StopWatchProgressBar.visibility = View.GONE
        StopWatchPausedProgressBar.visibility = View.VISIBLE
        pausedTime = System.currentTimeMillis() - startTime
        isPaused = true
    }
}