package com.muei.apm.runtrack.activities

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.data.api.Api
import com.muei.apm.runtrack.data.api.ApiFactory
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.data.persistence.AppDatabase
import com.muei.apm.runtrack.data.persistence.Service
import com.muei.apm.runtrack.data.persistence.ServiceDb
import com.muei.apm.runtrack.utils.EventUtils

class EventDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
        const val EXTRA_EVENT_FROM_TRACKING = "EXTRA_EVENT_FROM_TRACKING"
        const val EXTRA_EVENT_NAME = "EXTRA_EVENT_NAME"
    }

    private val api: Api by lazy {
        ApiFactory.getApi(this)
    }

    private val database: Service by lazy {
        ServiceDb(AppDatabase.getInstance(this)!!, this)
    }

    private var event: Event? = null

    private var joined: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val eventId = savedInstanceState?.getLong(EXTRA_EVENT_ID)
                ?: intent.getLongExtra(EXTRA_EVENT_ID, -1)

        api.findEventById(eventId).onResult(::initializeActivity)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putLong(EXTRA_EVENT_ID, event?.id ?: -1)
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

        database.getEventById(event.id).observe(this, Observer {
            if (it != null) {
                if (it.endDate != null) {
                    setEventAsFinished()
                } else {
                    setEventAsJoined()
                }
            } else {
                setEventAsUnJoined()
            }
        })
    }

    private fun setEventAsFinished() {
        this.joined = true
        findViewById<Button>(R.id.join_event_button).visibility = View.GONE

        findViewById<ImageView>(R.id.event_map_preview).setOnClickListener {
            val intent = Intent(this, EventMapActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event?.id ?: -1)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event?.name)
            startActivity(intent)
        }
    }

    private fun setEventAsJoined() {
        this.joined = true
        val joinButton = findViewById<Button>(R.id.join_event_button)

        joinButton.text = resources.getText(R.string.event_start_button)
        joinButton.visibility = View.VISIBLE
        joinButton.setOnClickListener {
            val intent = Intent(this, TrackingActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event?.id ?: -1)
            startActivity(intent)
        }
    }

    private fun setEventAsUnJoined() {
        this.joined = false
        val joinButton = findViewById<Button>(R.id.join_event_button)

        joinButton.text = resources.getText(R.string.event_join_button)
        joinButton.visibility = View.VISIBLE
        joinButton.setOnClickListener {
            database.joinEvent(event!!)
            showToast(resources.getText(R.string.event_joined))
            setEventAsJoined()
        }
    }

    private fun showToast(message: CharSequence, isLong: Boolean = false) =
            Toast.makeText(
                    this,
                    message,
                    if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
}
