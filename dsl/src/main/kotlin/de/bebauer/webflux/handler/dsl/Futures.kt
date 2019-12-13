package de.bebauer.webflux.handler.dsl

import arrow.core.*
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CompletableFuture
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
 * Executes the nested block with the optional value from a [CompletableFuture], waiting indefinitely.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 */
fun <T> HandlerDsl.onComplete(
    future: CompletableFuture<T>,
    init: HandlerDsl.(Either<Throwable, T>) -> CompleteOperation
) =
    onComplete(future, Option.empty<Timeout>(), init)

/**
 * Executes the nested block with the optional value from a [CompletableFuture], waiting the specified amount of time.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Duration]
 */
fun <T> HandlerDsl.onComplete(
    future: CompletableFuture<T>,
    timeout: Duration,
    init: HandlerDsl.(Either<Throwable, T>) -> CompleteOperation
) =
    onComplete(future, timeout.toOption(), init)

/**
 * Executes the nested block with the optional value from a [CompletableFuture], waiting the specified amount of time.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Timeout]
 */
fun <T> HandlerDsl.onComplete(
    future: CompletableFuture<T>,
    timeout: Timeout,
    init: HandlerDsl.(Either<Throwable, T>) -> CompleteOperation
) =
    onComplete(future, timeout.toOption(), init)

/**
 * Executes the nested block with the optional value from a [CompletableFuture], waiting the specified amount of time.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Duration]
 */
@JvmName("onCompleteDuration")
fun <T> HandlerDsl.onComplete(
    future: CompletableFuture<T>,
    timeout: Option<Duration>,
    init: HandlerDsl.(Either<Throwable, T>) -> CompleteOperation
) =
    onComplete(future, timeout.map { Timeout.of(it) }, init)

/**
 * Executes the nested block with the optional value from a [CompletableFuture], waiting the specified amount of time.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Timeout]
 */
@JvmName("onCompleteTimeout")
fun <T> HandlerDsl.onComplete(
    future: CompletableFuture<T>,
    timeout: Option<Timeout>,
    init: HandlerDsl.(Either<Throwable, T>) -> CompleteOperation
) =
    complete(Mono.fromFuture(timeout.map {
        future.orTimeout(
            it.value,
            it.unit
        )
    }.getOrElse { future }.thenApply<Either<Throwable, T>> { Right(it) })
        .onErrorResume { Mono.just(Left(it.cause ?: it)) }
        .flatMap { value -> execute { init(value) } })

/**
 * Executes the nested block with the value from a [CompletableFuture], if it was successful, waiting indefinitely.
 * Fails if the handler with [failWith] if the future failed.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 */
fun <T> HandlerDsl.onSuccess(future: CompletableFuture<T>, init: HandlerDsl.(T) -> CompleteOperation) =
    onSuccess(future, Option.empty<Timeout>(), init)

/**
 * Executes the nested block with the value from a [CompletableFuture], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [failWith] if the future failed.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Duration]
 */
fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>,
    timeout: Duration,
    init: HandlerDsl.(T) -> CompleteOperation
) =
    onSuccess(future, timeout.toOption(), init)

/**
 * Executes the nested block with the value from a [CompletableFuture], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [failWith] if the future failed.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Timeout]
 */
fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>,
    timeout: Timeout,
    init: HandlerDsl.(T) -> CompleteOperation
) =
    onSuccess(future, timeout.toOption(), init)

/**
 * Executes the nested block with the value from a [CompletableFuture], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [failWith] if the future failed.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Duration]
 */
@JvmName("onSuccessDuration")
fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>,
    timeout: Option<Duration>,
    init: HandlerDsl.(T) -> CompleteOperation
) =
    onSuccess(future, timeout.map { Timeout.of(it) }, init)

/**
 * Executes the nested block with the value from a [CompletableFuture], if it was successful,
 * waiting the specified amount of time.
 * Fails if the handler with [failWith] if the future failed.
 *
 * @param T the type of the [CompletableFuture]s return value
 * @param future the future
 * @param timeout the timeout for the future as a [Option] of [Duration]
 */
@JvmName("onSuccessTimeout")
fun <T> HandlerDsl.onSuccess(
    future: CompletableFuture<T>,
    timeout: Option<Timeout>,
    init: HandlerDsl.(T) -> CompleteOperation
) =
    onComplete(future, timeout) { result ->
        when (result) {
            is Either.Right -> init(result.b)
            is Either.Left -> failWith(result.a)
        }
    }
