package com.muei.apm.runtrack.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.data.persistence.AppDatabase

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = resources.getString(R.string.settings)

        findViewById<TextView>(R.id.settings_about).setOnClickListener(this)
        findViewById<TextView>(R.id.settings_diagnostics).setOnClickListener(this)
        findViewById<TextView>(R.id.settings_clear_database).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settings_about -> {
                AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.app_about)
                        .setPositiveButton(R.string.ok, null)
                        .show()
            }
            R.id.settings_diagnostics -> {
                startActivity(Intent(this, DiagnosticsActivity::class.java))
            }
            R.id.settings_clear_database -> {
                AlertDialog.Builder(this)
                        .setMessage(R.string.clear_database_message)
                        .setPositiveButton(R.string.clear_database, { _: DialogInterface, _: Int ->
                            AppDatabase.clearDatabase()
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show()
            }
        }
    }
}
