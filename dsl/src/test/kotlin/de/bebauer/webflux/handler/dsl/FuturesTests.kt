package de.bebauer.webflux.handler.dsl

import arrow.core.Either
import de.bebauer.webflux.handler.dsl.time.hours
import de.bebauer.webflux.handler.dsl.time.milliseconds
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FuturesTests : WordSpec({
    "onComplete" should {
        "continue with 'Either.Right' for a successful future without a timeout" {
            runHandlerTest(
                handler {
                    onComplete(CompletableFuture.completedFuture("test")) { result ->
                        when (result) {
                            is Either.Right -> ok(result.value)
                            is Either.Left -> failWith(result.value)
                        }
                    }
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "test"
                })
        }

        "continue with 'Either.Left' for a failed future without a timeout" {
            runHandlerTest(
                handler {
                    onComplete(CompletableFuture.failedFuture<String>(RuntimeException("err"))) { result ->
                        when (result) {
                            is Either.Right -> ok(result.value)
                            is Either.Left -> ok(result.value.message)
                        }
                    }
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "err"
                })
        }

        "continue with 'Either.Right' if the specified 'Duration' timeout was not hit and the future succeeds" {
            runHandlerTest(
                handler {
                    onComplete(CompletableFuture.completedFuture("test"), 2.hours) { result ->
                        when (result) {
                            is Either.Right -> ok(result.value)
                            is Either.Left -> failWith(result.value)
                        }
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "test"
                })
        }

        "continue with 'Either.Left' if the specified 'Duration' timeout was hit" {
            runHandlerTest(
                handler {
                    onComplete(CompletableFuture.runAsync { Thread.sleep(5000) }, 20.milliseconds) { result ->
                        when (result) {
                            is Either.Right -> ok(result.value)
                            is Either.Left -> ok(result.value.toString())
                        }
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe TimeoutException().toString()
                })
        }

        "continue with 'Either.Right' if the specified 'Timeout' timeout was not hit and the future succeeds" {
            runHandlerTest(
                handler {
                    onComplete(CompletableFuture.completedFuture("test"), Timeout(10, TimeUnit.SECONDS)) { result ->
                        when (result) {
                            is Either.Right -> ok(result.value)
                            is Either.Left -> failWith(result.value)
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
                        ok(result)
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
                        ok(result)
                    }
                },
                { expectStatus().is5xxServerError })
        }

        "continue with the future result if the future was successful and the specified 'Duration' timeout was not hit" {
            runHandlerTest(
                handler {
                    onSuccess(CompletableFuture.completedFuture("test"), 2.hours) { result ->
                        ok(result)
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
                        ok(result)
                    }
                },
                { expectStatus().is5xxServerError })
        }

        "continue with the future result if the future was successful and the specified 'Timeout' timeout was not hit" {
            runHandlerTest(
                handler {
                    onSuccess(CompletableFuture.completedFuture("test"), Timeout(10, TimeUnit.SECONDS)) { result ->
                        ok(result)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "test"
                })
        }
    }
})