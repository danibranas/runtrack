package com.muei.apm.runtrack.data.api

import com.muei.apm.runtrack.data.models.Event

interface Api {
    fun fetchNearEvents(lat: Long, lng: Long): List<Event>

    fun joinEvent(eventId: Long): Event?

    fun findEventById(eventId: Long): Event?
}