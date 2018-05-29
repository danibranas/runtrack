package com.muei.apm.runtrack.data.models

import com.google.android.gms.maps.model.LatLng
import java.util.Date

data class Location(
        var latitude: Double,
        var longitude: Double,
        var date: Date = Date()
) {
    constructor(point: LatLng, date: Date = Date()): this(point.latitude, point.longitude, date)
}