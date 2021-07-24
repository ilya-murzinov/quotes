package quotes.repos

import quotes.exception.InstrumentNotFoundException
import quotes.model.Instrument
import quotes.model.Isin
import java.util.concurrent.ConcurrentHashMap

class InstrumentsRepository {

    private val storage = ConcurrentHashMap<Isin, Instrument>()

    fun save(instrument: Instrument): Instrument {
        storage.putIfAbsent(instrument.isin, instrument)
        return instrument
    }

    fun remove(isin: Isin) = storage.remove(isin)

    fun all(): Collection<Instrument> = storage.values.sortedBy { it.isin.value }

    fun find(isin: Isin): Instrument? = storage[isin]

    fun get(isin: Isin): Instrument = find(isin) ?: throw InstrumentNotFoundException()
}