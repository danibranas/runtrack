package com.muei.apm.runtrack.data.models

import java.util.Date

data class Location(
        var latitude: Long,
        var longitude: Long,
        var date: Date = Date()
)