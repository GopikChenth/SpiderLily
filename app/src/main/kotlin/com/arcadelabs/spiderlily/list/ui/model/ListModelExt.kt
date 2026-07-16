package com.arcadelabs.spiderlily.list.ui.model

import androidx.annotation.StringRes
import com.arcadelabs.spiderlily.R
import com.arcadelabs.spiderlily.core.exceptions.resolve.ExceptionResolver
import com.arcadelabs.spiderlily.core.util.ext.getDisplayIcon
import com.arcadelabs.spiderlily_parser.util.ifZero

fun Throwable.toErrorState(canRetry: Boolean = true, @StringRes secondaryAction: Int = 0) = ErrorState(
	exception = this,
	icon = getDisplayIcon(),
	canRetry = canRetry,
	buttonText = ExceptionResolver.getResolveStringId(this).ifZero { R.string.try_again },
	secondaryButtonText = secondaryAction,
)

fun Throwable.toErrorFooter() = ErrorFooter(
	exception = this,
)

operator fun ListModel.plus(list: List<ListModel>): List<ListModel> {
	val result = ArrayList<ListModel>(list.size + 1)
	result.add(this)
	result.addAll(list)
	return result
}

operator fun ListModel.plus(other: ListModel): List<ListModel> = listOf(this, other)
