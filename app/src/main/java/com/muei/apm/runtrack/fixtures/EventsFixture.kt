package com.muei.apm.runtrack.fixtures

import com.muei.apm.runtrack.models.Event
import java.util.*
import kotlin.math.absoluteValue

class EventsFixture {
    companion object {
        private val names = arrayOf(
                "Carrera Montecarlo",
                "III Carrera Madrid",
                "XII Milla solidaria",
                "XVII Carrera increible",
                "II La carrera returns",
                "II Carrera de obstáculos"
        )

        fun generate(length: Int = 5): List<Event> {
            return (0..length.absoluteValue).map {
                _ -> generateRandomEvent()
            }
        }

        private fun generateRandomEvent(): Event {
            val event = Event(generateRandomId())

            event.name = names[Random().nextInt(names.size)]
            event.distance = this.generateRandomFloat(50)
            event.date = generateRandomDate()

            return event
        }

        private fun generateRandomFloat(bound: Int): Float {
            return (Random().nextInt(bound) + Random().nextInt(10).toFloat()/10)
        }

        private fun generateRandomId(): Long {
            return Random().nextLong()
        }

        private fun generateRandomDate(): Date {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, Random().nextInt(60))
            calendar.add(Calendar.HOUR, Random().nextInt(10))
            calendar.add(Calendar.MINUTE, Random().nextInt(30))
            return calendar.time
        }
    }
}