package com.muei.apm.runtrack.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.muei.apm.runtrack.R
import com.muei.apm.runtrack.utils.GooglePlayServices
import com.muei.apm.runtrack.utils.MediaUtils

class GooglePlayServicesCheckFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_google_play_services_check, container, false)

        var text = "- Google Play Services: ${GooglePlayServices(context!!).isAvailable()}"
        text = "$text\n- Media Apps: ${MediaUtils(context!!).checkMediaCaptureApps()}"

        view.findViewById<TextView>(R.id.location_check_text)?.text = text
        return view
    }
}