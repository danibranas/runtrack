package com.muei.apm.runtrack.data.fixtures

import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.Location
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

        private var eventId: Long = 0

        private var route = listOf(
                Location(-8.4234881, 43.3767151),
                Location(-8.4222865, 43.3766059),
                Location(-8.4212995, 43.3760912),
                Location(-8.4203553, 43.3748435),
                Location(-8.4198403, 43.3736269),
                Location(-8.4191537, 43.3730498),
                Location(-8.4190679, 43.3721295),
                Location(-8.4187889, 43.3715056),
                Location(-8.4183383, 43.3711001),
                Location(-8.4168577, 43.3705541),
                Location(-8.4157848, 43.370133),
                Location(-8.4152484, 43.3696494),
                Location(-8.4138536, 43.3684639),
                Location(-8.4131455, 43.3681832),
                Location(-8.4118366, 43.3680584),
                Location(-8.4107208, 43.36823),
                Location(-8.4094119, 43.3684327),
                Location(-8.4074378, 43.3690879),
                Location(-8.4057212, 43.3698679)
        )

        fun generate(length: Int = 5): List<Event> {
            return (0..length.absoluteValue).map {
                _ -> generateRandomEvent()
            }
        }

        private fun generateRandomEvent(): Event {
            val event = Event(generateRandomId())

            event.id = nextEventId()
            event.name = names[Random().nextInt(names.size)]
            event.distance = this.generateRandomFloat(50)
            event.date = generateRandomDate()
            event.route = route

            return event
        }

        private fun nextEventId(): Long {
            eventId++
            return eventId
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