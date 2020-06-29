package com.example.runnerapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Run::class], version = 1)
@TypeConverters(RunConverters::class)
abstract class RunDatabase : RoomDatabase() {
    abstract fun getDao(): RunDao
}