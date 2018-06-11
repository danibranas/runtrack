package com.muei.apm.runtrack.data.models.event

enum class EventStatus(val code: Int) {
    UNKNOWN(-1),
    UNJOINED(0),
    JOINED(1),
    FINISHED(2)
}