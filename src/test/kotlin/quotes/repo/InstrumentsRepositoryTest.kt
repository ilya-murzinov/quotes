package quotes.repo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import quotes.TestData.anInstrument
import quotes.exception.InstrumentNotFoundException
import quotes.model.Isin
import quotes.repos.InstrumentsRepository
import java.util.*

class InstrumentsRepositoryTest {

    private val instrumentsRepository = InstrumentsRepository()

    @Test
    fun `should get instrument by isin`() {
        // given
        val instrument = instrumentsRepository.save(anInstrument())

        // then
        assertThat(instrumentsRepository.get(instrument.isin)).isEqualTo(instrument)
    }

    @Test
    fun `should find instrument by isin`() {
        // given
        val instrument = instrumentsRepository.save(anInstrument())

        // then
        assertThat(instrumentsRepository.find(instrument.isin)).isEqualTo(instrument)
    }

    @Test
    fun `should fail when instrument doesn't exist`() {
        assertThatThrownBy { instrumentsRepository.get(Isin(UUID.randomUUID().toString())) }
            .isInstanceOf(InstrumentNotFoundException::class.java)
    }

    @Test
    fun `should return all instruments`() {
        // given
        val instrument0 = instrumentsRepository.save(anInstrument())
        val instrument1 = instrumentsRepository.save(anInstrument())

        // then
        assertThat(instrumentsRepository.all()).containsExactlyInAnyOrder(instrument0, instrument1)
    }

    @Test
    fun `should remove instrument by isin`() {
        // given
        val instrument = instrumentsRepository.save(anInstrument())

        // when
        instrumentsRepository.remove(instrument.isin)

        // then
        assertThat(instrumentsRepository.find(instrument.isin)).isNull()
    }
}