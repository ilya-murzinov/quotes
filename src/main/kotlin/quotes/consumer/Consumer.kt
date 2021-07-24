package quotes.consumer

import reactor.core.Disposable

interface Consumer {

    fun start(): Disposable
}