package com.muei.apm.runtrack.tasks

import android.os.AsyncTask

class OperationTask<E> : AsyncTask<() -> E, Any, Any>() {
    override fun doInBackground(vararg params: (() -> E)?) {
        params.forEach {
            it?.invoke()
        }
    }
}