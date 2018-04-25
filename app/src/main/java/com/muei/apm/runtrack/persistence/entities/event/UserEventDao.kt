package com.muei.apm.runtrack.persistence.entities.event

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface UserEventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<UserEvent>

    @Insert
    fun insert(userEvent: UserEvent)

    @Query("SELECT * FROM event WHERE id = :id")
    fun findById(id: Long): UserEvent?

    @Delete
    fun delete(userEvent: UserEvent)
}