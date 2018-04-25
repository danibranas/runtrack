package com.muei.apm.runtrack.persistence.entities.location

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "location")
data class Location(
        @ColumnInfo var latitude: Long,
        @ColumnInfo var longitude: Long,
        @ColumnInfo var eventId: Long
        // ...
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}