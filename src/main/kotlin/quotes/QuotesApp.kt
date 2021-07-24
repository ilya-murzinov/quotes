package quotes

import quotes.config.Config
import quotes.module.AppModule
import quotes.server.HttpServer.Companion.await

object QuotesApp {

    @JvmStatic
    fun main(args: Array<String>) {
        val appModule = AppModule(Config())
        startConsumers(appModule)
        startServer(appModule)
    }

    private fun startConsumers(appModule: AppModule) {
        appModule.consumerModule.allConsumers.forEach { it.start() }
    }

    private fun startServer(appModule: AppModule) {
        appModule.serverModule.server.run().await()
    }
}