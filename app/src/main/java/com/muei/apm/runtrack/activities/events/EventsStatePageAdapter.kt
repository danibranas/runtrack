package com.muei.apm.runtrack.activities.events

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.muei.apm.runtrack.fragments.EventsActivityFragment

class EventsStatePageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    companion object {
        const val NUM_PAGES = 3
    }

    var pageTitles: Array<String> = arrayOf()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> EventsActivityFragment()
                    .setMode(EventsActivityFragment.Companion.Mode.MODE_UPCOMING_EVENTS)
            2 -> EventsActivityFragment()
                    .setMode(EventsActivityFragment.Companion.Mode.MODE_MY_EVENTS)
            else -> EventsActivityFragment()
                    .setMode(EventsActivityFragment.Companion.Mode.MODE_NEAR_EVENTS)
        }
    }

    override fun getCount(): Int {
        return NUM_PAGES
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitles[position]
    }
}