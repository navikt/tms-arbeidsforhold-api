package no.nav.tms.arbeidsforhold.api.setup

import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.AaregServicesConsumer
import java.util.UUID

object HeaderHelper {
    const val CALL_ID_HEADER = "Nav-Call-Id"
    const val NAV_CONSUMER_ID_HEADER = "Nav-Consumer-Id"
    const val NAV_CONSUMER_ID = "tms-arbeidsforhold-api"
    const val NAV_PERSONIDENT_HEADER = "Nav-Personident"
    const val BRUKERKONTEKST = "Nav-Aareg-Kontekst"

    fun HttpRequestBuilder.addNavHeaders(ident: String? = null) {
        header(CALL_ID_HEADER, UUID.randomUUID().toString())
        header(NAV_CONSUMER_ID_HEADER, NAV_CONSUMER_ID)

        if (ident != null) {
            header(NAV_PERSONIDENT_HEADER, ident)
        }
    }

    fun HttpRequestBuilder.addKontekstHeader(rolle: AaregServicesConsumer.Brukerkontekst) {
        header(BRUKERKONTEKST, rolle.name.uppercase())
    }

    fun HttpRequestBuilder.authorization(token: String) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}
