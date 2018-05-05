package com.muei.apm.runtrack.data.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntity
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntityDao
import com.muei.apm.runtrack.data.persistence.entities.location.LocationEntity
import com.muei.apm.runtrack.data.persistence.entities.location.LocationEntityDao

@Database(entities = [ EventEntity::class, LocationEntity::class ], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventEntityDao
    abstract fun locationDao(): LocationEntityDao

    companion object {
        private const val DB_NAME = "runtrack.app"

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized (AppDatabase::class) {
                    // TODO: Temporarily store in memory, but change it!!
                    // INSTANCE = Room.databaseBuilder(context.applicationContext,
                    //      AppDatabase::class.java, "$DB_NAME.db")
                    //      .build()
                    INSTANCE = Room.inMemoryDatabaseBuilder(context.applicationContext,
                            AppDatabase::class.java)
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