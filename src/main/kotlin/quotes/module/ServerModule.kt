package quotes.module

import quotes.server.HttpServer
import quotes.config.Config

class ServerModule(config: Config, endpointsModule: EndpointsModule) {
    val server = HttpServer(config, endpointsModule.instrumentsEndpoint::routes)
}