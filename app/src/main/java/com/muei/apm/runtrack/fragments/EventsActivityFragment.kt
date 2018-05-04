package com.muei.apm.runtrack.fragments

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
import com.muei.apm.runtrack.data.fixtures.EventsFixture
import com.muei.apm.runtrack.fragments.events.EventsRecyclerAdapter

class EventsActivityFragment() : Fragment() {

    companion object {
        enum class Mode {
            MODE_NEAR_EVENTS,
            MODE_UPCOMING_EVENTS,
            MODE_MY_EVENTS
        }
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

        // specify an adapter
        // TODO: get from database
        eventsRecyclerView.adapter = EventsRecyclerAdapter(EventsFixture.generate(), context)

        val fabButton = view.findViewById<FloatingActionButton>(R.id.fab)
        val appBar = view.findViewById<AppBarLayout?>(R.id.events_app_bar)

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
