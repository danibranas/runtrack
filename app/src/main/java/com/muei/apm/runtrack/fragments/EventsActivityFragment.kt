package com.muei.apm.runtrack.fragments

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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

    private var mode: Mode = Mode.MODE_NEAR_EVENTS

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
                if (init) {
                    showLoading(view)
                }

                Handler().postDelayed({ // FIXME
                    // This method will be executed once the timer is over
                    val a = api.fetchNearEvents(0, 0)
                    a.onResult(fun (list: List<Event>) {
                        hideLoading(view)
                        setViewByContent(list, view)
                        eventsRecyclerView.adapter = EventsRecyclerAdapter(list, context!!)
                    })
                }, 1500)
            }
            Mode.MODE_MY_EVENTS -> {
                storage.getMyFinishedEvents().observe(this, Observer<List<Event>> {
                    setViewByContent(it!!, view)
                    eventsRecyclerView.adapter = EventsRecyclerAdapter(it, context!!)
                })
            }
            Mode.MODE_UPCOMING_EVENTS -> {
                storage.getMyUpcomingEvents().observe(this, Observer<List<Event>> {
                    setViewByContent(it!!, view)
                    eventsRecyclerView.adapter = EventsRecyclerAdapter(it, context!!)
                })
            }
        }

        val fabButton = view.findViewById<FloatingActionButton>(R.id.fab)

        fabButton.setOnClickListener((activity as EventsActivity)::onAddEventClick)
    }

    private fun showLoading(view: View) {
        view.findViewById<ProgressBar>(R.id.loading_spinner)?.visibility = View.VISIBLE
    }

    private fun hideLoading(view: View) {
        view.findViewById<ProgressBar>(R.id.loading_spinner)?.visibility = View.GONE
    }

    private fun setViewByContent(list: List<*>, view: View) {
        val noEvents = view.findViewById<ViewGroup>(R.id.content_no_events)
        if (list.isEmpty()) {
            noEvents.visibility = View.VISIBLE
            return
        }
        noEvents.visibility = View.GONE
    }
}
