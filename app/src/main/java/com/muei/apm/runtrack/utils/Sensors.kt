package com.muei.apm.runtrack.utils

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build

class Sensors(val sensorManager: SensorManager) {
    fun checkHeartRate(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null
        }
        return false
    }

    fun checkStepDetector(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null
        }
        return false
    }

    fun checkStepCounter(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
        }
        return false
    }

    @SuppressLint("InlinedApi")
    fun getStepCounterSensor(): Sensor? {
        if (checkStepCounter()) {
            return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        }
        return null
    }
}