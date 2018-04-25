package com.muei.apm.runtrack.persistence.entities.location

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface LocationDao {
    @Query("SELECT * FROM location")
    fun getAll(): List<Location>

    @Insert
    fun insert(location: Location)

    @Query("SELECT * FROM location WHERE id = :id")
    fun findById(id: Long): Location?

    @Delete
    fun delete(location: Location)
}