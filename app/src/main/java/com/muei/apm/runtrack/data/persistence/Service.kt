package com.muei.apm.runtrack.data.persistence

import android.arch.lifecycle.LiveData
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.Location

interface Service {

    fun joinEvent(event: Event)

    fun unjoinEventById(eventId: Long)

    fun getMyFinishedEvents(): LiveData<List<Event>>

    fun getMyUpcomingEvents(): LiveData<List<Event>>

    fun getEventById(eventId: Long): LiveData<Event?>

    fun getLocationsByEventId(eventId: Long): LiveData<List<Location>>

    fun startEventTracking(eventId: Long)

    fun registerEventLocation(eventId: Long, location: Location)
}