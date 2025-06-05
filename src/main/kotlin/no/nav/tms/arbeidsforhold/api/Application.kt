package no.nav.tms.arbeidsforhold.api

import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.*
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.AaregServicesConsumer
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.ArbeidsforholdService
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.EregServicesConsumer
import no.nav.tms.arbeidsforhold.api.setup.TokenExchanger
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.arbeidsforholdRoutes
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

fun main() {
    val environment = Environment()

    val httpClient = HttpClientBuilder.build()

    val tokenExchanger = TokenExchanger(
        tokendingsService = TokendingsServiceBuilder.buildTokendingsService(),
        aaregServicesClientId = environment.aaregServicesClientId
    )

    val userRoutes: Route.() -> Unit = {
        arbeidsforholdRoutes(
            ArbeidsforholdService(
                aaregServicesConsumer = AaregServicesConsumer(httpClient, environment.aaregServicesUrl, tokenExchanger),
                eregServicesConsumer = EregServicesConsumer(httpClient, environment.aaregServicesUrl)
            )
        )
    }

    embeddedServer(
        factory = Netty,
        configure = {
            connector {
                port = 8080
            }
        },
        module = {
            rootPath = "tms-arbeidsforhold-api"
            mainModule(userRoutes, httpClient)
        }
    ).start(wait = true)
}
