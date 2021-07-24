package quotes.endpoint

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import quotes.model.*

object JsonConverters {

    private val om = ObjectMapper()

    fun toJson(instrument: Instrument, candlesticks: List<Candlestick>): JsonNode {
        return toJson(instrument)
            .set("priceHistory", toJson(candlesticks))
    }

    fun toJson(candlesticks: List<Candlestick>): JsonNode =
        toJsonArray(candlesticks, this::toJson)

    fun toJson(candlestick: Candlestick): JsonNode = om.createObjectNode()
        .put("start", candlestick.start.toEpochMilli())
        .put("end", candlestick.end.toEpochMilli())
        .put("openPrice", candlestick.open.value)
        .put("closePrice", candlestick.close.value)
        .put("high", candlestick.high.value)
        .put("low", candlestick.low.value)

    fun toJson(instruments: Collection<Instrument>, quotes: Map<Isin, Quote?>): ArrayNode {
        val elements: List<Pair<Instrument, Quote?>> = instruments.map { it to quotes[it.isin] }
        return toJsonArray(elements) { (instrument, quote) -> toJson(instrument, quote) }
    }

    fun toJson(instrument: Instrument): ObjectNode = om.createObjectNode()
        .put("isin", instrument.isin.value)
        .put("description", instrument.description)

    fun toJson(instrument: Instrument, quote: Quote?): ObjectNode =
        toJson(instrument).put("price", quote?.price?.value)

    fun toJson(hotInstruments: Collection<HotInstrument>): JsonNode =
        toJsonArray(hotInstruments, this::toJson)

    fun toJson(hotInstrument: HotInstrument): JsonNode =
        toJson(hotInstrument.instrument)
            .put("priceChangePercent", hotInstrument.priceChangePercent)

    fun <T> toJsonArray(elements: Collection<T>, transform: (T) -> JsonNode): ArrayNode {
        val result = om.createArrayNode()
        elements.forEach { result.add(transform(it)) }
        return result
    }
}