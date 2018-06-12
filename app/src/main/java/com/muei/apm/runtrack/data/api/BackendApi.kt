package com.muei.apm.runtrack.data.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.muei.apm.runtrack.data.api.response.ApiResponse
import com.muei.apm.runtrack.data.api.response.BackendApiResponse
import com.muei.apm.runtrack.data.models.Event
import org.json.JSONArray
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
        return getRequest("/events/$eventId", ::jsonToEvent)
    }

    override fun fetchNearEvents(lat: Long, lng: Long): ApiResponse<List<Event>> {
        return getRequestList("/events", {
            list ->
                val result = ArrayList<Event>(list.length())

                for (i in 0..(list.length() - 1)) {
                    val item = list.getJSONObject(i)
                    result.add(jsonToEvent(item))
                }
            result
        })
    }

    override fun joinEvent(eventId: Long, userId: Long): ApiResponse<Event?> {
        return postRequest("/events/$eventId/$userId")
    }

    override fun finishEventById(eventId: Long, results: Event.Results?): ApiResponse<Any> {
        return postRequest("/events/$eventId")
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

    private fun <T> getRequestList(url: String, converter: (e: JSONArray) -> T): BackendApiResponse<T> {
        return BackendApiResponse({
            onSuccess, onError ->
            val req = JsonArrayRequest(Request.Method.GET, "$BASE_URL$url", null,
                    Response.Listener<JSONArray> { onSuccess(converter(it)) },
                    Response.ErrorListener { error -> onError?.invoke(error.message!!) })
            addToRequestQueue(req)
        })
    }

    private fun <T> postRequest(url: String): BackendApiResponse<T> {
        return BackendApiResponse({
            _, onError ->
            val req = JsonObjectRequest(Request.Method.GET, "$BASE_URL$url", null,
                    null,
                    Response.ErrorListener { error -> onError?.invoke(error.message!!) })
            addToRequestQueue(req)
        })
    }

    private fun jsonToEvent(obj: JSONObject): Event {
        val ev = Event(obj.getLong("id"))
        ev.name = obj.getString("name")
        ev.users = obj.getInt("users")
        ev.isInternal = obj.getBoolean("isInternal")
        ev.imageUri = obj.getString("imageUri")
        ev.description = obj.getString("description")
        return ev
    }
}