package no.nav.tms.arbeidsforhold.api.arbeidsforhold

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.isSuccess
import no.nav.tms.arbeidsforhold.api.UserPrincipal
import no.nav.tms.arbeidsforhold.api.setup.ConsumerException
import no.nav.tms.arbeidsforhold.api.setup.ConsumerMetrics
import no.nav.tms.arbeidsforhold.api.setup.HeaderHelper.addNavHeaders
import no.nav.tms.arbeidsforhold.api.setup.HeaderHelper.addKontekstHeader
import no.nav.tms.arbeidsforhold.api.setup.HeaderHelper.authorization
import no.nav.tms.arbeidsforhold.api.setup.TokenExchanger

class AaregServicesConsumer(
    private val client: HttpClient,
    private val aaregServicesUrl: String,
    private val tokenExchanger: TokenExchanger
) {
    private val metrics = ConsumerMetrics.init { }

    suspend fun hentAlleArbeidsforhold(user: UserPrincipal): List<AaregResponse.Arbeidsforhold> {

        val response = metrics.measureRequest("alle_forhold") {
            client.get("$aaregServicesUrl/api/v2/arbeidstaker/arbeidsforhold") {
                parameter("regelverk", REGELVERK)
                parameter("sporingsinformasjon", false)
                parameter("arbeidsforholdtype", ARBEIDSFORHOLDTYPER)
                parameter("arbeidsforholdstatus", ARBEIDSFORHOLDSTATUS)
                authorization(tokenExchanger.aaregServicesToken(user.accessToken))
                addNavHeaders(ident = user.ident)
            }
        }

        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw ConsumerException.fromResponse("aareg-services", response)
        }
    }

    suspend fun hentArbeidsforhold(user: UserPrincipal, arbeidsforholdId: Int, brukerkontekst: Brukerkontekst): AaregResponse.Arbeidsforhold {
        val response = metrics.measureRequest("enkelt_forhold") {
            client.get("$aaregServicesUrl/api/v2/arbeidsforhold/$arbeidsforholdId") {
                parameter("historikk", true)
                parameter("sporingsinformasjon", false)
                authorization(tokenExchanger.aaregServicesToken(user.accessToken))
                addNavHeaders()
                addKontekstHeader(brukerkontekst)
            }
        }

        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw ConsumerException.fromResponse("aareg-services", response)
        }
    }

    enum class Brukerkontekst {
        Privat, Arbeidsgiver
    }

    companion object {
        private const val REGELVERK = "A_ORDNINGEN"
        private const val ARBEIDSFORHOLDTYPER =
            "ordinaertArbeidsforhold,maritimtArbeidsforhold,forenkletOppgjoersordning,frilanserOppdragstakerHonorarPersonerMm"
        private const val ARBEIDSFORHOLDSTATUS = "AKTIV,FREMTIDIG,AVSLUTTET"
    }
}
