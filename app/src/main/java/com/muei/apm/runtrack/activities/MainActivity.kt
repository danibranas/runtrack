package com.muei.apm.runtrack.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import android.view.WindowManager
import android.os.Build
import com.muei.apm.runtrack.R

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val RC_SIGN_IN = 25496
    }

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_main)
        hideStatusBar()
        configureGoogleSignIn()
    }

    // Can be safely ignored (https://developers.google.com/android/guides/releases#march_20_2018_-_version_1200)
    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        // TODO: uncomment for google check
        // handleOnAccount(GoogleSignIn.getLastSignedInAccount(this))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_in_button -> handleSignInClick(v)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            @SuppressLint("RestrictedApi")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleOnAccount(task.result)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun handleSignInClick(view: View) {
        // TODO: uncomment for Google SignIn
        // val signInIntent = mGoogleSignInClient!!.signInIntent
        // startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN)

        // TODO: skipping for dev purposes. Remove it
        handleOnAccount(null)
    }

    private fun handleOnAccount(account: GoogleSignInAccount?) {
        if (account == null) {
            // TODO: show error message and return void
            // return
        }

        // TODO: save account info
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

        // Can be safely ignored (https://developers.google.com/android/guides/releases#march_20_2018_-_version_1200)
        @SuppressLint("RestrictedApi")
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setOnClickListener(this)
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}
