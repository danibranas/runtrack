package com.muei.apm.runtrack.data.converters

import com.muei.apm.runtrack.data.models.Location
import com.muei.apm.runtrack.data.persistence.entities.location.LocationEntity
import java.util.Date

class LocationConverter {
    companion object: Converter<Location, LocationEntity> {
        override fun entityToModel(e: LocationEntity): Location {
            return Location(e.latitude, e.longitude, Date(e.date!!))
        }

        override fun modelToEntity(m: Location): LocationEntity {
            return modelToEntity(m, -1)
        }

        fun modelToEntity(m: Location, eventId: Long): LocationEntity {
            val e = LocationEntity(
                    m.latitude,
                    m.longitude,
                    eventId
            )

            e.date = m.date.time

            return e
        }

    }
}