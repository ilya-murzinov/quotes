package quotes.config

import quotes.service.CandlestickInterval
import quotes.service.CandlestickInterval.MINUTE
import java.math.BigDecimal
import java.net.URI
import java.time.Duration

open class Config {
    open val serverPort = 8081
    val chartConfig = ChartConfig.default()
    val hotInstruments = HotInstrumentsConfig.default()

    open val partnerURI: URI by lazy { URI.create("ws://127.0.0.1:8080") }
    val instrumentsURI: URI by lazy {  partnerURI.resolve("instruments") }
    val quotesURI: URI  by lazy { partnerURI.resolve("quotes") }
}

data class ChartConfig(
    val interval: CandlestickInterval,
    val period: Duration,
) {
    companion object {
        fun default() = ChartConfig(MINUTE, Duration.ofMinutes(30))
    }
}

data class HotInstrumentsConfig(
    val threshold: BigDecimal,
    val period: Duration
) {
    companion object {
        fun default() = HotInstrumentsConfig(10.toBigDecimal(), Duration.ofMinutes(30))
    }
}