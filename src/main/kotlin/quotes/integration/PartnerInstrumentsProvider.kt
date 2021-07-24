package quotes.integration

import com.fasterxml.jackson.databind.JsonNode
import quotes.config.Config
import quotes.model.*

class PartnerInstrumentsProvider(config: Config)
    : PartnerDataProvider<InstrumentUpdate>(config.instrumentsURI) {

    override fun fromJson(json: JsonNode): InstrumentUpdate {
        val data = json.get("data")
        val instrument = Instrument(
            Isin(data.get("isin").asText()),
            data.get("description").asText())

        return InstrumentUpdate(instrument, ChangeType.valueOf(json.get("type").asText()))
    }
}