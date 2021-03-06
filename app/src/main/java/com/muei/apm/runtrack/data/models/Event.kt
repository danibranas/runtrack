package com.muei.apm.runtrack.data.models

import com.muei.apm.runtrack.data.models.event.EventStatus
import com.muei.apm.runtrack.data.models.event.EventUnit
import java.util.*

data class Event(var id: Long) {
    var name: String = ""
    var description: String = ""
    var joined = false
    var coordinates: Coordinates? = null
    var date: Date? = null
    var endDate: Date? = null
    var distance: Float? = null
    var imageUri: String? = null
    var isInternal: Boolean = false
    var users: Int = 0
    var unit: EventUnit? = EventUnit.KM
    var results: Results? = null
    var route: List<Location>? = null
    var prize: Double? = null

    data class Coordinates(var lat: Double, var long: Double)

    data class Results(
        // Exclusive joined
        var avgSpeed: Long? = null,
        var totalTime: Long? = null,
        var avgPace: Long? = null,
        var calories: Long? = null,
        var positiveRamp: Long? = null,
        var position: Int? = null
    )
}