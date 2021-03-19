package com.nicknterm.runningapp

import android.os.Parcel
import android.os.Parcelable


class TrainItem(private var id: Int, private var description: String?, private var time: Int):
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt()
    )

    fun getId(): Int{
        return id
    }

    fun getDescription(): String? {
        return description
    }

    fun getTime(): Int{
        return time
    }

    fun setTime(v: Int){
        time = v
    }

    fun setDescription(v: String){
        description = v
    }

    fun setId(v: Int){
        id = v
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(description)
        parcel.writeInt(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrainItem> {
        override fun createFromParcel(parcel: Parcel): TrainItem {
            return TrainItem(parcel)
        }

        override fun newArray(size: Int): Array<TrainItem?> {
            return arrayOfNulls(size)
        }
    }
}