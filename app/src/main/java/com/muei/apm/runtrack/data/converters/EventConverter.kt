package com.muei.apm.runtrack.data.converters

import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.event.EventUnit
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntity
import java.util.*

class EventConverter {
    companion object: Converter<Event, EventEntity> {
        override fun entityToModel(e: EventEntity): Event {
            val event = Event(e.id)

            if (e.startDate != null) {
                event.date = Date(e.startDate!!)
            }

            event.name = e.name
            event.distance = e.distance
            event.imageUri = e.imageUri
            event.isInternal = e.isInternal
            event.users = e.users
            event.prize = e.prize

            if (e.endDate != null) {
                event.endDate = Date(e.endDate!!)

                event.results = Event.Results(
                        e.avgSpeed,
                        e.totalTime,
                        e.avgPace,
                        e.calories,
                        e.positiveRamp,
                        e.position
                )
            }

            if (e.unit != null) {
                event.unit = EventUnit.valueOf(e.unit!!)
            }

            if (e.coordinateLat != null) {
                event.coordinates = Event.Coordinates(
                        e.coordinateLat!!,
                        e.coordinateLng!!
                )
            }

            return event
        }

        override fun modelToEntity(m: Event): EventEntity {
            var eventEntity = EventEntity(
                    m.name,
                    m.date?.time
            )
            eventEntity.id = m.id
            eventEntity.distance = m.distance
            eventEntity.coordinateLat = m.coordinates?.lat
            eventEntity.coordinateLng = m.coordinates?.long
            eventEntity.endDate = m.endDate?.time
            eventEntity.imageUri = m.imageUri
            eventEntity.prize = m.prize
            eventEntity.users = m.users
            eventEntity.isInternal = m.isInternal

            eventEntity = modelToEntityResults(m.results, eventEntity)

            return eventEntity
        }

        fun modelToEntityResults(r: Event.Results?, eventEntity: EventEntity): EventEntity {
            if (r != null) {
                with (r) {
                    eventEntity.avgSpeed = avgSpeed
                    eventEntity.totalTime = totalTime
                    eventEntity.avgPace = avgPace
                    eventEntity.calories = calories
                    eventEntity.positiveRamp = positiveRamp
                    eventEntity.position = position
                }
            }

            return eventEntity
        }
    }
}