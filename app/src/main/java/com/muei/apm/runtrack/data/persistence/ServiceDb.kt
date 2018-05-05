package com.muei.apm.runtrack.data.persistence

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.muei.apm.runtrack.data.converters.EventConverter
import com.muei.apm.runtrack.data.converters.LocationConverter
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.models.Location
import com.muei.apm.runtrack.data.persistence.entities.event.EventEntity
import com.muei.apm.runtrack.data.persistence.entities.location.LocationEntity
import java.util.Date


class ServiceDb(private val db: AppDatabase, private val owner: LifecycleOwner): Service {

    override fun joinEvent(event: Event) {
        db.eventDao().insert(EventConverter.modelToEntity(event))
    }

    override fun unjoinEventById(eventId: Long) {
        db.eventDao().deleteById(eventId)
    }

    override fun getLocationsByEventId(eventId: Long): LiveData<List<Location>> {
        val data = MutableLiveData<List<Location>>()
        db.locationDao()
                .findByEventId(eventId)
                .observe(owner, Observer<List<LocationEntity>> {
                    data.postValue(it!!.map { e -> LocationConverter.entityToModel(e) })
                })
        return data
    }

    override fun startEventTracking(eventId: Long) {
        db.eventDao().findById(eventId).observe(owner, Observer {
            if (it != null) {
                it.joinDate = Date().time
                db.eventDao().update(it)
            }
        })
    }

    override fun registerEventLocation(eventId: Long, location: Location) {
        val locationEntity = LocationConverter.modelToEntity(location)
        locationEntity.eventId = eventId
        db.locationDao().insert(locationEntity)
    }

    override fun getMyFinishedEvents(): LiveData<List<Event>> {
        val data = MutableLiveData<List<Event>>()
        db.eventDao()
                .getFinishedEvents()
                .observe(owner, Observer<List<EventEntity>> {
                    data.postValue(it?.map { e -> EventConverter.entityToModel(e) })
                })
        return data
    }

    override fun getMyUpcomingEvents(): LiveData<List<Event>> {
        val data = MutableLiveData<List<Event>>()

        db.eventDao()
                .getUpcomingEvents()
                .observe(owner, Observer<List<EventEntity>> {
                    data.postValue(it?.map { e -> EventConverter.entityToModel(e) })
                })

        return data
    }

    override fun getEventById(eventId: Long): LiveData<Event?> {
        val data = MutableLiveData<Event>()
        db.eventDao()
                .findById(eventId)
                .observe(owner, Observer {
                    data.postValue(if (it != null) EventConverter.entityToModel(it) else null)
                })
        return data
    }

}