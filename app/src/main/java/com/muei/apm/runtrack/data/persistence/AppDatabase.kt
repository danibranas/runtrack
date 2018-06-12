package com.muei.apm.runtrack.data.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntity
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntityDao
import com.muei.apm.runtrack.data.persistence.entities.location.LocationEntity
import com.muei.apm.runtrack.data.persistence.entities.location.LocationEntityDao
import com.muei.apm.runtrack.tasks.OperationTask

@Database(entities = [ EventEntity::class, LocationEntity::class ], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventEntityDao
    abstract fun locationDao(): LocationEntityDao

    companion object: com.muei.apm.runtrack.data.persistence.Database {
        private const val DB_NAME = "runtrack.app"

        private var INSTANCE: AppDatabase? = null

        override fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized (AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                         AppDatabase::class.java, "$DB_NAME.db")
                         .build()
                }
            }
            return INSTANCE
        }

        override fun clearDatabase() {
            OperationTask<Unit>().execute({
                INSTANCE?.clearAllTables()
            })
        }

        override fun destroyInstance() {
            INSTANCE = null
        }
    }
}