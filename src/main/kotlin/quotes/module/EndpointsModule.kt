package quotes.module

import quotes.config.Config
import quotes.endpoint.InstrumentsEndpoint

class EndpointsModule(config: Config, repositoryModule: RepositoryModule, serviceModule: ServiceModule) {
    val instrumentsEndpoint = InstrumentsEndpoint(
        config.chartConfig,
        repositoryModule.instrumentsRepository,
        repositoryModule.quotesRepository,
        serviceModule.candlesticksConverter,
        serviceModule.hotInstrumentsProvider)
}