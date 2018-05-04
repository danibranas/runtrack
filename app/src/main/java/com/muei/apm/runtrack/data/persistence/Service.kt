package com.muei.apm.runtrack.data.persistence

import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.Location

interface Service {

    fun joinEvent(event: Event)

    fun unjoinEventById(eventId: Long)

    fun getMyFinishedEvents(): List<Event>

    fun getMyUpcomingEvents(): List<Event>

    fun getEventById(eventId: Long): Event?

    fun getLocationsByEventId(eventId: Long): Location

    fun startEventTracking(eventId: Long): Boolean

    fun registerEventLocation(eventId: Long, location: Location): Boolean
}