package no.nav.tms.arbeidsforhold.api.arbeidsforhold

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.tms.arbeidsforhold.api.setup.ConsumerMetrics
import no.nav.tms.arbeidsforhold.api.setup.HeaderHelper.addNavHeaders

class EregServicesConsumer(
    private val client: HttpClient,
    private val eregServicesUrl: String
) {
    private val log = KotlinLogging.logger { }
    private val secureLog = KotlinLogging.logger("secureLog")

    private val metrics = ConsumerMetrics.init { }

    suspend fun hentOrganisasjonsnavn(orgnr: String, gyldigDato: String?): String {

        val eregResponse = metrics.measureRequest("organisasjon_info") {
            client.get("$eregServicesUrl/v2/organisasjon/$orgnr/noekkelinfo") {
                addNavHeaders()

                if (gyldigDato != null) {
                    parameter("gyldigDato", gyldigDato)
                }
            }
        }

        return if (eregResponse.status.isSuccess()) {
            eregResponse.body<EregOrganisasjon>()
                .navn
                .sammensattnavn
        } else {
            val feilmelding = eregResponse.bodyAsText()
            secureLog.warn { "Oppslag mot EREG på organisasjonsnummer [$orgnr] feilet med melding: [$feilmelding]." }
            log.warn { "Oppslag mot EREG på organisasjonsnummer [$orgnr] feilet." }

            orgnr
        }
    }

    data class EregOrganisasjon(
        val navn: Navn
    )

    data class Navn(
        val sammensattnavn: String = ""
    )
}
