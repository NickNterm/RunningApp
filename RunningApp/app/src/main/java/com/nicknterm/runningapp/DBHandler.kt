package com.nicknterm.runningapp

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.SQLException
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object{
        // Change DATABASE_VERSION every time you change the structure of the database
        // For example if you add another column, change the DATABASE_VERSION
        // Plus the onUpgrade function is going to get called so change that so the user doesn't loses all its data
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TrainTimer"
        private const val TABLE_NAME = "TrainTable"

        // Table columns
        private const val KEY_ID = "_id"
        private const val KEY_TRAINING_NAME = "train_name"
        private const val KEY_ITEM_ID = "item_id"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_TIMER = "times"

    }

    // If the Database is not made then it Creates the main table
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = ("CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY, $KEY_TRAINING_NAME TEXT, $KEY_ITEM_ID INTEGER, $KEY_DESCRIPTION INTEGER, $KEY_TIMER TEXT)")
        db?.execSQL(sql)
    }

    // Is called only while the version has changed. Change that
    // function accordingly so the user doesn't loses its data
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // This function saves a train record into the database
    // It returns a Long. I don't even know what this is sooo
    fun saveIntervalTrainingItem(item: IntervalTrainingItem, name: String):Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ITEM_ID, item.getId())
        contentValues.put(KEY_TRAINING_NAME, name)
        contentValues.put(KEY_TIMER, item.getTime())
        contentValues.put(KEY_DESCRIPTION, item.getDescription())

        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    // This function reads only the names of the saved activities
    // It returns a ArrayList of Strings with the names
    @SuppressLint("Recycle")
    fun intervalTrainingSessionNames():ArrayList<String>{
        val nameList: ArrayList<String> = ArrayList<String>()

        val selectQuery = "SELECT $KEY_TRAINING_NAME FROM $TABLE_NAME"

        val db = this.readableDatabase
        val cursor: Cursor?

        try{
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var name: String

        if(cursor.moveToFirst()){
            do{
                name = cursor.getString(cursor.getColumnIndex(KEY_TRAINING_NAME))

                nameList.add(name)
            }while(cursor.moveToNext())
        }
        return nameList
    }

    // Finally this function reads the Sessions by the name of a certain activity
    // It returns a list of TrainItems in order to get shown in the main RecycleView
    @SuppressLint("Recycle")
    fun intervalTrainingItemsInSession(trainName: String):ArrayList<IntervalTrainingItem>{
        val intervalTrainingItemList: ArrayList<IntervalTrainingItem> = ArrayList<IntervalTrainingItem>()

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $KEY_TRAINING_NAME = '$trainName'"

        val db = this.readableDatabase
        val cursor: Cursor

        try{
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        //var id:Int
        var itemId:Int
        var time:Int
        var description: String

        if(cursor.moveToFirst()){
            do{
                //id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                itemId = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ID))
                time = cursor.getInt(cursor.getColumnIndex(KEY_TIMER))
                description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))

                val finalItem = IntervalTrainingItem(itemId,description,time)
                intervalTrainingItemList.add(finalItem)
            }while(cursor.moveToNext())
        }
        return intervalTrainingItemList
    }
}