package com.reditus.core.system

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.selects.select

@OptIn(ExperimentalCoroutinesApi::class)
private fun CoroutineScope.fixedPeriodTicker(
    delayMillis: Long,
): ReceiveChannel<Unit> {
    return produce(capacity = 0) {
        delay(delayMillis)
        while (true) {
            channel.send(Unit)
            delay(delayMillis)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
    require(periodMillis > 0) { "Throttle period must be positive" }

    return channelFlow {
        val values = produce(capacity = Channel.CONFLATED) {
            collect { value -> send(value) }
        }

        var shouldEmit = true
        val ticker = fixedPeriodTicker(periodMillis)

        try {
            while (!values.isClosedForReceive) {
                select<Unit> {
                    values.onReceiveCatching { result ->
                        result.getOrNull()?.let { value ->
                            if (shouldEmit) {
                                send(value)
                                shouldEmit = false
                            }
                        } ?: run {
                            // Flow 종료
                            cancel()
                        }
                    }

                    ticker.onReceive {
                        shouldEmit = true
                    }
                }
            }
        } finally {
            ticker.cancel()
            values.cancel()
        }
    }
}