package com.muei.apm.runtrack.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.muei.apm.runtrack.fragments.AddEventActivityFragment
import com.muei.apm.runtrack.fragments.EventsActivityFragment
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.fragments.TrackEventIdActivityFragment
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.app_bar_menu.*

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toolbar.title = "Near events"
        toggle.syncState()

        startContainer()
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_events -> {
                loadInContainer(item, EventsActivityFragment::class.java)
            }
            R.id.nav_scan -> {
                onScanEventClick(null)
            }
            R.id.nav_add_event -> {
                loadInContainer(item, AddEventActivityFragment::class.java)
            }
            R.id.nav_settings -> {
                showToast("TODO: Open settings activity")
            }
            R.id.nav_rateus -> {
                showToast("TODO: Open Google Play page!")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onTrackEventIdClick(view: View) {
        loadInContainer(null, TrackEventIdActivityFragment::class.java)
    }

    fun onTrackEvent(view: View) {
        showToast("TODO: track event by id")
    }

    fun onScanEventClick(view: View?) {
        showToast("TODO: scan event QR")
    }

    fun onEventClick(view: View) {
        showToast("TODO: view event details")
    }

    private fun startContainer() {
        val fragment = EventsActivityFragment::class.java.newInstance() as Fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
    }

    private fun loadInContainer(item: MenuItem?, fragmentClass: Class<*>) {
        val fragment = fragmentClass.newInstance() as Fragment

        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()

        if (item != null) {
            item.isChecked = true
            title = item.title
        }
    }

    private fun showToast(message: CharSequence, isLong: Boolean = false) =
            Toast.makeText(
                    this,
                    message,
                    if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
}
