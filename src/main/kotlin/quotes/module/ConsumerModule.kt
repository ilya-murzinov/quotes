package quotes.module

import quotes.consumer.InstrumentsConsumer
import quotes.consumer.QuotesConsumer

class ConsumerModule(repositoryModule: RepositoryModule,
                     providerModule: ProviderModule) {

    private val instrumentsConsumer = InstrumentsConsumer(
        repositoryModule.instrumentsRepository,
        repositoryModule.quotesRepository,
        providerModule.partnerInstrumentsProvider)
    private val quotesConsumer = QuotesConsumer(
        repositoryModule.quotesRepository,
        providerModule.partnerQuotesProvider)

    val allConsumers = listOf(instrumentsConsumer, quotesConsumer)
}