package com.github.sbaldin.invoicer.time

inline fun <T> measureTimeMillis(
    loggingStart: () -> Unit,
    loggingFinish: (Long) -> Unit,
    function: () -> T
): T {
    loggingStart()
    val startTime = System.currentTimeMillis()
    val result: T = function.invoke()
    loggingFinish(System.currentTimeMillis() - startTime)
    return result
}
