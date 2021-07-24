package quotes.repos

import quotes.model.Isin
import quotes.model.Quote
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class QuotesRepository {

    private val storage = ConcurrentHashMap<Isin, List<Quote>>()

    fun save(quote: Quote): Quote {
        storage.compute(quote.isin) { _, quotes -> quotes?.let { it + quote } ?: listOf(quote) }
        return quote
    }

    fun removeFor(isin: Isin) = storage.remove(isin)

    fun latestPrices(isins: Collection<Isin>): Map<Isin, Quote?> = storage.filterKeys { isins.contains(it) }.mapValues { (_, quotes) -> quotes.maxByOrNull { it.timestamp } }.toMap()

    fun get(isin: Isin, since: Instant): List<Quote> = storage[isin]?.filter { !it.timestamp.isBefore(since) } ?: emptyList()

    fun all(since: Instant): Map<Isin, List<Quote>> = HashMap(storage).mapValues { (_, quotes) -> quotes.filter { !it.timestamp.isBefore(since) } }
}