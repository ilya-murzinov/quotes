package quotes.module

import quotes.config.Config
import quotes.integration.PartnerInstrumentsProvider
import quotes.integration.PartnerQuotesProvider

class ProviderModule(config: Config) {

    val partnerQuotesProvider = PartnerQuotesProvider(config)
    val partnerInstrumentsProvider = PartnerInstrumentsProvider(config)
}