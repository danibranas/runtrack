package com.muei.apm.runtrack.fragments.events

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.activities.EventDetailsActivity
import com.muei.apm.runtrack.data.models.Event
import com.muei.apm.runtrack.utils.EventUtils


class EventsRecyclerAdapter(events: List<Event>, val context: Context?) : RecyclerView.Adapter<ItemViewHolder>() {
    private var eventDataSet: List<Event> = events

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val v = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.fragment_event_element, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return ItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        return eventDataSet.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = eventDataSet[position]
        holder.itemView.findViewById<TextView>(R.id.event_title).text = event.name
        holder.itemView.findViewById<TextView>(R.id.event_distance).text = EventUtils.formatDistance(event)
        // TODO: customize fields...
        holder.itemView.findViewById<TextView>(R.id.event_date_day).text = EventUtils.getDay(event)
        holder.itemView.findViewById<TextView>(R.id.event_date_month).text = EventUtils.getMonthName(event)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.id)
            context?.startActivity(intent)
        }
    }
}