package quotes.integration

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.netty.http.client.HttpClient
import java.net.URI

abstract class PartnerDataProvider<T> (private val uri: URI) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)
    private val client = HttpClient.create()
    private val om = ObjectMapper()

    fun stream(): Flux<T> =
        client.websocket()
            .uri(uri)
            .handle { inbound, _ ->
                inbound.receive().asString().flatMap {
                    try {
                        return@flatMap Flux.just(fromJson(om.readTree(it)))
                    } catch (e: Exception) {
                        logger.error("Couldn't parse response '$it': ${e.message}")
                        return@flatMap Flux.empty()
                    }
                }
            }
            .doOnSubscribe { logger.info("Starting streaming...") }

    protected abstract fun fromJson(json: JsonNode): T
}