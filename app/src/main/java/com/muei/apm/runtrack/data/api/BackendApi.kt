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
        const val BASE_URL = "https://enigmatic-brook-93035.herokuapp.com"

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

    private fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    override fun findEventById(eventId: Long): ApiResponse<Event?> {
        return getRequest("/events/$eventId", { _ -> Event(eventId) }) // FIXME
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

    private fun <T> getRequest(url: String, converter: (e: JSONObject) -> T): BackendApiResponse<T> {
        return BackendApiResponse({
            onSuccess, onError ->
            val req = JsonObjectRequest(Request.Method.GET, "$BASE_URL$url", null,
                    Response.Listener<JSONObject> { onSuccess(converter(it)) },
                    Response.ErrorListener { error -> onError?.invoke(error.message!!) })
            addToRequestQueue(req)
        })
    }
}