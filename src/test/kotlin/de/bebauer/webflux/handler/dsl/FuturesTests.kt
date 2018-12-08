package de.bebauer.webflux.handler.dsl

import arrow.core.Try
import de.bebauer.webflux.handler.dsl.time.hours
import de.bebauer.webflux.handler.dsl.time.milliseconds
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FuturesTests {

    @Test
    fun `onComplete successful future without timeout`() {
        runHandlerTest(
            handler {
                onComplete(CompletableFuture.completedFuture("test")) { result ->
                    when (result) {
                        is Try.Success -> complete(result.value)
                        is Try.Failure -> failWith(result.exception)
                    }
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("test") }
            })
    }

    @Test
    fun `onComplete failed future without timeout`() {
        runHandlerTest(
            handler {
                onComplete(CompletableFuture.failedFuture<String>(RuntimeException("err"))) { result ->
                    when (result) {
                        is Try.Success -> complete(result.value)
                        is Try.Failure -> complete(result.exception.cause?.message)
                    }
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("err") }
            })
    }

    @Test
    fun `onComplete succeed with Duration timeout`() {
        runHandlerTest(
            handler {
                onComplete(CompletableFuture.completedFuture("test"), 2.hours) { result ->
                    when (result) {
                        is Try.Success -> complete(result.value)
                        is Try.Failure -> failWith(result.exception)
                    }
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("test") }
            })
    }

    @Test
    fun `onComplete fail with Duration timeout`() {
        runHandlerTest(
            handler {
                onComplete(CompletableFuture.runAsync { Thread.sleep(5000) }, 20.milliseconds) { result ->
                    when (result) {
                        is Try.Success -> complete(result.value)
                        is Try.Failure -> complete(result.exception.toString())
                    }
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo(TimeoutException().toString()) }
            })
    }

    @Test
    fun `onComplete with Timeout timeout`() {
        runHandlerTest(
            handler {
                onComplete(CompletableFuture.completedFuture("test"), Timeout(10, TimeUnit.SECONDS)) { result ->
                    when (result) {
                        is Try.Success -> complete(result.value)
                        is Try.Failure -> failWith(result.exception)
                    }
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("test") }
            })
    }


    @Test
    fun `onSuccess successful future without timeout`() {
        runHandlerTest(
            handler {
                onSuccess(CompletableFuture.completedFuture("test")) { result ->
                    complete(result)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("test") }
            })
    }

    @Test
    fun `onSuccess failed future without timeout`() {
        runHandlerTest(
            handler {
                onSuccess(CompletableFuture.failedFuture<String>(RuntimeException("err"))) { result ->
                    complete(result)
                }
            },
            { expectStatus().is5xxServerError })
    }

    @Test
    fun `onSuccess succeed with Duration timeout`() {
        runHandlerTest(
            handler {
                onSuccess(CompletableFuture.completedFuture("test"), 2.hours) { result ->
                    complete(result)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("test") }
            })
    }

    @Test
    fun `onSuccess fail with Duration timeout`() {
        runHandlerTest(
            handler {
                onSuccess(CompletableFuture.runAsync { Thread.sleep(5000) }, 20.milliseconds) { result ->
                    complete(result)
                }
            },
            { expectStatus().is5xxServerError })
    }

    @Test
    fun `onSuccess with Timeout timeout`() {
        runHandlerTest(
            handler {
                onSuccess(CompletableFuture.completedFuture("test"), Timeout(10, TimeUnit.SECONDS)) { result ->
                    complete(result)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("test") }
            })
    }
}