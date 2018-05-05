package com.muei.apm.runtrack.data.persistence.entities.event

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo

@Entity(tableName = "event")
data class EventEntity(
    @ColumnInfo var name: String,
    @ColumnInfo var startDate: Long? = null
    // ...
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @ColumnInfo var joinDate: Long? = null
    @ColumnInfo var endDate: Long? = null
}