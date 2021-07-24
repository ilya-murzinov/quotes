package quotes.consumers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import quotes.FunctionalTestSetup
import quotes.TestData.aQuote
import java.time.Instant.EPOCH

class QuotesConsumerTest : FunctionalTestSetup() {

    @Test
    fun `should add quote`() {
        // given
        val quote = aQuote()

        // when
        val partnerService = partnerReturns(quote)

        // then
        waitFor(
            {
                assertThat(quotesRepository.get(quote.isin, EPOCH)).allMatch {
                    it.isin == quote.isin &&
                            it.price == quote.price &&
                            it.timestamp.isAfter(quote.timestamp)
                }.hasSize(1)
            },
            { partnerService.dispose() })
    }
}