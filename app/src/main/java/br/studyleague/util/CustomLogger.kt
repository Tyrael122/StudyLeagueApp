package br.studyleague.util

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

object CustomLogger {
    fun d(tag: String, message: String) {
        Log.d(tag, message)

        Firebase.crashlytics.log(message)
    }

    fun e(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)

        Firebase.crashlytics.recordException(throwable)
    }
}