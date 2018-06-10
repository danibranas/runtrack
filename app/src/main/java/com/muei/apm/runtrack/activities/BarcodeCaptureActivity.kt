package com.muei.apm.runtrack.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.muei.apm.runtrack.R

class BarcodeCaptureActivity : AppCompatActivity() {

    companion object {
        // Used in checking for runtime permissions.
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.add_event_by_qr)

        // Only available for SDK >= 19
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            finish()
            return
        }

        setContentView(R.layout.activity_barcode_capture)

        if (!checkPermissions()) {
            requestPermissions()
        }

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val intent = Intent(this, EventDetailsActivity::class.java)
                intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, it.text.toLongOrNull())
                startActivity(intent)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                finish()
                Toast.makeText(this, "Camera initialization error: " + it.message,
                        Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TrackingActivity.TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                    findViewById(R.id.activity_barcode_capture),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, {
                        // JsonRequest permission
                        ActivityCompat.requestPermissions(this@BarcodeCaptureActivity,
                                arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSIONS_REQUEST_CODE)
                    })
                    .show()
        } else {
            Log.i(TrackingActivity.TAG, "Requesting permission")
            // JsonRequest permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@BarcodeCaptureActivity,
                    arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}