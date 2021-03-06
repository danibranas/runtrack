package com.muei.apm.runtrack.data.models.event

enum class EventUnit(val unit: String) {
    KM("km");

    override fun toString(): String {
        return unit
    }
}