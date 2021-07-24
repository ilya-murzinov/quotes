package quotes.service

import quotes.model.Candlestick
import quotes.model.Money
import quotes.model.Money.Companion.zero
import quotes.model.Quote
import java.time.Instant

class CandlesticksCalculator(private val interval: CandlestickInterval) {

    fun calculateCandlesticksFor(quotes: List<Quote>): List<Candlestick> {
        if (quotes.isEmpty())
            return emptyList()

        val sortedQuotes = quotes.sortedBy { it.timestamp }
        val from = requireNotNull(sortedQuotes.firstOrNull()).timestamp.truncatedTo(interval.scale)
        val to = requireNotNull(sortedQuotes.lastOrNull()).timestamp
        var previousCandlestick: Candlestick? = null
        var start = from

        val result = mutableListOf<Candlestick>()
        while (start.isBefore(to)) {
            val end = start.plus(interval.interval)
            val quotesForInterval = sortedQuotes.filter { !it.timestamp.isBefore(start) && it.timestamp.isBefore(end) }
            if (quotesForInterval.isEmpty() && previousCandlestick == null) {
                start = end
                continue
            }

            val low = quotesForInterval.map { it.price }.minByOrNull { it.value }
            val high = quotesForInterval.map { it.price }.maxByOrNull { it.value }
            val candlestick = Candlestick(
                start,
                end,
                openPrice(quotesForInterval, start, previousCandlestick),
                closePrice(quotesForInterval, previousCandlestick),
                low ?: requireNotNull(previousCandlestick).close,
                high ?: requireNotNull(previousCandlestick).close
            )

            previousCandlestick = candlestick
            result += candlestick
            start = end
        }

        return result
    }

    // Open price is (in order of priority):
    // 1. Price with the timestamp that is exactly equal to the start of the interval
    // 2. Previous candlestick close price
    // 3. The first price for the interval
    // 4. Zero
    private fun openPrice(
        quotes: List<Quote>,
        start: Instant,
        previousCandlestick: Candlestick?
    ): Money {
        val firstQuote = quotes.firstOrNull()
        return firstQuote?.takeIf { it.timestamp == start }?.price ?: previousCandlestick?.close ?: firstQuote?.price ?: zero()
    }

    private fun closePrice(
        quotesForInterval: List<Quote>,
        previousCandlestick: Candlestick?
    ) = quotesForInterval.lastOrNull()?.price ?: requireNotNull(previousCandlestick).close
}