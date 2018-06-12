package com.muei.apm.runtrack.data.persistence

import android.content.Context

interface Database {
    fun getInstance(context: Context): AppDatabase?
    fun clearDatabase()
    fun destroyInstance()
}