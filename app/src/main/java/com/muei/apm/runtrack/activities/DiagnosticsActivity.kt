package com.muei.apm.runtrack.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.utils.GooglePlayServices
import com.muei.apm.runtrack.utils.MediaUtils
import com.muei.apm.runtrack.utils.Sensors

class DiagnosticsActivity : AppCompatActivity(), SensorEventListener {

    private var sensors: Sensors? = null
    private var stepCounterSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnostics)

        sensors = Sensors(getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        stepCounterSensor = sensors?.getStepCounterSensor()

        checkGooglePlayServices()
        checkSensors()
    }

    override fun onResume() {
        super.onResume()
        if (sensors!!.checkStepCounter()) {
            sensors!!.sensorManager.registerListener(this, stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()

        if (sensors!!.checkStepCounter()) {
            sensors!!.sensorManager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (sensors!!.checkStepCounter()) {
            val steps = event!!.values[0].toInt()
            val stepsText = "Steps counted: $steps"
            findViewById<TextView>(R.id.sensor_check_steps)?.text = stepsText
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun checkGooglePlayServices() {
        var text = "- Google Play Services: ${GooglePlayServices(this).isAvailable()}"
        text = "$text\n- Media Apps: ${MediaUtils(this).checkMediaCaptureApps()}"

        findViewById<TextView>(R.id.location_check_text).text = text
    }

    private fun checkSensors() {
        val m = mutableMapOf<String, Boolean?>()
        m["Heart Rate"] = sensors?.checkHeartRate()
        m["Step Counter"] = sensors?.checkStepCounter()
        m["Step Detector"] = sensors?.checkStepDetector()

        val result = StringBuilder()
        m.forEach {
            (name, value) -> result.append("\n$name: $value")
        }

        findViewById<TextView>(R.id.sensor_check_text)?.text = result
    }
}
