package quotes

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.handler.codec.http.HttpResponseStatus.OK
import quotes.config.Config
import quotes.model.ChangeType
import quotes.model.ChangeType.ADD
import quotes.model.ChangeType.DELETE
import quotes.model.Instrument
import quotes.model.InstrumentUpdate
import quotes.model.Quote
import quotes.module.AppModule
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeoutException

abstract class FunctionalTestSetup {

    private val om = ObjectMapper()
    private val client = HttpClient.create()
    private val testConfig = object : Config() {
        override val serverPort = randomPort()
        override val partnerURI = URI.create("ws://localhost:${randomPort()}")
    }
    private val appModule = AppModule(testConfig)
    private val baseUri = "http://localhost:${testConfig.serverPort}"

    val instrumentsRepository = appModule.repositoryModule.instrumentsRepository
    val quotesRepository = appModule.repositoryModule.quotesRepository

    val partnerServiceStub = PartnerServiceStub(testConfig.partnerURI.port)

    init {
        val server = appModule.serverModule.server.run()
        Runtime.getRuntime().addShutdownHook(Thread { server.disposeNow() })
    }

    fun get(path: String): JsonNode {
        val body = client
            .get()
            .uri("$baseUri/$path")
            .response { rs, content ->
                if (rs.status() == OK)
                    content.asString()
                else
                    Mono.error(
                        IllegalStateException("""
                        An error occurred while calling ${rs.uri()}:
                        Status: ${rs.status()}
                        Headers: ${rs.responseHeaders()}
                        """.trimIndent())
                    )
            }
            .log()
            .blockLast()

        return om.readTree(body)
    }

    private fun randomPort(): Int = 40000 + Random().nextInt(10000)

    fun givenExists(instrument: Instrument): Instrument {
        instrumentsRepository.save(instrument)
        return instrument
    }

    fun givenExists(quote: Quote): Quote {
        quotesRepository.save(quote)
        return quote
    }

    fun partnerAdds(instrument: Instrument): Disposable = givenPartnerUpdates(instrument, ADD)

    fun partnerDeletes(instrument: Instrument): Disposable = givenPartnerUpdates(instrument, DELETE)

    fun partnerReturns(quote: Quote): Disposable {
        val partnerService = partnerServiceStub.startPartnerService(listOf(), listOf(quote))
        appModule.consumerModule.allConsumers.map { it.start() }
        return partnerService
    }

    private fun givenPartnerUpdates(instrument: Instrument, changeType: ChangeType): Disposable {
        val partnerService = partnerServiceStub.startPartnerService(listOf(InstrumentUpdate(instrument, changeType)), listOf())
        appModule.consumerModule.allConsumers.map { it.start() }
        return partnerService
    }

    fun waitFor(block: () -> Unit) = waitFor(Duration.ofSeconds(5), block) {}

    fun waitFor(block: () -> Unit, cleanup: () -> Unit) = waitFor(Duration.ofSeconds(5), block, cleanup)

    fun waitFor(timeout: Duration, block: () -> Unit, cleanup: () -> Unit) {
        val start = Instant.now()
        while (start.plus(timeout).isAfter(Instant.now())) {
            try {
                block.invoke()
                return
            } catch (e: Throwable) {
                Thread.sleep(100)
            }
        }
        cleanup.invoke()
        throw TimeoutException("Condition timed out")
    }
}