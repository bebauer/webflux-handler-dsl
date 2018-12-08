package de.bebauer.webflux.handler.dsl.time

import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Returns a [Duration] of this [Number] in days.
 */
val Number.days: Duration
    get() = Duration.ofDays(this.toLong())

/**
 * Returns a [Duration] of this [Number] in days.
 */
val Number.day: Duration
    get() = this.days

/**
 * Returns a [Duration] of this [Number] in hours.
 */
val Number.hours: Duration
    get() = Duration.ofHours(this.toLong())

/**
 * Returns a [Duration] of this [Number] in hours.
 */
val Number.hour: Duration
    get() = this.hours

/**
 * Returns a [Duration] of this [Number] in minutes.
 */
val Number.minutes: Duration
    get() = Duration.ofMinutes(this.toLong())

/**
 * Returns a [Duration] of this [Number] in minutes.
 */
val Number.minute: Duration
    get() = this.minutes

/**
 * Returns a [Duration] of this [Number] in seconds.
 */
val Number.seconds: Duration
    get() = Duration.ofSeconds(this.toLong())

/**
 * Returns a [Duration] of this [Number] in seconds.
 */
val Number.second: Duration
    get() = this.seconds

/**
 * Returns a [Duration] of this [Number] in milliseconds.
 */
val Number.milliseconds: Duration
    get() = Duration.ofMillis(this.toLong())

/**
 * Returns a [Duration] of this [Number] in milliseconds.
 */
val Number.millisecond: Duration
    get() = this.milliseconds

/**
 * Returns a [Duration] of this [Number] in microseconds.
 */
val Number.microseconds: Duration
    get() = Duration.of(this.toLong(), ChronoUnit.MICROS)

/**
 * Returns a [Duration] of this [Number] in microseconds.
 */
val Number.microsecond: Duration
    get() = this.microseconds

/**
 * Returns a [Duration] of this [Number] in nanoseconds.
 */
val Number.nanoseconds: Duration
    get() = Duration.ofNanos(this.toLong())

/**
 * Returns a [Duration] of this [Number] in nanoseconds.
 */
val Number.nanosecond: Duration
    get() = this.nanoseconds
