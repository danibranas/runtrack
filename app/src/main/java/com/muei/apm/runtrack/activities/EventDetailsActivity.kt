package com.muei.apm.runtrack.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.data.api.Api
import com.muei.apm.runtrack.data.api.ApiFactory
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.utils.EventUtils

class EventDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
    }

    private val api: Api by lazy {
        ApiFactory.getApi(this)
    }

    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1)

        api.findEventById(eventId).onResult(::initializeActivity)
    }

    private fun initializeActivity(event: Event?) {
        this.event = event


        if (event == null) {
            showToast("Ops! Event not found :(")
            return
        }

        findViewById<TextView>(R.id.event_title).text = event.name
        findViewById<TextView>(R.id.event_distance).text = EventUtils.formatDistance(event)
        findViewById<TextView>(R.id.event_date_day).text = EventUtils.getDay(event)
        findViewById<TextView>(R.id.event_date_month).text = EventUtils.getMonthName(event)
        // ...

        findViewById<Button>(R.id.join_event_button).setOnClickListener {
            val intent = Intent(this, TrackingActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
    }

    private fun showToast(message: CharSequence, isLong: Boolean = false) =
            Toast.makeText(
                    this,
                    message,
                    if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
}
