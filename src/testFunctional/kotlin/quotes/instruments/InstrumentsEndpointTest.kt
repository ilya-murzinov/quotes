package quotes.instruments

import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.Test
import quotes.FunctionalTestSetup
import quotes.TestData.aQuote
import quotes.TestData.anInstrument
import quotes.model.Isin
import quotes.model.Money
import java.time.temporal.ChronoUnit.MINUTES

class InstrumentsEndpointTest : FunctionalTestSetup() {

    @Test
    fun `should return list of instruments with latest prices`() {
        // given
        val instrument0 = givenExists(anInstrument(isin = Isin("AAA")))
        val instrument1 = givenExists(anInstrument(isin = Isin("BBB")))
        val instrument2 = givenExists(anInstrument(isin = Isin("CCC")))
        val quote00 = givenExists(aQuote(instrument0.isin, price = Money(10.toBigDecimal())))
        val quote01 = givenExists(aQuote(instrument0.isin, price = Money(15.toBigDecimal())))
        val quote10 = givenExists(aQuote(instrument1.isin, price = Money(126.toBigDecimal())))

        // when
        val response = get("instruments")

        // then
        assertThatJson(response).isEqualTo(
            """
            [ 
                {
                  "isin" : "${instrument0.isin.value}",
                  "description" : "${instrument0.description}",
                  "price": ${quote01.price.value}
                },
                {
                  "isin" : "${instrument1.isin.value}",
                  "description" : "${instrument1.description}",
                  "price": ${quote10.price.value}
                },
                {
                  "isin" : "${instrument2.isin.value}",
                  "description" : "${instrument2.description}",
                  "price": null
                }
            ]
        """.trimIndent()
        )
    }

    @Test
    fun `should return price history`() {
        // given
        val instrument = givenExists(anInstrument())
        val quote = givenExists(aQuote(isin = instrument.isin))

        // when
        val response = get("price-history/${instrument.isin.value}")

        // then
        assertThatJson(response).isEqualTo(
            """
            {
              "isin": "${instrument.isin.value}",
              "description": "${instrument.description}",
              "priceHistory": [
                {
                  "start": ${quote.timestamp.truncatedTo(MINUTES).toEpochMilli()},
                  "end": ${quote.timestamp.truncatedTo(MINUTES).plus(1, MINUTES).toEpochMilli()},
                  "openPrice": 1,
                  "closePrice": 1,
                  "high": 1,
                  "low": 1
                }
              ]
            }
        """.trimIndent()
        )
    }

    @Test
    fun `should return hot instruments`() {
        // given
        val instrument0 = givenExists(anInstrument())
        givenExists(aQuote(isin = instrument0.isin))
        givenExists(aQuote(isin = instrument0.isin, price = Money(10.toBigDecimal())))

        val instrument1 = givenExists(anInstrument())
        givenExists(aQuote(isin = instrument1.isin, price = Money(10.toBigDecimal())))
        givenExists(aQuote(isin = instrument1.isin))

        // when
        val response = get("hot-instruments")

        // then
        assertThatJson(response).isEqualTo(
            """
            [
              {
                "isin": "${instrument0.isin.value}",
                "description": "${instrument0.description}",
                "priceChangePercent": 900.0
              },
              {
                "isin": "${instrument1.isin.value}",
                "description": "${instrument1.description}",
                "priceChangePercent": -90.0
              }
            ]
        """.trimIndent()
        )
    }

}