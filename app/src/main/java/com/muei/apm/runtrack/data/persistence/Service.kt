package com.muei.apm.runtrack.data.persistence

import android.arch.lifecycle.LiveData
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.Location
import java.util.*

interface Service {

    fun joinEvent(event: Event): LiveData<Boolean>

    fun unjoinEventById(eventId: Long): LiveData<Boolean>

    fun getMyFinishedEvents(): LiveData<List<Event>>

    fun getMyUpcomingEvents(): LiveData<List<Event>>

    fun getEventById(eventId: Long): LiveData<Event?>

    fun getLocationsByEventId(eventId: Long): LiveData<List<Location>>

    fun startEventTracking(eventId: Long): LiveData<Boolean>

    fun finishEventTracking(eventId: Long, date: Date, results: Event.Results): LiveData<Boolean>

    fun registerEventLocation(eventId: Long, location: Location)
}