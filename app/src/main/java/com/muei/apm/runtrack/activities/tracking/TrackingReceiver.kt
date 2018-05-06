package com.muei.apm.runtrack.activities.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.muei.apm.runtrack.services.LocationUpdatesService
import com.muei.apm.runtrack.utils.LocationUtils

class TrackingReceiver(private val onReceive: (position: LatLng) -> Unit): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val location = intent!!.getParcelableExtra<Location>(
                LocationUpdatesService.EXTRA_LOCATION)
        if (location != null) {
            Toast.makeText(context, LocationUtils.getLocationText(location),
                    Toast.LENGTH_SHORT).show()

            onReceive(LatLng(location.latitude, location.longitude))
        }
    }

}