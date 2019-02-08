package de.bebauer.webflux.handler.dsl.time

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import java.time.Duration
import java.time.temporal.ChronoUnit

class DurationTests : StringSpec({
    "test duration creation" {
        forall(
            row(1.day, Duration.ofDays(1)),
            row(10.days, Duration.ofDays(10)),
            row(1.hour, Duration.ofHours(1)),
            row(10.hours, Duration.ofHours(10)),
            row(1.minute, Duration.ofMinutes(1)),
            row(10.minutes, Duration.ofMinutes(10)),
            row(1.second, Duration.ofSeconds(1)),
            row(10.seconds, Duration.ofSeconds(10)),
            row(1.millisecond, Duration.ofMillis(1)),
            row(10.milliseconds, Duration.ofMillis(10)),
            row(1.microsecond, Duration.of(1, ChronoUnit.MICROS)),
            row(10.microseconds, Duration.of(10, ChronoUnit.MICROS)),
            row(1.nanosecond, Duration.ofNanos(1)),
            row(10.nanoseconds, Duration.ofNanos(10))
        ) { actual, expected ->
            actual shouldBe expected
        }
    }
})