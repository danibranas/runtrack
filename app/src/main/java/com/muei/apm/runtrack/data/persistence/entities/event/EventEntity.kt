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
    @ColumnInfo var coordinateLat: Double? = null
    @ColumnInfo var coordinateLng: Double? = null
    @ColumnInfo var distance: Float? = null
    @ColumnInfo var unit: String? = null
    @ColumnInfo var imageUri: String? = null
    @ColumnInfo var prize: Double? = null
    @ColumnInfo var users: Int = 0
    @ColumnInfo var isInternal: Boolean = false

    // Exclusive joined
    @ColumnInfo var avgSpeed: Long? = null
    @ColumnInfo var totalTime: Long? = null
    @ColumnInfo var avgPace: Long? = null
    @ColumnInfo var calories: Long? = null
    @ColumnInfo var positiveRamp: Long? = null
    @ColumnInfo var position: Int? = null
}