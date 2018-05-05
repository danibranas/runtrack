package com.muei.apm.runtrack.data.api.response

class BackendApiResponse<T>(
        private val onRequest: (onSuccess: (T) -> Unit, onError: ((String) -> Unit)?) -> Any
): ApiResponse<T> {
    override fun onResult(onSuccess: (T) -> Unit, onError: ((String) -> Unit)?) {
        onRequest(onSuccess, onError)
    }
}