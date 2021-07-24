package quotes.util

import com.fasterxml.jackson.databind.JsonNode
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import reactor.core.publisher.Mono
import reactor.netty.NettyOutbound
import reactor.netty.http.server.HttpServerResponse

fun HttpServerResponse.sendJson(json: JsonNode): NettyOutbound =
    header(CONTENT_TYPE, APPLICATION_JSON).sendString(Mono.just(json.toString()))