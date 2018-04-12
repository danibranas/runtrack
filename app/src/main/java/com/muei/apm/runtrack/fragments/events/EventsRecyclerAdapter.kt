package com.muei.apm.runtrack.fragments.events

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.models.Event

class EventsRecyclerAdapter(events: List<Event>): RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder>() {

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
        holder.itemView.findViewById<TextView>(R.id.event_distance).text = event.distance.toString()
        // TODO: customize fields...
        holder.itemView.findViewById<TextView>(R.id.event_date_day).text = 16.toString()
        holder.itemView.findViewById<TextView>(R.id.event_date_month).text = "feb"
    }
}