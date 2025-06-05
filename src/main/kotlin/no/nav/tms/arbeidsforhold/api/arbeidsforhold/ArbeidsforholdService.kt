package no.nav.tms.arbeidsforhold.api.arbeidsforhold

import no.nav.tms.arbeidsforhold.api.UserPrincipal
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.AaregIdentHelper.erOrganisasjon
import no.nav.tms.arbeidsforhold.api.arbeidsforhold.AaregIdentHelper.firstOfTypeOrganisasjon


class ArbeidsforholdService(
    private val aaregServicesConsumer: AaregServicesConsumer,
    private val eregServicesConsumer: EregServicesConsumer,
) {
    suspend fun hentAlleArbeidsforhold(user: UserPrincipal): List<Arbeidsforhold> {
        return aaregServicesConsumer.hentAlleArbeidsforhold(user).map { response ->
            Arbeidsforhold.mapArbeidsforhold(
                response = response,
                arbeidsgivernavn = orgnavnForPeriode(response.arbeidssted, response.ansettelsesperiode),
                opplysningspliktignavn = orgnavnForPeriode(response.opplysningspliktig, response.ansettelsesperiode),
            )
        }
    }

    suspend fun hentDetaljertArbeidsforhold(user: UserPrincipal, forholdsId: Int): Arbeidsforhold {
        return aaregServicesConsumer.hentArbeidsforhold(user, forholdsId).let { response ->
            Arbeidsforhold.mapDetaljertForhold(
                response = response,
                arbeidsgivernavn = orgnavnForPeriode(response.arbeidssted, response.ansettelsesperiode),
                opplysningspliktignavn = orgnavnForPeriode(response.opplysningspliktig, response.ansettelsesperiode),
            )
        }
    }

    private suspend fun orgnavnForPeriode(identer: AaregResponse.Identer?, ansettelsesperiode: AaregResponse.Ansettelsesperiode?): String? {
        if (identer == null) {
            return null
        }

        val orgnr = identer.identer?.let { firstOfTypeOrganisasjon(it) }

        return when {
            orgnr == null -> null
            erOrganisasjon(identer) -> eregServicesConsumer.hentOrganisasjonsnavn(orgnr, ansettelsesperiode?.sluttdato)
            else -> orgnr
        }
    }
}
