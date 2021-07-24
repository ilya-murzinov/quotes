package quotes.module

import quotes.repos.InstrumentsRepository
import quotes.repos.QuotesRepository

class RepositoryModule {
    val instrumentsRepository = InstrumentsRepository()
    val quotesRepository = QuotesRepository()
}