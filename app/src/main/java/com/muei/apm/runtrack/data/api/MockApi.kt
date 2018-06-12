package com.muei.apm.runtrack.data.api

import android.content.Context
import com.muei.apm.runtrack.data.api.response.ApiResponse
import com.muei.apm.runtrack.data.api.response.MockApiResponse
import com.muei.apm.runtrack.data.fixtures.EventsFixture
import com.muei.apm.runtrack.data.models.Event
import java.util.*

class MockApi(val context: Context): Api {

    companion object {
        private val nearEvents by lazy {
            EventsFixture.generateDemo()
        }
    }

    override fun fetchNearEvents(lat: Long, lng: Long): ApiResponse<List<Event>> {
        return MockApiResponse(nearEvents)
    }

    override fun joinEvent(eventId: Long, userId: Long): ApiResponse<Event?> {
        val event = getEventById(eventId)
        event?.joined = true
        return MockApiResponse(event)
    }

    override fun findEventById(eventId: Long): ApiResponse<Event?> {
        return MockApiResponse(getEventById(eventId))
    }

    override fun finishEventById(eventId: Long, results: Event.Results?): ApiResponse<Any> {
        val event = getEventById(eventId)
        event?.results = results
        event?.endDate = Date()
        return MockApiResponse("Ok")
    }


    private fun getEventById(eventId: Long): Event? {
        return nearEvents.find { e -> e.id == eventId }
    }
}