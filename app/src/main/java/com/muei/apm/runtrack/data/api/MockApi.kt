package com.muei.apm.runtrack.data.api

import android.content.Context
import com.muei.apm.runtrack.data.fixtures.EventsFixture
import com.muei.apm.runtrack.data.models.Event

class MockApi(val context: Context): Api {
    override fun fetchNearEvents(lat: Long, lng: Long): List<Event> {
        return EventsFixture.generate()
    }

    override fun joinEvent(eventId: Long): Event? {
        return null
    }

    override fun findEventById(eventId: Long): Event? {
        val event = EventsFixture.generate(1)[0]
        event.id = eventId
        return event
    }
}