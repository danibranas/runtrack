package com.muei.apm.runtrack.data.persistence.entities.location

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntity


@Entity(tableName = "location",
        indices = [ Index(value = [ "eventId" ]) ],
        foreignKeys = [ ForeignKey(entity = EventEntity::class,
                parentColumns = [ "id" ], childColumns = [ "eventId" ]) ])
data class LocationEntity(
        @ColumnInfo var latitude: Double,
        @ColumnInfo var longitude: Double,
        @ColumnInfo var eventId: Long
        // ...
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @ColumnInfo var date: Long? = null
}