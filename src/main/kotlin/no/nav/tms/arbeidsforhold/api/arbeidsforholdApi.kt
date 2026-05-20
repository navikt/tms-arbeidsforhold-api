package no.nav.tms.arbeidsforhold.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tms.common.metrics.installTmsMicrometerMetrics
import no.nav.tms.arbeidsforhold.api.setup.ConsumerException
import no.nav.tms.common.logging.TeamLogs
import no.nav.tms.token.support.user.token.verification.Issuer
import no.nav.tms.token.support.user.token.verification.userToken

fun Application.mainModule(
    arbeidsforholdRoutes: Route.() -> Unit,
    legacyRoutes: Route.() -> Unit,
    httpClient: HttpClient,
    corsAllowedOrigins: String,
    corsAllowedSchemes: String,
    authInstaller: Application.() -> Unit = {
        authentication {
            userToken {

            }
            userToken(SYSTEM_FACING_API) {
                configureIssuers(Issuer.Tokenx)
            }
        }
    }
) {
    val log = KotlinLogging.logger {}
    val teamLog = TeamLogs.logger { }

    authInstaller()

    install(CORS) {
        allowHost(host = corsAllowedOrigins, schemes = listOf(corsAllowedSchemes))
        allowCredentials = true
        allowHeader(HttpHeaders.ContentType)
    }

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when(cause) {
                is ConsumerException -> {
                    log.error { "Kall mot ${cause.externalService} [${cause.endpoint}] feiler med kode [${cause.status}]" }
                    teamLog.error { "Kall mot ${cause.externalService} [${cause.endpoint}] feiler med kode [${cause.status}] og melding: ${cause.responseContent}" }
                }
                else -> {
                    log.error { "Uventet feil ved henting av arbeidsforhold" }
                    teamLog.error(cause) { "Uventet feil ved henting av arbeidsforhold" }
                }
            }
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        jackson { jsonConfig() }
    }

    installTmsMicrometerMetrics {
        setupMetricsRoute = true
        installMicrometerPlugin = true
    }

    routing {
        metaRoutes()
        authenticate {
            arbeidsforholdRoutes()
        }
        authenticate(SYSTEM_FACING_API) {
            legacyRoutes()
        }
    }

    configureShutdownHook(httpClient)
}

const val SYSTEM_FACING_API = "system_facing"

private fun Route.metaRoutes() {
    get("/internal/isalive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/internal/isready") {
        call.respondText(text = "READY", contentType = ContentType.Text.Plain)
    }
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}

fun ObjectMapper.jsonConfig(): ObjectMapper {
    registerKotlinModule()
    registerModule(JavaTimeModule())
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    return this
}
