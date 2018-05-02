package com.muei.apm.runtrack.utils

import android.widget.Chronometer
import android.os.SystemClock

class PausableChronometer(val chronometer: Chronometer?) {
    private var timeWhenStopped: Long = 0

    fun start() {
        chronometer?.base = SystemClock.elapsedRealtime() + timeWhenStopped
        chronometer?.start()
    }

    fun stop() {
        chronometer?.stop()

        if (chronometer != null) {
            timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        }
    }

    fun reset() {
        stop()
        chronometer?.base = SystemClock.elapsedRealtime()
        timeWhenStopped = 0
    }
}