package com.muei.apm.runtrack.activities.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.muei.apm.runtrack.services.LocationUpdatesService
import com.muei.apm.runtrack.utils.LocationUtils

class TrackingReceiver(private val getMap: () -> GoogleMap?, private val polylines: PolylineOptions): BroadcastReceiver() {
    companion object {
        fun createPolyline(): PolylineOptions {
            return PolylineOptions()
                    .geodesic(true)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val location = intent!!.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
        if (location != null) {
            Toast.makeText(context, LocationUtils.getLocationText(location),
                    Toast.LENGTH_SHORT).show()

            val newPosition = LatLng(location.latitude, location.longitude)

            getMap()?.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 17f))
            polylines.add(newPosition)
        }
    }
}