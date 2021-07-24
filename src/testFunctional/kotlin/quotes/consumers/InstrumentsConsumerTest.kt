package quotes.consumers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import quotes.FunctionalTestSetup
import quotes.TestData.aQuote
import quotes.TestData.anInstrument
import java.time.Instant.EPOCH

class InstrumentsConsumerTest : FunctionalTestSetup() {

    @Test
    fun `should add instrument`() {
        // given
        val instrument = anInstrument()

        // when
        val partnerService = partnerAdds(instrument)

        // then
        waitFor(
            { assertThat(instrumentsRepository.get(instrument.isin)).isEqualTo(instrument) },
            { partnerService.dispose() })
    }

    @Test
    fun `should delete instrument and quotes`() {
        // given
        val instrument = givenExists(anInstrument())
        givenExists(aQuote(isin = instrument.isin))
        givenExists(aQuote(isin = instrument.isin))

        // when
        val partnerService = partnerDeletes(instrument)

        // then
        waitFor(
            {
                assertThat(instrumentsRepository.find(instrument.isin)).isNull()
                assertThat(quotesRepository.get(instrument.isin, EPOCH)).isEmpty()
            },
            { partnerService.dispose() })
    }
}