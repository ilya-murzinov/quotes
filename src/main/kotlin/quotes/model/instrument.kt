package quotes.model

import java.math.BigDecimal
import java.math.BigDecimal.ZERO

data class Instrument(
    val isin: Isin,
    val description: String
)

data class InstrumentUpdate(
    val instrument: Instrument,
    val changeType: ChangeType
)

data class HotInstrument(
    val instrument: Instrument,
    val priceChangePercent: BigDecimal
)

data class Isin(val value: String)

data class Money(val value: BigDecimal) {
    companion object{
        fun zero() = Money(ZERO)
    }
}

enum class ChangeType {
    ADD, DELETE
}