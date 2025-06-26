package no.nav.tms.arbeidsforhold.api.arbeidsforhold

import io.ktor.http.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import no.nav.tms.arbeidsforhold.api.setup.HeaderHelper
import no.nav.tms.arbeidsforhold.api.user

fun Route.arbeidsforholdRoutes(arbeidsforholdService: ArbeidsforholdService) {

    get("/arbeidsforhold/forenklet/alle") {
        call.respond(arbeidsforholdService.hentAlleArbeidsforhold(call.user))
    }

    get("/arbeidsforhold/{id}") {
        val forholdsId = call.parameters.requireId()

        call.respond(arbeidsforholdService.hentDetaljertArbeidsforhold(call.user, forholdsId))
    }

    // legacy
    get("/arbeidsforhold") {
        call.respond(arbeidsforholdService.hentAlleArbeidsforhold(call.user))
    }

    get("/arbeidsforholdinnslag/arbeidstaker/{id}") {
        val forholdsId = call.parameters.requireId()

        call.respond(arbeidsforholdService.hentDetaljertArbeidsforhold(call.user, forholdsId))
    }

    get("/arbeidsforholdinnslag/arbeidsgiver/{id}") {
        val forholdsId = call.parameters.requireId()

        call.respond(arbeidsforholdService.hentDetaljertArbeidsforhold(call.user, forholdsId))
    }
}

private fun Parameters.requireId(): Int {
    val idParameter = get("id") ?: throw ArbeidsforholdIdException()

    try {
        return idParameter.toInt()
    } catch (e: NumberFormatException) {
        throw ArbeidsforholdIdException(idParameter)
    }
}


class ArbeidsforholdIdException(val parameter: String? = null): IllegalArgumentException()
