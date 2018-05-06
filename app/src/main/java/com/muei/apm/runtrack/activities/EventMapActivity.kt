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
import com.muei.apm.runtrack.data.persistence.AppDatabase
import com.muei.apm.runtrack.data.persistence.Service
import com.muei.apm.runtrack.data.persistence.ServiceDb

class EventMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null

    private var eventId: Long? = null

    private val database: Service by lazy {
        ServiceDb(AppDatabase.getInstance(this)!!, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

        database.getLocationsByEventId(eventId!!)
                .observe(this, Observer {
                    if (it != null) {
                        this.map?.addPolyline(PolylineOptions()
                                .width(15f)
                                .color(Color.BLUE) // TODO: change to orange?
                                .geodesic(true)
                                .addAll(it.map { loc -> LatLng(loc.latitude, loc.longitude) }))
                        val start = it.firstOrNull()
                        val end = it.firstOrNull()

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
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putLong(EventDetailsActivity.EXTRA_EVENT_ID, eventId ?: -1)
        bundle.putString(EventDetailsActivity.EXTRA_EVENT_NAME, title.toString())
    }
}
