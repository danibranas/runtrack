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
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.fragments.*
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
            R.id.nav_checks -> {
                loadInContainer(item, SensorCheckFragment::class.java)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onAddEventClick(view: View) {
        loadInContainer(null, AddEventActivityFragment::class.java)
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
        loadInContainer(R.id.nav_events, EventDetailsFragment::class.java)
    }

    private fun loadInContainer(item: MenuItem?, fragmentClass: Class<*>, addToBackStack: Boolean = true) {
        val fragment = fragmentClass.newInstance() as Fragment

        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        var transaction = fragmentManager.beginTransaction().replace(R.id.main_container, fragment)

        if (addToBackStack) {
            transaction = transaction.addToBackStack(null)
        }

        transaction.commit()

        if (item != null) {
            item.isChecked = true
            title = item.title
        }
    }

    private fun loadInContainer(itemId: Int, fragmentClass: Class<*>, addToBackStack: Boolean = true) {
        nav_view.setCheckedItem(itemId)
        return this.loadInContainer(null, fragmentClass, addToBackStack)
    }

    private fun startContainer() {
        val fragment = EventsActivityFragment::class.java.newInstance() as Fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
    }

    private fun showToast(message: CharSequence, isLong: Boolean = false) =
            Toast.makeText(
                    this,
                    message,
                    if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
}
