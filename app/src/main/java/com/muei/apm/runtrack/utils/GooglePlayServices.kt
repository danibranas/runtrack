package com.muei.apm.runtrack.utils

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices

class GooglePlayServices(val context: Context) {
    fun getGooglePlayServicesAvailability(): Int {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
    }

    fun isAvailable(): Boolean {
        return when (getGooglePlayServicesAvailability()) {
            ConnectionResult.SUCCESS -> true
            ConnectionResult.SERVICE_MISSING -> true
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> true
            ConnectionResult.SERVICE_DISABLED -> false
            else -> false
        }
    }

    fun getLocationService() {
        LocationServices.getFusedLocationProviderClient(context)
    }
}