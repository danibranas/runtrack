package com.muei.apm.runtrack.activities

import android.Manifest
import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.muei.apm.runtrack.BuildConfig
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.services.LocationUpdatesService
import com.muei.apm.runtrack.utils.LocationUtils
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.muei.apm.runtrack.activities.tracking.TrackingReceiver
import com.muei.apm.runtrack.data.api.Api
import com.muei.apm.runtrack.data.api.ApiFactory
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.Location
import com.muei.apm.runtrack.data.persistence.AppDatabase
import com.muei.apm.runtrack.data.persistence.Service
import com.muei.apm.runtrack.data.persistence.ServiceDb
import com.muei.apm.runtrack.utils.EventUtils
import com.muei.apm.runtrack.utils.PausableChronometer
import com.muei.apm.runtrack.utils.TrackingUtils
import java.util.*
import kotlin.math.roundToInt


class TrackingActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
        OnMapReadyCallback {
    companion object {
        val TAG: String = TrackingActivity::class.java.simpleName

        // Used in checking for runtime permissions.
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

        const val EXTRA_CHRONO_TIME = "EXTRA_CHRONO_TIME"
        const val EXTRA_CURRENT_PROGRESS = "EXTRA_CURRENT_PROGRESS"
    }

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private var myReceiver: BroadcastReceiver? = null

    private val route = arrayListOf<LatLng>()

    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    private var mBound = false

    // UI elements.
    private var mStartPauseTrackingButton: FloatingActionButton? = null
    private var mStopTrackingButton: FloatingActionButton? = null
    private var mapFragment: View? = null
    private var chrono: PausableChronometer? = null
    private var distanceMeasure: TextView? = null
    private var paceMeasure: TextView? = null
    private var distanceMeasureUnit: TextView? = null
    private var paceMeasureUnit: TextView? = null
    private var progressBar: ProgressBar? = null

    private var backButtonDisabled = false

    // Control vars
    private var lastDistance: Double = 0.0

    // Restore vars
    private var chronoTime: Long = 0
    private var currentProgress: Int = 0

    // Api and Storage
    private val api: Api by lazy {
        ApiFactory.getApi(this)
    }
    private val database: Service by lazy {
        ServiceDb(AppDatabase.getInstance(this)!!, this)
    }

    private var event: Event? = null

    private val mServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            mBound = false
        }
    }

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        myReceiver = TrackingReceiver({
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
            addRouteSegment(it)
            updateStats(it)
            // TODO: Update
        })

        setContentView(R.layout.activity_tracking)
        supportActionBar?.hide()

        val eventId = savedInstanceState?.getLong(EventDetailsActivity.EXTRA_EVENT_ID)
                ?: intent.getLongExtra(EventDetailsActivity.EXTRA_EVENT_ID, -1)
        chronoTime = savedInstanceState?.getLong(TrackingActivity.EXTRA_CHRONO_TIME) ?: 0
        currentProgress = savedInstanceState?.getInt(TrackingActivity.EXTRA_CURRENT_PROGRESS) ?: 0

        database.getEventById(eventId).observe(this, Observer {
            event = it
        })

        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)

        // Check that the user hasn't revoked permissions by going to Settings.
        if (!checkPermissions()) {
            requestPermissions()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            (findViewById<ViewGroup>(R.id.activity_tracking))
                    .layoutTransition
                    .enableTransitionType(LayoutTransition.CHANGING)
        }
    }

    private fun addRouteSegment(point: LatLng) {
        val lastPoint = route.lastOrNull()
        val pointList = arrayListOf<LatLng>()

        if (route.size == 1) {
            map?.addMarker(MarkerOptions().position(point))
        }

        route.add(point)

        if (lastPoint != null) {
            pointList.add(lastPoint)
            pointList.add(point)

            map?.addPolyline(PolylineOptions()
                    .width(20f)
                    .color(Color.YELLOW)
                    .geodesic(true)
                    .addAll(pointList)
            )
        }

        database.registerEventLocation(event!!.id, Location(point.latitude, point.longitude, Date()))
        // TODO: update distance
    }

    private fun updateStats(point: LatLng) {
        database.getLocationsByEventId(event!!.id).observe(this, Observer {
            val totalDistance = TrackingUtils.calculateDistance(it!!)

            if (lastDistance > totalDistance) {
                // A next async-updateStats has been set before this one
                return@Observer
            }

            val goalDistance = event!!.distance!! * 1000
            val distancePercent = TrackingUtils.calculateDistancePercent(goalDistance, totalDistance)

            val firstPoint = it.firstOrNull()
            val avgPace = TrackingUtils.calculatePace(firstPoint, Location(point), totalDistance)

            distanceMeasure?.text = EventUtils.formatDouble(TrackingUtils.distanceToKm(totalDistance))
            paceMeasure?.text = EventUtils.formatDouble(avgPace)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar?.setProgress(distancePercent.roundToInt(), true)
            } else {
                progressBar?.progress = distancePercent.roundToInt()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this)

        mStartPauseTrackingButton = findViewById(R.id.start_pause_tracking_button)
        mStopTrackingButton = findViewById(R.id.stop_tracking_button)
        chrono = PausableChronometer(findViewById(R.id.chronometer))
        distanceMeasure = findViewById(R.id.distance_measure)
        paceMeasure = findViewById(R.id.pace_measure)
        distanceMeasureUnit = findViewById(R.id.distance_measure_unit)
        paceMeasureUnit = findViewById(R.id.pace_measure_unit)
        mapFragment = findViewById(R.id.map)
        progressBar = findViewById(R.id.track_progress_bar)

        chrono!!.setTime(chronoTime)

        mStartPauseTrackingButton!!.setOnClickListener({ toggleLocationUpdates() })
        mStopTrackingButton!!.setOnClickListener({
            AlertDialog.Builder(this)
                    .setMessage(R.string.tracking_stop_question)
                    .setPositiveButton(R.string.stop, { _: DialogInterface, _: Int ->
                        chrono!!.reset()
                        mService!!.removeLocationUpdates()
                        // Save on db and notify backend
                        val results = calculateEventStats()
                        database.finishEventTracking(event!!.id, Date(), results)
                        api.finishEventById(event!!.id, results)
                        startEventDetailsActivity()
                    })
                    .setNegativeButton(R.string.no, null)
                    .show()
       })

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(LocationUtils.requestingLocationUpdates(this))

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(Intent(this, LocationUpdatesService::class.java), mServiceConnection,
                Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver!!,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver!!)
        super.onPause()
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onBackPressed() {
        if (!backButtonDisabled) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        bundle.run {
            putLong(EventDetailsActivity.EXTRA_EVENT_ID, event?.id ?: -1)
            putLong(TrackingActivity.EXTRA_CHRONO_TIME, chrono!!.getTime())
            putInt(TrackingActivity.EXTRA_CURRENT_PROGRESS, progressBar?.progress ?: 0)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val eventId = savedInstanceState?.getLong(EventDetailsActivity.EXTRA_EVENT_ID)
                ?: intent.getLongExtra(EventDetailsActivity.EXTRA_EVENT_ID, -1)
        chrono?.setTime(savedInstanceState?.getLong(TrackingActivity.EXTRA_CHRONO_TIME) ?: 0)

        database.getEventById(eventId).observe(this, Observer {
            event = it
        })
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun toggleLocationUpdates() {
        backButtonDisabled = true

        if (LocationUtils.requestingLocationUpdates(this)) {
            mService!!.removeLocationUpdates()
        } else {
            if (!checkPermissions()) {
                requestPermissions()
            } else {
                mService!!.requestLocationUpdates()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            initMapLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initMapLocation() {
        map?.isMyLocationEnabled = true

        FusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            val latLng = LatLng(it.latitude, it.longitude)
            addRouteSegment(latLng)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)

            map?.animateCamera(cameraUpdate)
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                    findViewById(R.id.activity_tracking),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, {
                        // JsonRequest permission
                        ActivityCompat.requestPermissions(this@TrackingActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_PERMISSIONS_REQUEST_CODE)
                    })
                    .show()
        } else {
            Log.i(TAG, "Requesting permission")
            // JsonRequest permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@TrackingActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "Event interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {// Permission was granted.
                    mService!!.requestLocationUpdates()
                    initMapLocation()
                }
                else -> {
                    // Permission denied.
                    setButtonsState(false)
                    Snackbar.make(
                            findViewById(R.id.activity_tracking),
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.settings, {
                                // Build intent that displays the App settings screen.
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

                                val uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null)
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            })
                            .show()
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s == LocationUtils.KEY_REQUESTING_LOCATION_UPDATES) {
            setButtonsState(sharedPreferences.getBoolean(LocationUtils.KEY_REQUESTING_LOCATION_UPDATES,
                    false))
        }
    }

    private fun setButtonsState(requestingLocationUpdates: Boolean) {
        if (requestingLocationUpdates) {
            mStopTrackingButton!!.visibility = View.VISIBLE
            paceMeasure!!.visibility = View.VISIBLE
            paceMeasureUnit!!.visibility = View.VISIBLE
            distanceMeasure!!.visibility = View.VISIBLE
            distanceMeasureUnit!!.visibility = View.VISIBLE
            mStartPauseTrackingButton!!.setImageResource(R.drawable.ic_pause_icon)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar!!.visibility = View.VISIBLE
            }

            chrono!!.chronometer?.visibility = View.VISIBLE
            chrono!!.start()
            doSetMarginBottomDp(mapFragment, 300)
        } else {
            mStartPauseTrackingButton!!.setImageResource(R.drawable.ic_play_icon)
            chrono!!.stop()
        }
    }

    private fun doSetMarginBottomDp(view: View?, marginBottom: Int) {
        val p = view?.layoutParams as ViewGroup.MarginLayoutParams?

        if (p != null) {
            val marginBottomDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, marginBottom.toFloat(),
                    resources.displayMetrics
            )

            p.bottomMargin = marginBottomDp.toInt()
            view?.requestLayout()
        }
    }

    private fun startEventDetailsActivity() {
        val intent = Intent(this, EventDetailsActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event?.id ?: -1)
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_FROM_TRACKING, true)
        startActivity(intent)
    }

    private fun calculateEventStats(): Event.Results {
        // TODO: calculate events on event finish and return them
        return Event.Results()
    }
}
