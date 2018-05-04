package com.muei.apm.runtrack.data.converters

import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntity
import java.util.*

class EventConverter {
    companion object: Converter<Event, EventEntity> {
        override fun entityToModel(e: EventEntity): Event {
            val event = Event(e.id)

            if (e.startDate != null) {
                event.date = Date(e.startDate!!)
            }

            event.joined = e.joined

            // TODO: ...

            return event
        }

        override fun modelToEntity(m: Event): EventEntity {
            val eventEntity = EventEntity(
                    m.name,
                    m.date?.time
            )
            eventEntity.joined = m.joined
            // TODO: ...
            return eventEntity
        }
    }
}