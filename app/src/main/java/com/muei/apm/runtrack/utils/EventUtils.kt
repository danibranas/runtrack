package com.muei.apm.runtrack.utils

import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.event.EventStatus
import java.text.SimpleDateFormat
import java.util.*


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

        fun getStatus(event: Event?): EventStatus {
            return if (event != null) {
                if (event.endDate != null) {
                    EventStatus.FINISHED
                } else {
                    EventStatus.JOINED
                }
            } else {
                EventStatus.UNJOINED
            }
        }

        fun formatTimeDiff(dateA: Date, dateB: Date): String {
            val seconds = Math.abs(dateA.time - dateB.time)/1000
            val s = seconds % 60
            val m = seconds / 60 % 60
            val h = seconds / (60 * 60) % 24
            return String.format("%d:%02d:%02d", h, m, s)
        }

        fun formatDecimal(d: Double, unit: String = ""): String {
            return "%.2f$unit".format(d)
        }
    }
}