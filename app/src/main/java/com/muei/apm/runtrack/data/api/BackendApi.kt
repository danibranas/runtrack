package com.muei.apm.runtrack.data.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.muei.apm.runtrack.data.api.response.ApiResponse
import com.muei.apm.runtrack.data.api.response.BackendApiResponse
import com.muei.apm.runtrack.data.models.Event
import org.json.JSONObject

class BackendApi(context: Context): Api {
    companion object {
        @Volatile
        private var INSTANCE: BackendApi? = null
        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: BackendApi(context)
                }
    }

    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    override fun findEventById(eventId: Long): ApiResponse<Event?> {
        return BackendApiResponse({
            onSuccess, onError ->
                val req = JsonObjectRequest(Request.Method.GET, "http://google.com", null,
                        Response.Listener<JSONObject> {
                            // TODO: Convert object to event
                            onSuccess(Event(eventId))
                        },
                        Response.ErrorListener { error -> onError?.invoke(error.message!!) })
                addToRequestQueue(req)
        })
    }

    override fun fetchNearEvents(lat: Long, lng: Long): ApiResponse<List<Event>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun joinEvent(eventId: Long, userId: Long): ApiResponse<Event?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finishEventById(eventId: Long, results: Event.Results?): ApiResponse<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}