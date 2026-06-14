package com.arcadelabs.spiderlily.core.util

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> rx.Observable<T>.awaitFirst(): T = suspendCancellableCoroutine { cont ->
    val subscription = this.first().subscribe(
        { cont.resume(it) },
        { cont.resumeWithException(it) }
    )
    cont.invokeOnCancellation { subscription.unsubscribe() }
}
