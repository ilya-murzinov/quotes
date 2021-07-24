package quotes.service

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.*

enum class CandlestickInterval(val interval: Duration, val scale: ChronoUnit) {
    FIFTEEN_SECONDS(Duration.ofSeconds(15), MINUTES),
    MINUTE(Duration.ofMinutes(1), MINUTES),
    HOUR(Duration.ofHours(1), HOURS),
    DAY(Duration.ofDays(1), DAYS),
}