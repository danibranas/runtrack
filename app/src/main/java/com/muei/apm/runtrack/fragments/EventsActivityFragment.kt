package com.muei.apm.runtrack.fragments

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.fixtures.EventsFixture
import com.muei.apm.runtrack.fragments.events.EventsRecyclerAdapter

class EventsActivityFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        initializeEventList(view)

        return view
    }

    private fun initializeEventList(view: View) {
        val eventsRecyclerView: RecyclerView = view.findViewById(R.id.event_list)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        eventsRecyclerView.setHasFixedSize(true)

        // use a linear layout manager
        eventsRecyclerView.layoutManager = LinearLayoutManager(this.context)

        // specify an adapter
        eventsRecyclerView.adapter = EventsRecyclerAdapter(EventsFixture.generate(), context)
    }
}
