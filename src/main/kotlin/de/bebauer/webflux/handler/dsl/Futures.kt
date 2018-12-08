package de.bebauer.webflux.handler.dsl

import arrow.core.*
import java.time.Duration
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Data class representing a timeout.
 */
data class Timeout(val value: Long, val unit: TimeUnit) {
    companion object {
        fun of(duration: Duration) = Timeout(TimeUnit.NANOSECONDS.convert(duration), TimeUnit.NANOSECONDS)
    }
}

/**
 * Executes the nested block with the optional value from a [Future], waiting indefinitely.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 */
fun <T> HandlerDsl.onComplete(future: Future<T>, init: HandlerDsl.(Try<T>) -> Unit) =
    onComplete(future, Option.empty<Timeout>(), init)

/**
 * Executes the nested block with the optional value from a [Future], waiting the specified amount of time.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Duration]
 */
fun <T> HandlerDsl.onComplete(future: Future<T>, timeout: Duration, init: HandlerDsl.(Try<T>) -> Unit) =
    onComplete(future, timeout.toOption(), init)

/**
 * Executes the nested block with the optional value from a [Future], waiting the specified amount of time.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Timeout]
 */
fun <T> HandlerDsl.onComplete(future: Future<T>, timeout: Timeout, init: HandlerDsl.(Try<T>) -> Unit) =
    onComplete(future, timeout.toOption(), init)

/**
 * Executes the nested block with the optional value from a [Future], waiting the specified amount of time.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Duration]
 */
@JvmName("onCompleteDuration")
fun <T> HandlerDsl.onComplete(future: Future<T>, timeout: Option<Duration>, init: HandlerDsl.(Try<T>) -> Unit) =
    onComplete(future, timeout.map { Timeout.of(it) }, init)

/**
 * Executes the nested block with the optional value from a [Future], waiting the specified amount of time.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Timeout]
 */
@JvmName("onCompleteTimeout")
fun <T> HandlerDsl.onComplete(future: Future<T>, timeout: Option<Timeout>, init: HandlerDsl.(Try<T>) -> Unit) {
    val value = Try { timeout.map { future.get(it.value, it.unit) }.getOrElse { future.get() } }

    init(value)
}

/**
 * Executes the nested block with the value from a [Future], if it was successful, waiting indefinitely.
 * Fails if the handler with [HandlerDsl.failWith] if the future failed.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 */
fun <T> HandlerDsl.onSuccess(future: Future<T>, init: HandlerDsl.(T) -> Unit) =
    onSuccess(future, Option.empty<Timeout>(), init)

/**
 * Executes the nested block with the value from a [Future], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [HandlerDsl.failWith] if the future failed.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Duration]
 */
fun <T> HandlerDsl.onSuccess(future: Future<T>, timeout: Duration, init: HandlerDsl.(T) -> Unit) =
    onSuccess(future, timeout.toOption(), init)

/**
 * Executes the nested block with the value from a [Future], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [HandlerDsl.failWith] if the future failed.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Timeout]
 */
fun <T> HandlerDsl.onSuccess(future: Future<T>, timeout: Timeout, init: HandlerDsl.(T) -> Unit) =
    onSuccess(future, timeout.toOption(), init)

/**
 * Executes the nested block with the value from a [Future], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [HandlerDsl.failWith] if the future failed.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Duration]
 */
@JvmName("onSuccessDuration")
fun <T> HandlerDsl.onSuccess(future: Future<T>, timeout: Option<Duration>, init: HandlerDsl.(T) -> Unit) =
    onSuccess(future, timeout.map { Timeout.of(it) }, init)

/**
 * Executes the nested block with the value from a [Future], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [HandlerDsl.failWith] if the future failed.
 *
 * @param T the type of the [Future]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Duration]
 */
@JvmName("onSuccessTimeout")
fun <T> HandlerDsl.onSuccess(future: Future<T>, timeout: Option<Timeout>, init: HandlerDsl.(T) -> Unit) =
    onComplete(future, timeout) { result ->
        when (result) {
            is Try.Success -> init(result.value)
            is Try.Failure -> failWith(result.exception)
        }
    }
