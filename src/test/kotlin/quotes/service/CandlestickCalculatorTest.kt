package quotes.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import quotes.TestData.aQuote
import quotes.model.Candlestick
import quotes.model.Money
import quotes.service.CandlestickInterval.*
import java.time.Instant

class CandlestickCalculatorTest {

    private val candlesticksCalculator = CandlesticksCalculator(MINUTE)
    private val now = Instant.parse("2021-07-24T05:20:24Z")

    @Test
    fun `should return a candlestick for one quote`() {
        // given
        val quote = aQuote(timestamp = now)

        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf(quote))

        // then
        assertThat(result).containsExactly(
            Candlestick(
                Instant.parse("2021-07-24T05:20:00Z"),
                Instant.parse("2021-07-24T05:21:00Z"),
                quote.price,
                quote.price,
                quote.price,
                quote.price
            )
        )
    }

    @Test
    fun `should return a candlestick for a list of quotes in one interval`() {
        // given
        val quote0 = aQuote(price = Money(10.toBigDecimal()), timestamp = now)
        val quote1 = aQuote(price = Money(500.toBigDecimal()), timestamp = now.plusSeconds(2))
        val quote2 = aQuote(price = Money(1.toBigDecimal()), timestamp = now.plusSeconds(10))
        val quote3 = aQuote(price = Money(15.toBigDecimal()), timestamp = now.plusSeconds(20))

        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf(quote0, quote1, quote2, quote3))

        // then
        assertThat(result).containsExactly(
            Candlestick(
                Instant.parse("2021-07-24T05:20:00Z"),
                Instant.parse("2021-07-24T05:21:00Z"),
                Money(10.toBigDecimal()),
                Money(15.toBigDecimal()),
                Money(1.toBigDecimal()),
                Money(500.toBigDecimal())
            )
        )
    }

    @Test
    fun `should return a candlestick for a list of quotes in multiple intervals`() {
        // given
        val quote0 = aQuote(price = Money(10.toBigDecimal()), timestamp = now)
        val quote1 = aQuote(price = Money(500.toBigDecimal()), timestamp = now.plusSeconds(60))
        val quote2 = aQuote(price = Money(1.toBigDecimal()), timestamp = Instant.parse("2021-07-24T05:22:00Z"))
        val quote3 = aQuote(price = Money(15.toBigDecimal()), timestamp = now.plusSeconds(180))

        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf(quote0, quote1, quote2, quote3))

        // then
        assertThat(result).containsExactly(
            Candlestick(
                Instant.parse("2021-07-24T05:20:00Z"),
                Instant.parse("2021-07-24T05:21:00Z"),
                quote0.price,
                quote0.price,
                quote0.price,
                quote0.price
            ),
            Candlestick(
                Instant.parse("2021-07-24T05:21:00Z"),
                Instant.parse("2021-07-24T05:22:00Z"),
                quote0.price,
                quote1.price,
                quote1.price,
                quote1.price
            ),
            Candlestick(
                Instant.parse("2021-07-24T05:22:00Z"),
                Instant.parse("2021-07-24T05:23:00Z"),
                quote2.price,
                quote2.price,
                quote2.price,
                quote2.price
            ),
            Candlestick(
                Instant.parse("2021-07-24T05:23:00Z"),
                Instant.parse("2021-07-24T05:24:00Z"),
                quote2.price,
                quote3.price,
                quote3.price,
                quote3.price
            ),
        )
    }

    @Test
    fun `should return a candlestick for a list of quotes in multiple intervals with gaps`() {
        // given
        val quote0 = aQuote(price = Money(10.toBigDecimal()), timestamp = now)
        val quote1 = aQuote(price = Money(500.toBigDecimal()), timestamp = now.plusSeconds(60))
        val quote3 = aQuote(price = Money(15.toBigDecimal()), timestamp = now.plusSeconds(240))

        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf(quote0, quote1, quote3))

        // then
        assertThat(result).containsExactly(
            Candlestick(
                Instant.parse("2021-07-24T05:20:00Z"),
                Instant.parse("2021-07-24T05:21:00Z"),
                quote0.price,
                quote0.price,
                quote0.price,
                quote0.price
            ),
            Candlestick(
                Instant.parse("2021-07-24T05:21:00Z"),
                Instant.parse("2021-07-24T05:22:00Z"),
                quote0.price,
                quote1.price,
                quote1.price,
                quote1.price
            ),
            Candlestick(
                Instant.parse("2021-07-24T05:22:00Z"),
                Instant.parse("2021-07-24T05:23:00Z"),
                quote1.price,
                quote1.price,
                quote1.price,
                quote1.price
            ),
            Candlestick(
                Instant.parse("2021-07-24T05:23:00Z"),
                Instant.parse("2021-07-24T05:24:00Z"),
                quote1.price,
                quote1.price,
                quote1.price,
                quote1.price
            ),
            Candlestick(
                Instant.parse("2021-07-24T05:24:00Z"),
                Instant.parse("2021-07-24T05:25:00Z"),
                quote1.price,
                quote3.price,
                quote3.price,
                quote3.price
            ),
        )
    }

    @Test
    fun `should return empty list for empty list of quote`() {
        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should skip empty intervals before start`() {
        // given
        val candlesticksCalculator = CandlesticksCalculator(FIFTEEN_SECONDS)
        val quote = aQuote(timestamp = now)

        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf(quote))

        // then
        assertThat(result).containsExactly(
            Candlestick(
                Instant.parse("2021-07-24T05:20:15Z"),
                Instant.parse("2021-07-24T05:20:30Z"),
                quote.price,
                quote.price,
                quote.price,
                quote.price
            )
        )
    }

    @Test
    fun `should return candlesticks with HOUR interval`() {
        // given
        val candlesticksCalculator = CandlesticksCalculator(HOUR)
        val quote = aQuote(timestamp = now)

        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf(quote))

        // then
        assertThat(result).containsExactly(
            Candlestick(
                Instant.parse("2021-07-24T05:00:00Z"),
                Instant.parse("2021-07-24T06:00:00Z"),
                quote.price,
                quote.price,
                quote.price,
                quote.price
            )
        )
    }

    @Test
    fun `should return candlesticks with DAY interval`() {
        // given
        val candlesticksCalculator = CandlesticksCalculator(DAY)
        val quote = aQuote(timestamp = now)

        // when
        val result = candlesticksCalculator.calculateCandlesticksFor(listOf(quote))

        // then
        assertThat(result).containsExactly(
            Candlestick(
                Instant.parse("2021-07-24T00:00:00Z"),
                Instant.parse("2021-07-25T00:00:00Z"),
                quote.price,
                quote.price,
                quote.price,
                quote.price
            )
        )
    }
}