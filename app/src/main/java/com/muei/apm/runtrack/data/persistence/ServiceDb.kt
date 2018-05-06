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
import com.muei.apm.runtrack.tasks.OperationTask
import java.util.Date


class ServiceDb(private val db: AppDatabase, private val owner: LifecycleOwner): Service {

    override fun joinEvent(event: Event): LiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        OperationTask<Unit>().execute({
            db.eventDao().insert(EventConverter.modelToEntity(event))
            data.postValue(true)
        })
        return data
    }

    override fun unjoinEventById(eventId: Long): LiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        OperationTask<Unit>().execute({
            db.eventDao().deleteById(eventId)
            data.postValue(true)
        })
        return data
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

    override fun startEventTracking(eventId: Long): LiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        db.eventDao().findById(eventId).observe(owner, Observer {
            if (it != null) {
                it.joinDate = Date().time
                db.eventDao().update(it)
                data.postValue(true)
                return@Observer
            }
            data.postValue(false)
        })
        return data
    }

    override fun finishEventTracking(eventId: Long, date: Date, results: Event.Results): LiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        db.eventDao()
                .findById(eventId)
                .observe(owner, Observer {
                    if (it != null) {
                        OperationTask<Unit>().execute({
                            val event = EventConverter.modelToEntityResults(results, it)
                            event.endDate = date.time
                            db.eventDao().update(event)
                            data.postValue(true)
                        })
                    } else {
                        db.eventDao().finishEventById(eventId, date.time)
                        data.postValue(true)
                    }
                })
        return data
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