package com.muei.apm.runtrack.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.models.Event

class EventDetailsFragment : Fragment() {

    val event: Event? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }
}
