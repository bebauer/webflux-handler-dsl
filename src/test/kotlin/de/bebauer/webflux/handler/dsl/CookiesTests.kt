package de.bebauer.webflux.handler.dsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class CookiesTests : WordSpec({
    "cookie" should {
        "extract set required cookie values" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie) { (test) ->
                        ok(test)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "xxx"
                },
                request = { get().uri("/test").cookie("test", "xxx") })
        }

        "fail with bad request if required cookie is missing" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie) { (test) ->
                        ok(test)
                    }
                },
                { expectStatus().isBadRequest })
        }

        "extract set optional cookie as Some" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.optional) { test ->
                        ok(test.toString())
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "Some([xxx])"
                },
                request = { get().uri("/test").cookie("test", "xxx") })
        }

        "extract missing optional cookie as None" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.optional) { test ->
                        ok(test.toString())
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "None"
                })
        }

        "extract cookie values if it is set and was defined as optional with default value" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.optional(listOf("abc"))) { (test) ->
                        ok(test)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "xxx"
                },
                request = { get().uri("/test").cookie("test", "xxx") })
        }

        "fallback to default values if optional cookie is missing" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.optional(listOf("abc"))) { (test) ->
                        ok(test)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "abc"
                })
        }

        "extract single value from set cookie" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.single) { test ->
                        ok(test)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "xxx"
                },
                request = { get().uri("/test").cookie("test", "xxx") })
        }

        "fail with bad request if cookie is missing when trying to extract single value" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.single) { test ->
                        ok(test)
                    }
                },
                {
                    expectStatus().isBadRequest
                })
        }

        "extract single optional value as Some if cookie is set" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.single.optional) { test ->
                        ok(test.toString())
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "Some(xxx)"
                },
                request = { get().uri("/test").cookie("test", "xxx") })
        }

        "extract single optional value as None if cookie is missing" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.single.optional) { test ->
                        ok(test.toString())
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "None"
                })
        }

        "extract single cookie value if it was defined as optional with default" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.single.optional("abc")) { test ->
                        ok(test)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "xxx"
                },
                request = { get().uri("/test").cookie("test", "xxx") })
        }

        "fallback to default value if cookie defined as single optional with default value is missing" {
            runHandlerTest(
                handler {
                    cookie("test".stringCookie.single.optional("abc")) { test ->
                        ok(test)
                    }
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult().responseBody shouldBe "abc"
                })
        }
    }
})