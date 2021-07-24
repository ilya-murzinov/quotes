package quotes.repo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import quotes.TestData.aQuote
import quotes.model.Isin
import quotes.model.Money
import quotes.repos.QuotesRepository
import java.time.Instant
import java.time.Instant.EPOCH
import java.time.temporal.ChronoUnit.HOURS
import java.util.UUID.randomUUID

class QuotesRepositoryTest {

    private val now = Instant.now()
    private val quotesRepository = QuotesRepository()

    @Test
    fun `should return quotes by isin`() {
        // given
        val isin = Isin(randomUUID().toString())
        val quote0 = quotesRepository.save(aQuote(isin))
        val quote1 = quotesRepository.save(aQuote(isin))
        val oldQuote = quotesRepository.save(aQuote(isin, timestamp = now.minus(1, HOURS)))

        // then
        assertThat(quotesRepository.get(isin, now)).containsExactly(quote0, quote1)
    }

    @Test
    fun `should return latest price by isin`() {
        // given
        val isin = Isin(randomUUID().toString())
        val quote0 = quotesRepository.save(aQuote(isin, price = Money(10.toBigDecimal())))
        val quote1 = quotesRepository.save(aQuote(isin, price = Money(20.toBigDecimal())))
        val quote2 = quotesRepository.save(aQuote(isin, price = Money(15.toBigDecimal())))
        val oldQuote = quotesRepository.save(aQuote(isin, timestamp = now.minus(1, HOURS)))

        // then
        assertThat(quotesRepository.latestPrices(listOf(isin))).isEqualTo(mapOf(isin to quote2))
    }

    @Test
    fun `should return all quotes`() {
        // given
        val isin0 = Isin(randomUUID().toString())
        val quote0 = quotesRepository.save(aQuote(isin0))
        val quote1 = quotesRepository.save(aQuote(isin0))
        val oldQuote = quotesRepository.save(aQuote(isin0, timestamp = now.minus(1, HOURS)))
        val isin1 = Isin(randomUUID().toString())
        val oldQuote1 = quotesRepository.save(aQuote(isin1, timestamp = now.minus(2, HOURS)))

        // then
        assertThat(quotesRepository.all(now)).isEqualTo(mapOf(
            isin0 to listOf(quote0, quote1),
            isin1 to listOf()
        ))
    }

    @Test
    fun `should remove quotes by isin`() {
        // given
        val isin = Isin(randomUUID().toString())
        quotesRepository.save(aQuote(isin))
        quotesRepository.save(aQuote(isin))

        // when
        quotesRepository.removeFor(isin)

        // then
        assertThat(quotesRepository.get(isin, EPOCH)).isEmpty()
    }
}