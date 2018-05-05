package com.muei.apm.runtrack.data.persistence.entities.event

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface EventEntityDao {
    @Query("SELECT * FROM event")
    fun getAll(): LiveData<List<EventEntity>>

    @Insert
    fun insert(userEvent: EventEntity)

    @Query("SELECT * FROM event WHERE id = :id")
    fun findById(id: Long): LiveData<EventEntity?>

    @Query("SELECT * FROM event WHERE endDate IS NOT NULL")
    fun getFinishedEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE endDate IS NULL")
    fun getUpcomingEvents(): LiveData<List<EventEntity>>

    @Update
    fun update(event: EventEntity)

    @Query("UPDATE event SET joinDate = :joinDate WHERE id = :eventId AND joinDate IS NULL")
    fun joinEventById(eventId: Long, joinDate: Long)

    @Query("DELETE FROM event WHERE id = :eventId AND joinDate IS NULL")
    fun deleteById(eventId: Long)

    @Delete
    fun delete(userEvent: EventEntity)
}