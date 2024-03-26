package br.studyleague.util

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

fun Any.debug(message: String) {
    val tag = this::class.java.simpleName
    debug(tag, message)
}

fun Any.error(message: String, throwable: Throwable) {
    val tag = this::class.java.simpleName
    error(tag, message, throwable)
}

fun error(tag: String, message: String, throwable: Throwable) {
    Log.e(tag, message, throwable)

    Firebase.crashlytics.recordException(throwable)
}

fun debug(tag: String, message: String) {
    Log.d(tag, message)

    Firebase.crashlytics.log(message)
}