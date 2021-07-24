package quotes.endpoint

import io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import quotes.config.ChartConfig
import quotes.endpoint.JsonConverters.toJson
import quotes.model.Isin
import quotes.repos.InstrumentsRepository
import quotes.repos.QuotesRepository
import quotes.service.CandlesticksCalculator
import quotes.service.HotInstrumentsProvider
import quotes.util.sendJson
import reactor.netty.http.server.HttpServerRoutes
import java.time.Instant

class InstrumentsEndpoint(
    private val config: ChartConfig,
    private val instrumentsRepository: InstrumentsRepository,
    private val quotesRepository: QuotesRepository,
    private val candlesticksCalculator: CandlesticksCalculator,
    private val hotInstrumentsProvider: HotInstrumentsProvider
) {

    fun routes(routes: HttpServerRoutes) {
        routes.get("/instruments") { _, response ->
            val instruments = instrumentsRepository.all()
            val quotes = quotesRepository.latestPrices(instruments.map { it.isin })
            response.sendJson(toJson(instruments, quotes))
        }

        routes.get("/price-history/{isin}") { request, response ->
            val isin = request.param("isin")?.let { Isin(it) } ?: return@get response.status(BAD_REQUEST)
            val instrument = instrumentsRepository.find(isin) ?: return@get response.status(NOT_FOUND)
            val quotes = quotesRepository.get(isin, Instant.now().minus(config.period))
            val candlesticks = candlesticksCalculator.calculateCandlesticksFor(quotes)
            response.sendJson(toJson(instrument, candlesticks))
        }

        routes.get("/hot-instruments") { _, response ->
            response.sendJson(toJson(hotInstrumentsProvider.hotInstruments()))
        }
    }
}