package com.muei.apm.runtrack.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.muei.apm.runtrack.R

class EventDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
    }

    private var eventId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1)
    }
}
