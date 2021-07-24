package quotes.server

import quotes.config.Config
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerRoutes
import java.util.function.Consumer

class HttpServer(config: Config, routes: Consumer<HttpServerRoutes>) {

    val server = HttpServer.create()
        .port(config.serverPort)
        .route(routes)

    fun run(): DisposableServer {
        return server.bindNow()
    }

    companion object {
        fun DisposableServer.await() = onDispose().block()
    }
}