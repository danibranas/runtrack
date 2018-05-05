package com.muei.apm.runtrack.data.models

import com.muei.apm.runtrack.data.models.event.EventStatus
import com.muei.apm.runtrack.data.models.event.EventUnit
import java.util.*

data class Event(var id: Long) {
    var name: String = ""
    var joined = false
    var coordinates: Coordinates? = null
    var date: Date? = null
    var distance: Float? = null
    var imageUri: String? = null
    var users: Set<User>? = null
    var status = EventStatus.UNKNOWN
    var unit: EventUnit? = EventUnit.KM

    data class Coordinates(var lat: Double, var long: Double)
}