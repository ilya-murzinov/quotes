package quotes.integration

import com.fasterxml.jackson.databind.JsonNode
import quotes.config.Config
import quotes.model.Isin
import quotes.model.Money
import quotes.model.Quote
import java.time.Instant

class PartnerQuotesProvider(config: Config)
    : PartnerDataProvider<Quote>(config.quotesURI) {

    override fun fromJson(json: JsonNode): Quote {
        val data = json.get("data")
        return Quote(
            Isin(data.get("isin").asText()),
            Money(data.get("price").asText().toBigDecimal()),
            Instant.now())
    }
}