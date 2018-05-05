package com.muei.apm.runtrack.data.api

import android.content.Context
import com.muei.apm.runtrack.data.api.response.ApiResponse
import com.muei.apm.runtrack.data.api.response.MockApiResponse
import com.muei.apm.runtrack.data.fixtures.EventsFixture
import com.muei.apm.runtrack.data.models.Event

class MockApi(val context: Context): Api {

    companion object {
        private val eventList by lazy {
            EventsFixture.generate(12)
        }
    }

    override fun fetchNearEvents(lat: Long, lng: Long): ApiResponse<List<Event>> {
        return MockApiResponse(eventList)
    }

    override fun joinEvent(eventId: Long, userId: Long): ApiResponse<Event?> {
        val event = eventList.find { e -> e.id == eventId }
        event?.joined = true
        return MockApiResponse(event)
    }

    override fun findEventById(eventId: Long): ApiResponse<Event?> {
        return MockApiResponse(eventList.find { e -> e.id == eventId })
    }

}