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
                "II La carrera returns"
        )

        fun generate(length: Int = 5): List<Event> {
            return (0..length.absoluteValue).map {
                _ -> generateRandomEvent()
            }
        }

        private fun generateRandomEvent(): Event {
            val event = Event()
            event.name = names[Random().nextInt(names.size)]
            event.distance = this.generateRandomFloat(50)
            // TODO: generate random values
            return event
        }

        private fun generateRandomFloat(bound: Int): Float {
            return (Random().nextInt(bound) + Random().nextInt(10).toFloat()/10)
        }
    }
}