package quotes.module

import quotes.config.Config

class AppModule(config: Config) {
    val repositoryModule = RepositoryModule()
    val providerModule = ProviderModule(config)
    val serviceModule = ServiceModule(config, repositoryModule)

    val consumerModule = ConsumerModule(repositoryModule, providerModule)
    val endpointsModule = EndpointsModule(config, repositoryModule, serviceModule)
    val serverModule = ServerModule(config, endpointsModule)
}