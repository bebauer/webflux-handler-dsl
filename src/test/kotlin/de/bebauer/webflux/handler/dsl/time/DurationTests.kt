package de.bebauer.webflux.handler.dsl.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.stream.Stream

class DurationTests {

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun testArgs(): Stream<Arguments> = Stream.of(
            arguments(1.day, Duration.ofDays(1)),
            arguments(10.days, Duration.ofDays(10)),
            arguments(1.hour, Duration.ofHours(1)),
            arguments(10.hours, Duration.ofHours(10)),
            arguments(1.minute, Duration.ofMinutes(1)),
            arguments(10.minutes, Duration.ofMinutes(10)),
            arguments(1.second, Duration.ofSeconds(1)),
            arguments(10.seconds, Duration.ofSeconds(10)),
            arguments(1.millisecond, Duration.ofMillis(1)),
            arguments(10.milliseconds, Duration.ofMillis(10)),
            arguments(1.microsecond, Duration.of(1, ChronoUnit.MICROS)),
            arguments(10.microseconds, Duration.of(10, ChronoUnit.MICROS)),
            arguments(1.nanosecond, Duration.ofNanos(1)),
            arguments(10.nanoseconds, Duration.ofNanos(10))
        )
    }

    @ParameterizedTest
    @MethodSource("testArgs")
    fun `test duration creation`(actual: Duration, expected: Duration) {
        assertThat(actual).isEqualTo(expected)
    }
}