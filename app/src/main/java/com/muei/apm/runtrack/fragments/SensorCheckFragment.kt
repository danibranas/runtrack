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
    private var sensorManager: SensorManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val view = inflater.inflate(R.layout.fragment_sensor_check, container, false)
        checkSensors(view)

        return view
    }

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkSensors(view: View) {
        val sensors = Sensors(sensorManager!!)

        val m = mutableMapOf<String, Boolean>()
        m["Heart Rate"] = sensors.checkHeartRate()
        m["Step Counter"] = sensors.checkStepCounter()
        m["Step Detector"] = sensors.checkStepDetector()

        val result = StringBuilder()
        m.forEach {
            (name, value) -> result.append("\n$name: $value")
        }

        view.findViewById<TextView>(R.id.sensor_check_text)?.text = result
    }
}
