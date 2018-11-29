package webflux.handler.dsl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException

data class HeaderName<T>(val name: String, val converter: (List<String>) -> T)

fun <T> String.header(converter: (List<String>) -> T) = HeaderName(this, converter)

fun String.rawHeader() = this.header { it.joinToString() }

fun String.stringHeader() = this.header { it }

fun <T> HeaderName<out List<T>>.single() = HeaderName(this.name) { this.converter(it).first() }

fun <T> HandlerDsl.headerValue(header: HeaderName<T>,
                               init: HandlerDsl.(T) -> Unit) = nest { request ->
    val (name, converter) = header

    val values = request.headers().header(name)

    if (values.isEmpty()) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing header $name.")
    }

    init(converter(values))
}

fun <T> HandlerDsl.optionalHeaderValue(header: HeaderName<T>,
                                       init: HandlerDsl.(Option<T>) -> Unit) = nest { request ->
    val (name, converter) = header

    val values = request.headers().header(name)

    val maybeValue = when {
        values.isEmpty() -> None
        else             -> Some(values)
    }

    init(maybeValue.map(converter))
}

object Headers {
    val Accept = HeaderName(HttpHeaders.ACCEPT) { it.map(MediaType::valueOf) }
    val ContentType = HeaderName(HttpHeaders.CONTENT_TYPE) { it.map(MediaType::valueOf) }
    val Authorization = HttpHeaders.AUTHORIZATION.stringHeader()
    val Location = HttpHeaders.LOCATION.stringHeader()
    val ContentLocation = HttpHeaders.CONTENT_LOCATION.stringHeader()
    val AcceptCharset = HttpHeaders.ACCEPT_CHARSET.stringHeader()
    val AcceptEncoding = HttpHeaders.ACCEPT_ENCODING.stringHeader()
    val AcceptLanguage = HttpHeaders.ACCEPT_LANGUAGE.stringHeader()
    val AcceptRanges = HttpHeaders.ACCEPT_RANGES.stringHeader()
    val AccessControlAllowCredentials = HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS.stringHeader()
    val AccessControlAllowHeaders = HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS.stringHeader()
    val AccessControlAllowMethods = HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS.stringHeader()
    val AccessControlAllowOrigin = HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.stringHeader()
    val AccessControlExposeHeaders = HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS.stringHeader()
    val AccessControlMaxAge = HttpHeaders.ACCESS_CONTROL_MAX_AGE.stringHeader()
    val AccessControlRequestHeaders = HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS.stringHeader()
    val AccessControlRequestMethod = HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD.stringHeader()
    val CacheControl = HttpHeaders.CACHE_CONTROL.stringHeader()
    val Age = HttpHeaders.AGE.stringHeader()
    val Allow = HttpHeaders.ALLOW.stringHeader()
    val Connection = HttpHeaders.CONNECTION.stringHeader()
    val ContentDisposition = HttpHeaders.CONTENT_DISPOSITION.stringHeader()
    val ContentEncoding = HttpHeaders.CONTENT_ENCODING.stringHeader()
    val ContentLanguage = HttpHeaders.CONTENT_LANGUAGE.stringHeader()
    val ContentLength = HttpHeaders.CONTENT_LENGTH.stringHeader()
    val ContentRange = HttpHeaders.CONTENT_RANGE.stringHeader()
    val Cookie = HttpHeaders.COOKIE.stringHeader()
    val Date = HttpHeaders.DATE.stringHeader()
    val Etag = HttpHeaders.ETAG.stringHeader()
    val Expect = HttpHeaders.EXPECT.stringHeader()
    val Expires = HttpHeaders.EXPIRES.stringHeader()
    val From = HttpHeaders.FROM.stringHeader()
    val Host = HttpHeaders.HOST.stringHeader()
    val IfMatch = HttpHeaders.IF_MATCH.stringHeader()
    val IfModifiedSince = HttpHeaders.IF_MODIFIED_SINCE.stringHeader()
    val IfNoneMatch = HttpHeaders.IF_NONE_MATCH.stringHeader()
    val IfRange = HttpHeaders.IF_RANGE.stringHeader()
    val IfUnmodifiedSince = HttpHeaders.IF_UNMODIFIED_SINCE.stringHeader()
    val LastModified = HttpHeaders.LAST_MODIFIED.stringHeader()
    val Link = HttpHeaders.LINK.stringHeader()
    val MaxForwards = HttpHeaders.MAX_FORWARDS.stringHeader()
    val Origin = HttpHeaders.ORIGIN.stringHeader()
    val Pragma = HttpHeaders.PRAGMA.stringHeader()
    val ProxyAuthenticate = HttpHeaders.PROXY_AUTHENTICATE.stringHeader()
    val ProxyAuthorization = HttpHeaders.PROXY_AUTHORIZATION.stringHeader()
    val Upgrade = HttpHeaders.UPGRADE.stringHeader()
    val Range = HttpHeaders.RANGE.stringHeader()
    val Referer = HttpHeaders.REFERER.stringHeader()
    val RetryAfter = HttpHeaders.RETRY_AFTER.stringHeader()
    val Server = HttpHeaders.SERVER.stringHeader()
    val SetCookie = HttpHeaders.SET_COOKIE.stringHeader()
    val SetCookie2 = HttpHeaders.SET_COOKIE2.stringHeader()
    val TE = HttpHeaders.TE.stringHeader()
    val Trailer = HttpHeaders.TRAILER.stringHeader()
    val TransferEncoding = HttpHeaders.TRANSFER_ENCODING.stringHeader()
    val UserAgent = HttpHeaders.USER_AGENT.stringHeader()
    val Vary = HttpHeaders.VARY.stringHeader()
    val Via = HttpHeaders.VIA.stringHeader()
    val Warning = HttpHeaders.WARNING.stringHeader()
    val WwwAuthenticate = HttpHeaders.WWW_AUTHENTICATE.stringHeader()
}