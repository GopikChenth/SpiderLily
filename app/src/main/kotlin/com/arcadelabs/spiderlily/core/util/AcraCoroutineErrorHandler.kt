package com.arcadelabs.spiderlily.core.util

import com.arcadelabs.spiderlily.core.util.ext.printStackTraceDebug
import com.arcadelabs.spiderlily.core.util.ext.report
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class AcraCoroutineErrorHandler : AbstractCoroutineContextElement(CoroutineExceptionHandler),
	CoroutineExceptionHandler {

	override fun handleException(context: CoroutineContext, exception: Throwable) {
		exception.printStackTraceDebug("AcraCoroutineErrorHandler::handleException")
		exception.report()
	}
}
