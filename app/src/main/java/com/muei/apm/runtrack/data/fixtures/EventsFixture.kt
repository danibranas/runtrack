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

        private var description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
                "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint " +
                "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                "id est laborum."

        private var eventId: Long = 0

        private var route = listOf(
                Location(43.3767151, -8.4234881),
                Location(43.3766059, -8.4222865),
                Location(43.3760912, -8.4212995),
                Location(43.3748435, -8.4203553),
                Location(43.3736269, -8.4198403),
                Location(43.3730498, -8.4191537),
                Location(43.3721295, -8.4190679),
                Location(43.3715056, -8.4187889),
                Location(43.3711001, -8.4183383),
                Location(43.3705541, -8.4168577),
                Location(43.370133, -8.4157848),
                Location(43.3696494, -8.4152484),
                Location(43.3684639, -8.4138536),
                Location(43.3681832, -8.4131455),
                Location(43.3680584, -8.4118366),
                Location(43.36823, -8.4107208),
                Location(43.3684327, -8.4094119),
                Location(43.3690879, -8.4074378),
                Location(43.3698679, -8.4057212)
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
            event.description = description
            event.users = Random().nextInt(1000)

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