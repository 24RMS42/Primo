package com.primo.utils

class NetworkException(report: String, code: Int) : Exception(){

    var report: String = ""
    var code: Int = 0

    init {
        this.report = report
        this.code = code
    }

    override fun toString(): String{
        return "NetworkException(report='$report', code=$code)"
    }

}
