package com.primo.network.api_new

interface ApiResult<T> {

    fun onStart() {}

    fun onResult(result: T)

    fun onError(message: String, code: Int)

    fun onComplete() {}
}