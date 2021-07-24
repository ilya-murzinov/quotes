package quotes.consumer

import quotes.integration.PartnerQuotesProvider
import quotes.repos.QuotesRepository
import reactor.core.Disposable
import java.time.Instant

class QuotesConsumer(
    private val quotesRepository: QuotesRepository,
    private val partnerQuotesProvider: PartnerQuotesProvider
) : Consumer {

    override fun start(): Disposable =
        partnerQuotesProvider.stream()
            .map {
                quotesRepository.save(it)
                val f = quotesRepository.get(it.isin, Instant.EPOCH)
                f.isEmpty()
            }
            .subscribe()
}