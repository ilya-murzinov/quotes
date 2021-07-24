package quotes.module

import quotes.config.Config
import quotes.service.CandlesticksCalculator
import quotes.service.HotInstrumentsProvider

class ServiceModule(config: Config, repositoryModule: RepositoryModule) {
    val candlesticksConverter = CandlesticksCalculator(config.chartConfig.interval)
    val hotInstrumentsProvider = HotInstrumentsProvider(
        config.hotInstruments,
        repositoryModule.instrumentsRepository,
        repositoryModule.quotesRepository)
}