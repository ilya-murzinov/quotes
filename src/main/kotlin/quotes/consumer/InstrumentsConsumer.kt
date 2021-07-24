package quotes.consumer

import quotes.integration.PartnerInstrumentsProvider
import quotes.model.ChangeType.ADD
import quotes.model.ChangeType.DELETE
import quotes.repos.InstrumentsRepository
import quotes.repos.QuotesRepository
import reactor.core.Disposable

class InstrumentsConsumer(
    private val instrumentsRepository: InstrumentsRepository,
    private val quotesRepository: QuotesRepository,
    private val partnerInstrumentsProvider: PartnerInstrumentsProvider
) : Consumer {

    override fun start(): Disposable =
        partnerInstrumentsProvider.stream()
            .map {
                when (it.changeType) {
                    ADD -> instrumentsRepository.save(it.instrument)
                    DELETE -> {
                        instrumentsRepository.remove(it.instrument.isin)
                        quotesRepository.removeFor(it.instrument.isin)
                    }
                }
                it
            }
            .subscribe()
}