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
import com.muei.apm.runtrack.data.models.Location
import com.muei.apm.runtrack.data.models.event.EventStatus
import com.muei.apm.runtrack.tasks.DownloadImageTask
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
            finish()
            return
        }

        val eventPrize = if (event.prize != null) {
            String.format(resources.getString(R.string.event_prize_format), event.prize)
        } else {
            "-"
        }

        findViewById<TextView>(R.id.event_title).text = event.name
        findViewById<TextView>(R.id.event_distance).text = EventUtils.formatDistance(event)
        findViewById<TextView>(R.id.event_date_day).text = EventUtils.getDay(event)
        findViewById<TextView>(R.id.event_date_month).text = EventUtils.getMonthName(event)
        findViewById<TextView>(R.id.event_description).text = event.description
        findViewById<TextView>(R.id.event_prize).text = eventPrize
        findViewById<TextView>(R.id.people_text).text = String.format(
                resources.getString(R.string.event_people_number), event.users)

        val mapPreview = findViewById<ImageView>(R.id.event_map_preview)

        if (event.imageUri != null) {
            val bitmap = DownloadImageTask().execute(event.imageUri).get()
            mapPreview.setImageBitmap(bitmap)
        }
        mapPreview.setOnClickListener {
            val intent = Intent(this, EventMapActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event.name)
            startActivity(intent)
        }
        // ...

        database.getEventById(event.id).observe(this, Observer {
            var tweetText = "Hey! ${event.name} is awesome. I'll run it on Runtrack!"
            when (EventUtils.getStatus(it)) {
                EventStatus.JOINED -> {
                    setEventAsJoined()
                    val date = "${EventUtils.getMonthName(event)} ${EventUtils.getDay(event)}"
                    tweetText = "I'll participate ${event.name} on $date with Runtrack!"
                }
                EventStatus.FINISHED -> {
                    setEventAsFinished()
                    tweetText = "I finished ${event.name} race with Runtrack! :D"
                }
                else -> {
                    setEventAsUnJoined()
                }
            }

            findViewById<ImageButton>(R.id.share_twitter_button).setOnClickListener {
                val tweet = SocialMediaUtils.getTwitterIntent(this, tweetText)
                startActivity(tweet)
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

            event?.users = (event?.users ?: 0) + 1

            findViewById<TextView>(R.id.people_text).text = String.format(
                    resources.getString(R.string.event_people_number), event?.users)

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
                                    EventUtils.formatTimeDiff(end.date, start.date)
                            findViewById<TextView>(R.id.event_avg_speed).text =
                                    EventUtils.formatDecimal(TrackingUtils.speedToKmH(speed))
                            findViewById<TextView>(R.id.event_pace).text =
                                    EventUtils.formatDecimal(TrackingUtils.speedToPace(speed))
                        }

                        // Graph
                        val graph = findViewById<GraphView>(R.id.speed_graph)

                        // Speed
                        val series = LineGraphSeries<DataPoint>(locationsToDataPoints(it, ::calculateSpeed))
                        graph.addSeries(series)
                    }
                })
    }

    private fun calculateSpeed(i: Double, a: Location, b: Location): DataPoint {
        return DataPoint(i, TrackingUtils.calculateSpeed(a, b))
    }

    private fun locationsToDataPoints(locations: List<Location>,
                                      fx: (i: Double, a: Location, b: Location) -> DataPoint): Array<DataPoint> {
        val result = ArrayList<DataPoint>(locations.size - 1)
        val iterator = locations.iterator()

        var previous = iterator.next()
        var i = 0.0

        while (iterator.hasNext()) {
            val next = iterator.next()
            result.add(fx(i++, previous, next))
            previous = next
        }

        return result.toTypedArray()
    }
}
