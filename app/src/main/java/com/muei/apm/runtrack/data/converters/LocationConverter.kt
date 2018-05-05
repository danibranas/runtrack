package com.muei.apm.runtrack.data.converters

import com.muei.apm.runtrack.data.models.Location
import com.muei.apm.runtrack.data.persistence.entities.location.LocationEntity

class LocationConverter {
    companion object: Converter<Location, LocationEntity> {
        override fun entityToModel(e: LocationEntity): Location {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun modelToEntity(m: Location): LocationEntity {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}