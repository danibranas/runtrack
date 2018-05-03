package com.muei.apm.runtrack.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.fragments.*
import com.muei.apm.runtrack.tasks.DownloadImageTask
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.app_bar_menu.*


class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var account: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
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
        title = "Near events"
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_events -> {
                loadInContainer(item, EventsActivityFragment::class.java)
            }
            R.id.nav_add_event -> {
                loadInContainer(item, AddEventActivityFragment::class.java)
            }
            R.id.nav_settings -> {
                showToast("TODO: Open settings activity")
            }
            R.id.nav_rateus -> {
                val appPackageName = packageName
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")))
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }

                showToast("TODO: Open Google Play page!")
            }
            R.id.nav_checks -> {
                loadInContainer(item, SensorCheckFragment::class.java)
            }
            R.id.nav_location_checks -> {
                item.isChecked = true
                title = item.title
                val intent = Intent(this, TrackingActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_google_play_services_checks -> {
                loadInContainer(item, GooglePlayServicesCheckFragment::class.java)
            }
            R.id.nav_sign_out -> {
                AlertDialog.Builder(this)
                        .setMessage("Are you sure?")
                        .setPositiveButton("Sign out", { _: DialogInterface, _: Int ->
                            getGoogleSignInClient().signOut().addOnSuccessListener {
                                goToLoginActivity()
                            }
                        })
                        .setNegativeButton("No", null)
                        .show()
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

    private fun showToast(message: CharSequence, isLong: Boolean = false) =
            Toast.makeText(
                    this,
                    message,
                    if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()

    @Synchronized
    private fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

        return GoogleSignIn.getClient(this, gso)
    }
}
