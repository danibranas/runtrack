package com.muei.apm.runtrack.data.api

import android.content.Context

class ApiFactory {
    companion object {
        fun getApi(context: Context): Api {
            return MockApi(context)
        }
    }
}