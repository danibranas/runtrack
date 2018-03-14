package com.muei.apm.runtrack

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_main)
    }

    fun onSignInClick(view: View) {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}
