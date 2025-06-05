package no.nav.tms.arbeidsforhold.api.arbeidsforhold

object AaregIdentHelper {
    const val ORGANISASJONSNUMMER = "ORGANISASJONSNUMMER"
    const val FOLKEREGISTERIDENT = "FOLKEREGISTERIDENT"

    private const val HOVEDENHET = "Hovedenhet"
    private const val UNDERENHET = "Underenhet"

    const val ORGANISASJON = "Organisasjon"

    fun firstOfTypeOrganisasjon(identer: List<AaregResponse.Ident>): String? {
        return identer.firstOrNull { it.type == ORGANISASJONSNUMMER }
            ?.ident
    }

    fun erOrganisasjon(identer: AaregResponse.Identer) = identer.type.equals(HOVEDENHET) || identer.type.equals(UNDERENHET)
}
