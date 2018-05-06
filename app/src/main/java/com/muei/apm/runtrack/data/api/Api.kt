package com.muei.apm.runtrack.data.api

import com.muei.apm.runtrack.data.api.response.ApiResponse
import com.muei.apm.runtrack.data.models.Event

interface Api {

    fun fetchNearEvents(lat: Long, lng: Long): ApiResponse<List<Event>>
    fun joinEvent(eventId: Long, userId: Long): ApiResponse<Event?>
    fun findEventById(eventId: Long): ApiResponse<Event?>
    fun finishEventById(eventId: Long, results: Event.Results?): ApiResponse<Any>
}