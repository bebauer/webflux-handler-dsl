package de.bebauer.webflux.handler.dsl

import arrow.core.Try
import de.bebauer.webflux.handler.dsl.time.hours
import de.bebauer.webflux.handler.dsl.time.milliseconds
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FuturesTests : WordSpec({
    "onComplete" should {
        "continue with 'Try.Success' for a successful future without a timeout" {
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
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "test"
                })
        }

        "continue with 'Try.Failure' for a failed future without a timeout" {
            runHandlerTest(
                handler {
                    onComplete(CompletableFuture.failedFuture<String>(RuntimeException("err"))) { result ->
                        when (result) {
                            is Try.Success -> complete(result.value)
                            is Try.Failure -> complete(result.exception.message)
                        }
                    }
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "err"
                })
        }

        "continue with 'Try.Success' if the specified 'Duration' timeout was not hit and the future succeeds" {
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
                        .responseBody shouldBe "test"
                })
        }

        "continue with 'Try.Failure' if the specified 'Duration' timeout was hit" {
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
                        .responseBody shouldBe TimeoutException().toString()
                })
        }

        "continue with 'Try.Success' if the specified 'Timeout' timeout was not hit and the future succeeds" {
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
                        .responseBody shouldBe "test"
                })
        }
    }

    "onSuccess" should {
        "continue with the future result if the future was successful" {
            runHandlerTest(
                handler {
                    onSuccess(CompletableFuture.completedFuture("test")) { result ->
                        complete(result)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "test"
                })
        }

        "fail with an internal server error if the future failed" {
            runHandlerTest(
                handler {
                    onSuccess(CompletableFuture.failedFuture<String>(RuntimeException("err"))) { result ->
                        complete(result)
                    }
                },
                { expectStatus().is5xxServerError })
        }

        "continue with the future result if the future was successful and the specified 'Duration' timeout was not hit" {
            runHandlerTest(
                handler {
                    onSuccess(CompletableFuture.completedFuture("test"), 2.hours) { result ->
                        complete(result)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "test"
                })
        }

        "fail with an internal server error if the specified 'Duration' timeout was hit" {
            runHandlerTest(
                handler {
                    onSuccess(CompletableFuture.runAsync { Thread.sleep(5000) }, 20.milliseconds) { result ->
                        complete(result)
                    }
                },
                { expectStatus().is5xxServerError })
        }

        "continue with the future result if the future was successful and the specified 'Timeout' timeout was not hit" {
            runHandlerTest(
                handler {
                    onSuccess(CompletableFuture.completedFuture("test"), Timeout(10, TimeUnit.SECONDS)) { result ->
                        complete(result)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "test"
                })
        }
    }
})