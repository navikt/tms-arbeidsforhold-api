package no.nav.tms.arbeidsforhold.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.*

data class Environment(
    val corsAllowedOrigins: String = "*.nav.no",
    val corsAllowedSchemes: String = "https",

    val eregServicesUrl: String = System.getenv("EREG_SERVICES_URL"),
    val aaregServicesUrl: String = System.getenv("AAREG_SERVICES_URL"),
    val aaregServicesClientId: String = System.getenv("AAREG_SERVICES_CLIENT_ID"),
)

object HttpClientBuilder {

    fun build(httpClientEngine: HttpClientEngine = Apache.create()): HttpClient {
        return HttpClient(httpClientEngine) {
            install(ContentNegotiation) {
                jackson { jsonConfig() }
            }
            install(HttpTimeout)
        }
    }
}
