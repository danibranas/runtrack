package com.muei.apm.runtrack.fragments.events

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.activities.EventDetailsActivity
import com.muei.apm.runtrack.models.Event
import java.text.SimpleDateFormat
import java.util.*

class EventsRecyclerAdapter(events: List<Event>, var context: Context?): RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder>() {

    private var eventDataSet: List<Event> = events

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.fragment_event_element, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return eventDataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventDataSet[position]
        holder.itemView.findViewById<TextView>(R.id.event_title).text = event.name
        holder.itemView.findViewById<TextView>(R.id.event_distance).text = formatDistance(event)
        // TODO: customize fields...
        holder.itemView.findViewById<TextView>(R.id.event_date_day).text = getDay(event)
        holder.itemView.findViewById<TextView>(R.id.event_date_month).text = getMonthName(event)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, -1)
            context?.startActivity(intent)
        }
    }

    private fun getDay(event: Event): String {
        val calendar = Calendar.getInstance()
        calendar.time = event.date
        return calendar.get(Calendar.DATE).toString()
    }

    private fun getMonthName(event: Event): String {
        val fmt = SimpleDateFormat("MMM", Locale.ENGLISH)
        return fmt.format(event.date)
    }

    private fun formatDistance(event: Event): String {
        return "${event.distance} ${event.unit}"
    }
}