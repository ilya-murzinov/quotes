package quotes.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import quotes.TestData.aQuote
import quotes.TestData.anInstrument
import quotes.config.HotInstrumentsConfig
import quotes.model.HotInstrument
import quotes.model.Instrument
import quotes.model.Money
import quotes.model.Quote
import quotes.repos.InstrumentsRepository
import quotes.repos.QuotesRepository

class HotInstrumentsProviderTest {

    private val config = HotInstrumentsConfig.default()
    private val instrumentsRepository = mock<InstrumentsRepository>()
    private val quotesRepository = mock<QuotesRepository>()
    private val provider = HotInstrumentsProvider(config, instrumentsRepository, quotesRepository)

    @Test
    fun `should provide hot instruments with price changes`() {
        // given
        val instrument0 = anInstrument()
        val instrument1 = anInstrument()
        val hotInstrument0 = anInstrument()
        val hotInstrument1 = anInstrument()
        val hotInstrument2 = anInstrument()
        val quote0 = aQuote(instrument0.isin)
        val quote1 = aQuote(instrument0.isin, price = Money(1.1.toBigDecimal()))

        val hotQuote00 = aQuote(hotInstrument0.isin)
        val hotQuote01 = aQuote(hotInstrument0.isin, price = Money(1.11.toBigDecimal()))
        val hotQuote10 = aQuote(hotInstrument1.isin)
        val hotQuote11 = aQuote(hotInstrument1.isin, price = Money(10.toBigDecimal()))
        val hotQuote20 = aQuote(hotInstrument2.isin, price = Money(20.toBigDecimal()))
        val hotQuote21 = aQuote(hotInstrument2.isin, price = Money(5.toBigDecimal()))

        givenExists(instrument0, instrument1, hotInstrument0, hotInstrument1, hotInstrument2)
        givenExists(quote0, quote1, hotQuote00, hotQuote01, hotQuote10, hotQuote11, hotQuote20, hotQuote21)

        // when
        val result = provider.hotInstruments()

        // then
        assertThat(result).containsExactly(
            HotInstrument(hotInstrument1, 900.toBigDecimal().setScale(2)),
            HotInstrument(hotInstrument2, (-75).toBigDecimal().setScale(2)),
            HotInstrument(hotInstrument0, 11.toBigDecimal().setScale(2))
        )
    }

    private fun givenExists(vararg instruments: Instrument) {
        given(instrumentsRepository.all()).willReturn(instruments.toList())
    }

    private fun givenExists(vararg quotes: Quote) {
        given(quotesRepository.all(any())).willReturn(quotes.groupBy{ it.isin })
    }
}