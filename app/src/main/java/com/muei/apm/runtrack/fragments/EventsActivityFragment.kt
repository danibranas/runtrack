package com.muei.apm.runtrack.fragments

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.activities.EventsActivity
import com.muei.apm.runtrack.data.api.Api
import com.muei.apm.runtrack.data.api.ApiFactory
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.persistence.AppDatabase
import com.muei.apm.runtrack.data.persistence.Service
import com.muei.apm.runtrack.data.persistence.ServiceDb
import com.muei.apm.runtrack.fragments.events.EventsRecyclerAdapter

class EventsActivityFragment : Fragment() {

    companion object {
        enum class Mode {
            MODE_NEAR_EVENTS,
            MODE_UPCOMING_EVENTS,
            MODE_MY_EVENTS
        }
    }

    private val api: Api by lazy {
        ApiFactory.getApi(context!!)
    }

    private val storage: Service by lazy {
        ServiceDb(AppDatabase.getInstance(context!!)!!, this)
    }

    var mode: Mode = Mode.MODE_NEAR_EVENTS

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_events, container, false)

        // Config
        initializeSwipeRefresh(view)
        loadEventList(view, true)

        return view
    }

    fun setMode(mode: Mode): EventsActivityFragment {
        this.mode = mode
        return this
    }

    private fun initializeSwipeRefresh(view: View) {
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefresh.setOnRefreshListener {
            loadEventList(view)
            swipeRefresh.isRefreshing = false
        }
    }

    private fun loadEventList(view: View, init: Boolean = false) {
        val eventsRecyclerView: RecyclerView = view.findViewById(R.id.event_list)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // eventsRecyclerView.setHasFixedSize(true)

        if (init) {
            // use a linear layout manager
            eventsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        }

        // Specifying an adapter
        when (mode) {
            Mode.MODE_NEAR_EVENTS -> {
                val a = api.fetchNearEvents(0, 0)
                a.onResult(fun (list: List<Event>) {
                    eventsRecyclerView.adapter = EventsRecyclerAdapter(list, context!!)
                })
            }
            Mode.MODE_MY_EVENTS -> {
                storage.getMyFinishedEvents().observe(this, Observer<List<Event>> {
                    eventsRecyclerView.adapter = EventsRecyclerAdapter(it!!, context!!)
                })
            }
            Mode.MODE_UPCOMING_EVENTS -> {
                storage.getMyUpcomingEvents().observe(this, Observer<List<Event>> {
                    eventsRecyclerView.adapter = EventsRecyclerAdapter(it!!, context!!)
                })
            }
        }

        val fabButton = view.findViewById<FloatingActionButton>(R.id.fab)
        val appBar = view.findViewById<AppBarLayout?>(R.id.events_app_bar)

        fabButton.setOnClickListener((activity as EventsActivity)::onAddEventClick)

        eventsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && fabButton.isShown) {
                    fabButton.visibility = View.GONE
                    appBar?.visibility = View.VISIBLE
                } else if (dy > 0 ) {
                    fabButton.visibility = View.VISIBLE
                    appBar?.visibility = View.GONE
                }
            }
        })
    }
}
