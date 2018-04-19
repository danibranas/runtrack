package com.muei.apm.runtrack.activities

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.muei.apm.runtrack.R
import android.content.Intent
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.support.v4.app.ActivityCompat
import android.support.design.widget.Snackbar
import android.widget.Toast
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ApiException
import android.os.Looper
import android.app.Activity
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import android.widget.TextView
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.muei.apm.runtrack.BuildConfig
import java.text.DateFormat
import java.util.*

class TrackingActivity : AppCompatActivity() {

    private val tag = TrackingActivity::class.java.simpleName

    /**
     * Code used in requesting runtime permissions.
     */
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    /**
     * Constant used in the location settings dialog.
     */
    private val REQUEST_CHECK_SETTINGS = 0x1

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    // Keys for storing activity state in the Bundle.
    private val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
    private val KEY_LOCATION = "location"
    private val KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string"

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    private var mSettingsClient: SettingsClient? = null

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    /**
     * Callback for Location events.
     */
    private var mLocationCallback: LocationCallback? = null

    /**
     * Represents a geographical location.
     */
    private var mCurrentLocation: Location? = null

    // UI Widgets.
    private var mStartUpdatesButton: Button? = null
    private var mStopUpdatesButton: Button? = null
    private var mLastUpdateTimeTextView: TextView? = null
    private var mLatitudeTextView: TextView? = null
    private var mLongitudeTextView: TextView? = null

    // Labels.
    private var mLatitudeLabel: String? = null
    private var mLongitudeLabel: String? = null
    private var mLastUpdateTimeLabel: String? = null

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private var mRequestingLocationUpdates: Boolean? = null

    /**
     * Time when the location was updated represented as a String.
     */
    private var mLastUpdateTime: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        // Locate the UI widgets.
        mStartUpdatesButton = findViewById(R.id.start_updates_button) as Button
        mStopUpdatesButton = findViewById(R.id.stop_updates_button) as Button
        mLatitudeTextView = findViewById(R.id.latitude_text) as TextView
        mLongitudeTextView = findViewById(R.id.longitude_text) as TextView
        mLastUpdateTimeTextView = findViewById(R.id.last_update_time_text) as TextView

        // Set labels.
        mLatitudeLabel = resources.getString(R.string.latitude_label)
        mLongitudeLabel = resources.getString(R.string.longitude_label)
        mLastUpdateTimeLabel = resources.getString(R.string.last_update_time_label)

        mRequestingLocationUpdates = false
        mLastUpdateTime = ""

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES)
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable<Location>(KEY_LOCATION)
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING)
            }
            updateUI()
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     *
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                mCurrentLocation = locationResult!!.lastLocation
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
                updateLocationUI()
            }
        }
    }

    /**
     * Uses a [com.google.android.gms.location.LocationSettingsRequest.Builder] to build
     * a [com.google.android.gms.location.LocationSettingsRequest] that is used for checking
     * if a device has the needed location settings.
     */
    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
        // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.i(tag, "User agreed to make required location settings changes.")
                Activity.RESULT_CANCELED -> {
                    Log.i(tag, "User chose not to make required location settings changes.")
                    mRequestingLocationUpdates = false
                    updateUI()
                }
            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    fun startUpdatesButtonHandler(view: View) {
        if (!(mRequestingLocationUpdates)!!) {
            mRequestingLocationUpdates = true
            setButtonsEnabledState()
            startLocationUpdates()
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    fun stopUpdatesButtonHandler(view: View) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates()
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this) {
                    Log.i(tag, "All location settings are satisfied.")

                    mFusedLocationClient!!.requestLocationUpdates(mLocationRequest,
                            mLocationCallback!!, Looper.myLooper())

                    updateUI()
                }
                .addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(tag, "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(this@TrackingActivity, REQUEST_CHECK_SETTINGS)
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.i(tag, "PendingIntent unable to execute request.")
                            }

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            Log.e(tag, errorMessage)
                            Toast.makeText(this@TrackingActivity, errorMessage, Toast.LENGTH_LONG).show()
                            mRequestingLocationUpdates = false
                        }
                    }

                    updateUI()
                }
    }

    /**
     * Updates all UI fields.
     */
    private fun updateUI() {
        setButtonsEnabledState()
        updateLocationUI()
    }

    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */
    private fun setButtonsEnabledState() {
        if (mRequestingLocationUpdates!!) {
            mStartUpdatesButton!!.setEnabled(false)
            mStopUpdatesButton!!.setEnabled(true)
        } else {
            mStartUpdatesButton!!.setEnabled(true)
            mStopUpdatesButton!!.setEnabled(false)
        }
    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private fun updateLocationUI() {
        if (mCurrentLocation != null) {
            mLatitudeTextView!!.text = String.format(Locale.ENGLISH, "%s: %f", mLatitudeLabel,
                    mCurrentLocation!!.latitude)
            mLongitudeTextView!!.text = String.format(Locale.ENGLISH, "%s: %f", mLongitudeLabel,
                    mCurrentLocation!!.longitude)
            mLastUpdateTimeTextView!!.text = String.format(Locale.ENGLISH, "%s: %s",
                    mLastUpdateTimeLabel, mLastUpdateTime)
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private fun stopLocationUpdates() {
        if (!(mRequestingLocationUpdates)!!) {
            Log.d(tag, "stopLocationUpdates: updates never requested, no-op.")
            return
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
                .addOnCompleteListener(this) {
                    mRequestingLocationUpdates = false
                    setButtonsEnabledState()
                }
    }

    public override fun onResume() {
        super.onResume()
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates!! && checkPermissions()) {
            startLocationUpdates()
        } else if (!checkPermissions()) {
            requestPermissions()
        }

        updateUI()
    }

    override fun onPause() {
        super.onPause()

        // Remove location updates to save battery.
        stopLocationUpdates()
    }

    /**
     * Stores activity data in the Bundle.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates!!)
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation)
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime)
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Snackbar.make(
                findViewById<View>(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show()
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(tag, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this@TrackingActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
            })
        } else {
            Log.i(tag, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@TrackingActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.i(tag, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(tag, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates!!) {
                    Log.i(tag, "Permission granted, updates requested, starting location updates")
                    startLocationUpdates()
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, View.OnClickListener {
                    // Build intent that displays the App settings screen.
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                })
            }
        }
    }
}
