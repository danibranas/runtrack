package com.muei.apm.runtrack.activities

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
import com.muei.apm.runtrack.utils.SocialMediaUtils
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.muei.apm.runtrack.utils.TrackingUtils


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
        findViewById<TextView>(R.id.event_description).text = event.description
        findViewById<TextView>(R.id.people_text).text = String.format(
                resources.getString(R.string.event_people_number), event.users)

        findViewById<ImageButton>(R.id.share_twitter_button).setOnClickListener {
            val tweet = SocialMediaUtils.getTwitterIntent(this,
                    "Hey! ${event.name} is awesome. I'll run it on Runtrack!")
            startActivity(tweet)
        }

        findViewById<ImageView>(R.id.event_map_preview).setOnClickListener {
            val intent = Intent(this, EventMapActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event.name)
            startActivity(intent)
        }
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
        findViewById<TextView>(R.id.event_description_title).visibility = View.GONE
        findViewById<TextView>(R.id.event_description).visibility = View.GONE
        findViewById<ViewGroup>(R.id.include_activity_event_details_stats).visibility = View.VISIBLE

        setStatsData()
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

    private fun setStatsData() {
        database.getLocationsByEventId(event!!.id)
                .observe(this, Observer {
                    if (it != null) {
                        val start = it.firstOrNull()
                        val end = it.lastOrNull()

                        if (start != null && end != null) {
                            val speed = TrackingUtils.calculateSpeed(start, end)
                            findViewById<TextView>(R.id.event_time).text =
                                    ((end.date.time - start.date.time)/(60000)).toString() // FIXME: minutes
                            findViewById<TextView>(R.id.event_avg_speed).text =
                                    TrackingUtils.speedToKmH(speed).toString()
                            findViewById<TextView>(R.id.event_pace).text =
                                    TrackingUtils.speedToPace(speed).toString()

                        }

                        // Graph
                        val graph = findViewById<GraphView>(R.id.speed_graph)
                        // TODO: ...
                        val series = LineGraphSeries<DataPoint>(arrayOf(
                                DataPoint(0.0, 1.0),
                                DataPoint(1.0, 5.0),
                                DataPoint(2.0, 3.0)
                        ))
                        graph.addSeries(series)
                    }
                })
    }
}
