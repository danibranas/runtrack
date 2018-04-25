package com.muei.apm.runtrack.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.muei.apm.runtrack.persistence.entities.event.UserEvent
import com.muei.apm.runtrack.persistence.entities.event.UserEventDao
import com.muei.apm.runtrack.persistence.entities.location.Location
import com.muei.apm.runtrack.persistence.entities.location.LocationDao

@Database(entities = [ UserEvent::class, Location::class ], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): UserEventDao
    abstract fun locationDao(): LocationDao

    companion object {
        private const val DB_NAME = "runtrack.app"

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized (AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "$DB_NAME.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}