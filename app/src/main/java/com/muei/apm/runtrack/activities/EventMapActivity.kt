package com.muei.apm.runtrack.activities

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.data.api.Api
import com.muei.apm.runtrack.data.api.ApiFactory
import com.muei.apm.runtrack.data.persistence.AppDatabase
import com.muei.apm.runtrack.data.persistence.Service
import com.muei.apm.runtrack.data.persistence.ServiceDb

class EventMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var eventId: Long? = null

    private val database: Service by lazy {
        ServiceDb(AppDatabase.getInstance(this)!!, this)
    }

    private val api: Api by lazy {
        ApiFactory.getApi(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_map)

        eventId = savedInstanceState?.getLong(EventDetailsActivity.EXTRA_EVENT_ID)
                ?: intent.getLongExtra(EventDetailsActivity.EXTRA_EVENT_ID, -1)
        title = savedInstanceState?.getString(EventDetailsActivity.EXTRA_EVENT_NAME)
                ?: intent.getStringExtra(EventDetailsActivity.EXTRA_EVENT_NAME)

        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map

        api.findEventById(eventId!!).onResult({
            if (it?.route != null) {
                this.map?.addPolyline(PolylineOptions()
                        .width(30f)
                        .color(Color.DKGRAY)
                        .geodesic(true)
                        .addAll(it.route!!.map { loc -> LatLng(loc.latitude, loc.longitude) }))
                val start = it.route?.firstOrNull()
                val end = it.route?.lastOrNull()

                if (start != null) {
                    this.map?.addMarker(MarkerOptions().position(LatLng(start.latitude, start.longitude)))
                }

                if (end != null) {
                    val endPoint = LatLng(end.latitude, end.longitude)
                    this.map?.addMarker(MarkerOptions().position(endPoint))
                    this.map?.animateCamera(CameraUpdateFactory.newLatLngZoom(endPoint, 17f))
                }
            }
        })

        database.getLocationsByEventId(eventId!!)
                .observe(this, Observer {
                    if (it != null) {
                        this.map?.addPolyline(PolylineOptions()
                                .width(15f)
                                .color(Color.BLUE) // TODO: change to orange?
                                .geodesic(true)
                                .addAll(it.map { loc -> LatLng(loc.latitude, loc.longitude) }))
                        val end = it.firstOrNull()

                        if (end != null) {
                            val endPoint = LatLng(end.latitude, end.longitude)
                            this.map?.addMarker(MarkerOptions().position(endPoint))
                            this.map?.animateCamera(CameraUpdateFactory.newLatLngZoom(endPoint, 17f))
                        }
                    }
                })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        eventId = savedInstanceState?.getLong(EventDetailsActivity.EXTRA_EVENT_ID)
                ?: intent.getLongExtra(EventDetailsActivity.EXTRA_EVENT_ID, -1)
        title = savedInstanceState?.getString(EventDetailsActivity.EXTRA_EVENT_NAME)
                ?: intent.getStringExtra(EventDetailsActivity.EXTRA_EVENT_NAME)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        bundle.run {
            putLong(EventDetailsActivity.EXTRA_EVENT_ID, eventId ?: -1)
            putString(EventDetailsActivity.EXTRA_EVENT_NAME, title.toString())
        }
    }
}
