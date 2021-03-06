package com.muei.apm.runtrack.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.activities.events.EventsStatePageAdapter
import com.muei.apm.runtrack.tasks.DownloadImageTask
import kotlinx.android.synthetic.main.activity_events.drawer_layout
import kotlinx.android.synthetic.main.activity_events.nav_view
import kotlinx.android.synthetic.main.activity_events_bar_menu.toolbar

class EventsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var account: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        setSupportActionBar(toolbar)

        account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            goToLoginActivity()
            return
        }

        initializeProfileData()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        title = resources.getString(R.string.app_name)
        toggle.syncState()

        startContainer()
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_events -> {}
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_rate_us -> {
                val appPackageName = packageName
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")))
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
            R.id.nav_sign_out -> {
                AlertDialog.Builder(this)
                        .setMessage(R.string.sign_out_confirm)
                        .setPositiveButton(R.string.menu_sign_out, { _: DialogInterface, _: Int ->
                            getGoogleSignInClient().signOut().addOnSuccessListener {
                                goToLoginActivity()
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onAddEventClick(view: View?) {
        val options = arrayOf(
                view!!.resources.getString(R.string.add_event_by_id),
                view.resources.getString(R.string.add_event_by_qr)
        )

        AlertDialog.Builder(this)
                .setTitle(R.string.add_event_title)
                .setItems(options, {
                    _, which ->
                        when (which) {
                            0 -> onTrackEvent()
                            1 -> onScanEventClick()
                        }
                })
                .show()
    }

    private fun onTrackEvent() {
        val eventIdInput = EditText(this)
        eventIdInput.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
                .setTitle(R.string.add_event_by_id)
                .setView(eventIdInput)
                .setPositiveButton(R.string.search_event, {
                    _, _ -> goToEventDetailsActivity((eventIdInput.text.toString().toLong()))
                })
                .setNegativeButton(R.string.cancel, {
                    d, _ -> d.dismiss()
                })
                .show()
    }

    private fun onScanEventClick() {
        val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
        startActivity(intent)
    }

    private fun goToEventDetailsActivity(eventId: Long?) {
        val intent = Intent(this, EventDetailsActivity::class.java)
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, eventId)
        startActivity(intent)
    }

    private fun startContainer() {
        val tabLayout: TabLayout = findViewById(R.id.events_tabs)
        val viewPager: ViewPager = findViewById(R.id.events_view_pager)

        val adapter = EventsStatePageAdapter(supportFragmentManager)
        adapter.pageTitles = arrayOf(
                resources.getString(R.string.events_tab_near_events),
                resources.getString(R.string.events_tab_my_events),
                resources.getString(R.string.events_tab_records)
        )

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun initializeProfileData() {
        val header = nav_view.getHeaderView(0)
        val name = "${account?.givenName} ${account?.familyName}"

        header.findViewById<TextView>(R.id.account_email).text = account?.email
        header.findViewById<TextView>(R.id.account_name).text = name


        val bitmap = DownloadImageTask().execute(account?.photoUrl.toString()).get()
        val image = header.findViewById<ImageView>(R.id.account_image)
        image.setImageBitmap(bitmap)

        val dr = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
        dr.cornerRadius = (image.height/2).toFloat()
    }

    @Synchronized
    private fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

        return GoogleSignIn.getClient(this, gso)
    }
}
