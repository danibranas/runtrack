package com.muei.apm.runtrack.activities.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.muei.apm.runtrack.services.LocationUpdatesService

class TrackingReceiver(private val onReceive: (position: LatLng) -> Unit): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val location = intent!!.getParcelableExtra<Location>(
                LocationUpdatesService.EXTRA_LOCATION)
        if (location != null) {
            onReceive(LatLng(location.latitude, location.longitude))
        }
    }

}