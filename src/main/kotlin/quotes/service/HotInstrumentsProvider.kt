package quotes.service

import quotes.config.HotInstrumentsConfig
import quotes.model.HotInstrument
import quotes.repos.InstrumentsRepository
import quotes.repos.QuotesRepository
import java.math.MathContext.DECIMAL64
import java.math.RoundingMode.HALF_DOWN
import java.time.Instant

class HotInstrumentsProvider(
    private val config: HotInstrumentsConfig,
    private val instrumentsRepository: InstrumentsRepository,
    private val quotesRepository: QuotesRepository
) {

    fun hotInstruments(): Collection<HotInstrument> {
        val hotIsins = quotesRepository.all(Instant.now().minus(config.period))
            .filterValues { it.isNotEmpty() }
            .map { (isin, quotes) ->
                val prices = quotes.map { it.price.value }
                val diff = requireNotNull(prices.lastOrNull()) - requireNotNull(prices.firstOrNull())
                val openPrice = requireNotNull(quotes.minByOrNull { it.timestamp }).price.value
                isin to diff.divide(openPrice, DECIMAL64).multiply(100.toBigDecimal()).setScale(2, HALF_DOWN)
            }
            .filter { (_, change) -> change.abs() > config.threshold }
            .toMap()

        return instrumentsRepository.all()
            .filter { hotIsins.keys.contains(it.isin) }
            .map { HotInstrument(it, requireNotNull(hotIsins[it.isin])) }
            .sortedByDescending { it.priceChangePercent.abs() }
    }
}