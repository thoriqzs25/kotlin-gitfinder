package com.gitfinder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteUser::class], version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract fun favUserDao(): FavoriteDao

    companion object {
        @Volatile
        private  var INSTANCE: MainDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MainDatabase {
            if (INSTANCE == null) {
                synchronized(MainDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, MainDatabase::class.java, "main_database")
                        .build()
                }
            }
            return INSTANCE as MainDatabase
        }
    }
}