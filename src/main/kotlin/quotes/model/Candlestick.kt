package quotes.model

import java.time.Instant

data class Candlestick(
    val start: Instant,
    val end: Instant,
    val open: Money,
    val close: Money,
    val low: Money,
    val high: Money
)