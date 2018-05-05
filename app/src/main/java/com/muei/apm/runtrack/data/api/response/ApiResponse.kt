package com.muei.apm.runtrack.data.api.response

interface ApiResponse<T> {
    fun onResult(onSuccess: (T) -> Unit, onError: ((String) -> Unit)? = null)
}