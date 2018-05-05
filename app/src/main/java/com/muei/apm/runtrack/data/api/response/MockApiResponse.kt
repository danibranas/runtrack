package com.muei.apm.runtrack.data.api.response


class MockApiResponse<T>(private val response: T): ApiResponse<T> {
    override fun onResult(onSuccess: (T) -> Unit, onError: ((String) -> Unit)?) {
        onSuccess(response)
    }
}