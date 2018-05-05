package com.muei.apm.runtrack.data.persistence.entities.location

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface LocationEntityDao {
    @Query("SELECT * FROM location")
    fun getAll(): List<LocationEntity>

    @Insert
    fun insert(locationEntity: LocationEntity)

    @Query("SELECT * FROM location WHERE id = :id")
    fun findById(id: Long): LiveData<LocationEntity?>

    @Query("SELECT * FROM location WHERE eventId = :eventId")
    fun findByEventId(eventId: Long): LiveData<List<LocationEntity>>

    @Delete
    fun delete(locationEntity: LocationEntity)
}