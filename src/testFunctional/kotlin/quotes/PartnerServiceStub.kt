package quotes

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import quotes.model.InstrumentUpdate
import quotes.model.Quote
import reactor.core.publisher.Flux
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer

class PartnerServiceStub(private val port: Int) {

    private val om = ObjectMapper()

    fun startPartnerService(instruments: Collection<InstrumentUpdate>, quotes: Collection<Quote>): DisposableServer {
        return HttpServer.create()
            .route { routes ->
                routes.ws("/instruments") { _, outbound ->
                    outbound.sendString(Flux.fromIterable(instruments).map { toJson(it).toString() })
                }.ws("/quotes") { _, outbound ->
                    outbound.sendString(Flux.fromIterable(quotes).map { toJson(it).toString() })
                }
            }
            .port(port)
            .bindNow()
    }

    private fun toJson(update: InstrumentUpdate) = om.createObjectNode()
        .put("type", update.changeType.toString())
        .set<JsonNode>("data", om.createObjectNode()
            .put("isin", update.instrument.isin.value)
            .put("description", update.instrument.description))

    private fun toJson(quote: Quote) = om.createObjectNode()
        .put("type", "QUOTE")
        .set<JsonNode>("data", om.createObjectNode()
            .put("isin", quote.isin.value)
            .put("price", quote.price.value))
}