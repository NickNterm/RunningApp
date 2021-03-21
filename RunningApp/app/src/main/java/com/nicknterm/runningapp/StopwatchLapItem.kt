package com.nicknterm.runningapp

class StopwatchLapItem(private val id: Int, private val splitTime: Long, private val lapTime: Long, private var isTheBest:Boolean,private var isTheWorst:Boolean) {

    fun getId():Int{
        return id
    }

    fun getSplitTime():Long{
        return splitTime
    }

    fun getLapTime():Long{
        return lapTime
    }

    fun getIsTheBest():Boolean{
        return isTheBest
    }

    fun getIsTheWorst():Boolean{
        return isTheWorst
    }

    fun setIsTheBest(v: Boolean){
        isTheBest = v
    }

    fun setIsTheWorst(v: Boolean){
        isTheWorst = v
    }
}