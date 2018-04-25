package com.muei.apm.runtrack.persistence.entities.event

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo

@Entity(tableName = "event")
data class UserEvent(
    @ColumnInfo var name: String
    // ...
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}