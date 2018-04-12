package com.muei.apm.runtrack.models

import com.muei.apm.runtrack.models.event.EventStatus
import java.io.Serializable

class Event: Serializable {
    var name: String? = null
    var coordinates: Coordinates? = null
    var date: String? = null
    var distance: Float? = null
    var imageUri: String? = null
    var users: Set<User>? = null
    var status: EventStatus = EventStatus.UNKNOWN

    class Coordinates(var lat: Double, var long: Double) : Serializable
}