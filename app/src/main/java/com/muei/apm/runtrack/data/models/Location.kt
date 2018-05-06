package com.muei.apm.runtrack.data.models

import java.util.Date

data class Location(
        var latitude: Double,
        var longitude: Double,
        var date: Date = Date()
)