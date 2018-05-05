package com.muei.apm.runtrack.fragments.events

import android.view.View

interface RecyclerViewClickListener {
    fun onClick(view: View, position: Int)
}