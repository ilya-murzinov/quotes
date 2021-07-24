package quotes.model

import java.time.Instant

data class Quote(
    val isin: Isin,
    val price: Money,
    val timestamp: Instant
)