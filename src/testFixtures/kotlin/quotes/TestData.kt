package quotes

import quotes.model.Instrument
import quotes.model.Isin
import quotes.model.Money
import quotes.model.Quote
import java.math.BigDecimal.ONE
import java.time.Instant
import java.util.UUID.randomUUID

object TestData {

    fun anInstrument(isin: Isin = Isin(randomUUID().toString())) = Instrument(isin, "description ${randomUUID()}")

    fun aQuote(
        isin: Isin = Isin(randomUUID().toString()),
        price: Money = Money(ONE),
        timestamp: Instant = Instant.now()
    ) = Quote(isin, price, timestamp)
}