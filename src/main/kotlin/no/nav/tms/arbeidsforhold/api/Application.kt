package no.nav.tms.arbeidsforhold.api

import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.*
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.*
import no.nav.tms.arbeidsforhold.api.setup.TokenExchanger
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder

fun main() {
    val environment = Environment()

    val httpClient = HttpClientBuilder.build()

    val tokenExchanger = TokenExchanger(
        tokendingsService = TokendingsServiceBuilder.buildTokendingsService(),
        aaregServicesClientId = environment.aaregServicesClientId
    )

    val arbeidsforholdService = ArbeidsforholdService(
        aaregServicesConsumer = AaregServicesConsumer(httpClient, environment.aaregServicesUrl, tokenExchanger),
        eregServicesConsumer = EregServicesConsumer(httpClient, environment.eregServicesUrl)
    )

    val arbeidsforholdRoutes: Route.() -> Unit = {
        arbeidsforholdRoutes(arbeidsforholdService)
    }

    val legacyRoutes: Route.() -> Unit = {
        arbeidsgiverRoute(arbeidsforholdService)
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
            mainModule(
                arbeidsforholdRoutes = arbeidsforholdRoutes,
                legacyRoutes = legacyRoutes,
                httpClient = httpClient,
                corsAllowedOrigins = environment.corsAllowedOrigins,
                corsAllowedSchemes = environment.corsAllowedSchemes
            )
        }
    ).start(wait = true)
}
