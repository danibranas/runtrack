package com.muei.apm.runtrack.activities

import android.content.DialogInterface
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
import android.support.v7.app.AlertDialog
import android.util.Log
import com.muei.apm.runtrack.R
import java.lang.RuntimeException

class LoginActivity : AppCompatActivity(), View.OnClickListener {
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

    override fun onStart() {
        super.onStart()
        handleOnAccount(GoogleSignIn.getLastSignedInAccount(this))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_in_button -> handleSignInClick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInAccount?

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                result = task.result
            } catch (e: RuntimeException) {
                Log.println(Log.INFO, "Api", e.message)
                return
            }

            handleOnAccount(result)
        }
    }

    private fun handleSignInClick() {
        AlertDialog.Builder(this)
                .setTitle(R.string.eula_title)
                .setMessage(R.string.eula_content)
                .setPositiveButton(R.string.eula_agree, { _: DialogInterface, _: Int ->
                    val signInIntent = mGoogleSignInClient!!.signInIntent
                    startActivityForResult(signInIntent, LoginActivity.RC_SIGN_IN)
                })
                .setNegativeButton(R.string.eula_disagree, null)
                .show()
    }

    private fun handleOnAccount(account: GoogleSignInAccount?) {
        if (account == null) {
            // TODO: show error message
            return
        }

        // TODO: retrieve account info from API
        // account.getResult()

        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    @Synchronized
    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

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
