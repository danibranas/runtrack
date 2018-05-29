package com.muei.apm.runtrack.utils

import com.muei.apm.runtrack.data.models.Event
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EventUtils {
    companion object {
        fun getDay(event: Event): String {
            val calendar = Calendar.getInstance()
            calendar.time = event.date
            return calendar.get(Calendar.DATE).toString()
        }

        fun getMonthName(event: Event): String {
            val fmt = SimpleDateFormat("MMM", Locale.ENGLISH)
            return fmt.format(event.date)
        }

        fun formatDistance(event: Event): String {
            return "${event.distance} ${event.unit.toString()}"
        }

        fun formatDouble(num: Double): String {
            if (num.isInfinite()) {
                return "∞"
            }

            return "%.2f".format(num)
        }
    }
}