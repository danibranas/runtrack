package com.muei.apm.runtrack.fragments


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.utils.Sensors

class SensorCheckFragment : Fragment(), SensorEventListener {
    private var sensors: Sensors? = null
    private var stepCounterSensor: Sensor? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        sensors = Sensors(activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        stepCounterSensor = sensors?.getStepCounterSensor()

        val view = inflater.inflate(R.layout.fragment_sensor_check, container, false)
        checkSensors(view)

        return view
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
            view!!.findViewById<TextView>(R.id.sensor_check_steps).text = "Steps counted: $steps"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Accuracy has changed
    }

    private fun checkSensors(view: View) {
        val m = mutableMapOf<String, Boolean?>()
        m["Heart Rate"] = sensors?.checkHeartRate()
        m["Step Counter"] = sensors?.checkStepCounter()
        m["Step Detector"] = sensors?.checkStepDetector()

        val result = StringBuilder()
        m.forEach {
            (name, value) -> result.append("\n$name: $value")
        }

        view.findViewById<TextView>(R.id.sensor_check_text)?.text = result
    }
}
