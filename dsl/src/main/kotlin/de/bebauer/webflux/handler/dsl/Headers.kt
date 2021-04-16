package de.bebauer.webflux.handler.dsl

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.toOption
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException

/**
 * Represents a header name.
 *
 * @param T type of the header value
 * @param U the type of the conversion result
 * @param name the name of the header
 * @param converter the value converter function
 * @param valueExtractor value extractor function
 */
data class HeaderName<T, U>(
    val name: String,
    val converter: (List<String>) -> U,
    val valueExtractor: (List<String>) -> Either<Throwable, T>
)

/**
 * Creates a [HeaderName] from a [String].
 *
 * @param T the type of the header value
 * @param converter the value converter function
 */
fun <T> String.header(converter: (List<String>) -> T): HeaderName<T, T> = HeaderName(this, converter) {
    when {
        it.isEmpty() -> Either.Left(
            ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Missing required header $this."
            )
        )
        else -> Either.Right(converter(it))
    }
}

/**
 * Makes a [HeaderName] optional.
 *
 * @param T the type of the header value
 * @param U the type of the conversion result
 */
val <T, U> HeaderName<T, U>.optional: HeaderName<Option<T>, U>
    get() = HeaderName(this.name, this.converter) {
        when (val value = this.valueExtractor(it)) {
            is Either.Left -> Either.Right(None)
            is Either.Right -> value.map { v -> v.toOption() }
        }
    }

/**
 * Makes a [HeaderName] optional with a default value.
 *
 * @param T the type of the header value
 * @param U the type of the conversion result
 * @param defaultValue the default value if the header is missing
 */
fun <T, U> HeaderName<T, U>.optional(defaultValue: T): HeaderName<T, U> = HeaderName(this.name, this.converter) {
    when (val value = this.valueExtractor(it)) {
        is Either.Left -> Either.Right(defaultValue)
        is Either.Right -> value
    }
}

/**
 * Makes a [HeaderName] nullable.
 *
 * @param T the type of the header value
 * @param U the type of the conversion result
 */
val <T, U> HeaderName<T, U>.nullable: HeaderName<T?, U>
    get() = HeaderName(this.name, this.converter) {
        when (val value = this.valueExtractor(it)) {
            is Either.Left -> Either.Right(null)
            is Either.Right -> value
        }
    }

/**
 * Creates a [HeaderName] that returns the value as a comma separated string.
 */
val String.rawHeader
    get() = this.header { it.joinToString() }

/**
 * Creates a [HeaderName] that returns the value as list of strings.
 */
val String.stringHeader
    get() = this.header { it }

/**
 * Creates a required [HeaderName] that only extracts the first value.
 */
val <T, U> HeaderName<out List<T>, out List<U>>.single
    get() = this.name.header { this.converter(it).first() }

/**
 * Extracts a header value from the [org.springframework.web.reactive.function.server.ServerRequest].
 * Fails if the header does not exist.
 *
 * Example:
 * ```
 * handler {
 *  headerValue("test".stringHeader.single()) { test ->
 *      complete(test)
 *  }
 * }
 * ```
 *
 * @param header the header to extract as a [HeaderName]
 */
fun <T, U> HandlerDsl.headerValue(
    header: HeaderName<T, U>,
    init: HandlerDsl.(T) -> CompleteOperation
): CompleteOperation = extractRequest { request ->
    when (val values = header.valueExtractor(request.headers().header(header.name))) {
        is Either.Left -> failWith(values.value)
        is Either.Right -> init(values.value)
    }
}

/**
 * Container for standard HTTP headers.
 */
object Headers {
    val Accept = HttpHeaders.ACCEPT.header { it.map(MediaType::valueOf) }
    val ContentType = HttpHeaders.CONTENT_TYPE.header { it.map(MediaType::valueOf) }
    val Authorization = HttpHeaders.AUTHORIZATION.stringHeader
    val Location = HttpHeaders.LOCATION.stringHeader
    val ContentLocation = HttpHeaders.CONTENT_LOCATION.stringHeader
    val AcceptCharset = HttpHeaders.ACCEPT_CHARSET.stringHeader
    val AcceptEncoding = HttpHeaders.ACCEPT_ENCODING.stringHeader
    val AcceptLanguage = HttpHeaders.ACCEPT_LANGUAGE.stringHeader
    val AcceptRanges = HttpHeaders.ACCEPT_RANGES.stringHeader
    val AccessControlAllowCredentials = HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS.stringHeader
    val AccessControlAllowHeaders = HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS.stringHeader
    val AccessControlAllowMethods = HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS.stringHeader
    val AccessControlAllowOrigin = HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.stringHeader
    val AccessControlExposeHeaders = HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS.stringHeader
    val AccessControlMaxAge = HttpHeaders.ACCESS_CONTROL_MAX_AGE.stringHeader
    val AccessControlRequestHeaders = HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS.stringHeader
    val AccessControlRequestMethod = HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD.stringHeader
    val CacheControl = HttpHeaders.CACHE_CONTROL.stringHeader
    val Age = HttpHeaders.AGE.stringHeader
    val Allow = HttpHeaders.ALLOW.stringHeader
    val Connection = HttpHeaders.CONNECTION.stringHeader
    val ContentDisposition = HttpHeaders.CONTENT_DISPOSITION.stringHeader
    val ContentEncoding = HttpHeaders.CONTENT_ENCODING.stringHeader
    val ContentLanguage = HttpHeaders.CONTENT_LANGUAGE.stringHeader
    val ContentLength = HttpHeaders.CONTENT_LENGTH.stringHeader
    val ContentRange = HttpHeaders.CONTENT_RANGE.stringHeader
    val Cookie = HttpHeaders.COOKIE.stringHeader
    val Date = HttpHeaders.DATE.stringHeader
    val Etag = HttpHeaders.ETAG.stringHeader
    val Expect = HttpHeaders.EXPECT.stringHeader
    val Expires = HttpHeaders.EXPIRES.stringHeader
    val From = HttpHeaders.FROM.stringHeader
    val Host = HttpHeaders.HOST.stringHeader
    val IfMatch = HttpHeaders.IF_MATCH.stringHeader
    val IfModifiedSince = HttpHeaders.IF_MODIFIED_SINCE.stringHeader
    val IfNoneMatch = HttpHeaders.IF_NONE_MATCH.stringHeader
    val IfRange = HttpHeaders.IF_RANGE.stringHeader
    val IfUnmodifiedSince = HttpHeaders.IF_UNMODIFIED_SINCE.stringHeader
    val LastModified = HttpHeaders.LAST_MODIFIED.stringHeader
    val Link = HttpHeaders.LINK.stringHeader
    val MaxForwards = HttpHeaders.MAX_FORWARDS.stringHeader
    val Origin = HttpHeaders.ORIGIN.stringHeader
    val Pragma = HttpHeaders.PRAGMA.stringHeader
    val ProxyAuthenticate = HttpHeaders.PROXY_AUTHENTICATE.stringHeader
    val ProxyAuthorization = HttpHeaders.PROXY_AUTHORIZATION.stringHeader
    val Upgrade = HttpHeaders.UPGRADE.stringHeader
    val Range = HttpHeaders.RANGE.stringHeader
    val Referer = HttpHeaders.REFERER.stringHeader
    val RetryAfter = HttpHeaders.RETRY_AFTER.stringHeader
    val Server = HttpHeaders.SERVER.stringHeader
    val SetCookie = HttpHeaders.SET_COOKIE.stringHeader
    val SetCookie2 = HttpHeaders.SET_COOKIE2.stringHeader
    val TE = HttpHeaders.TE.stringHeader
    val Trailer = HttpHeaders.TRAILER.stringHeader
    val TransferEncoding = HttpHeaders.TRANSFER_ENCODING.stringHeader
    val UserAgent = HttpHeaders.USER_AGENT.stringHeader
    val Vary = HttpHeaders.VARY.stringHeader
    val Via = HttpHeaders.VIA.stringHeader
    val Warning = HttpHeaders.WARNING.stringHeader
    val WwwAuthenticate = HttpHeaders.WWW_AUTHENTICATE.stringHeader
}