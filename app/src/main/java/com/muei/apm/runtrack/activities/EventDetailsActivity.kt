package com.muei.apm.runtrack.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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

        findViewById<Button>(R.id.join_event_button).setOnClickListener {
            val intent = Intent(this, TrackingActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, eventId!!)
            startActivity(intent)
        }
    }
}
