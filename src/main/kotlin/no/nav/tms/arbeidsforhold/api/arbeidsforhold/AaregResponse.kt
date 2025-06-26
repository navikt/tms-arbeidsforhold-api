package no.nav.tms.arbeidsforhold.api.arbeidsforhold

object AaregResponse {
    data class Arbeidsforhold(
        val ansettelsesperiode: Ansettelsesperiode? = null,
        val timerMedTimeloenn: List<TimerMedTimeloenn> = emptyList(),
        val ansettelsesdetaljer: List<Ansettelsesdetaljer> = emptyList(),
        val id: String? = null,
        val arbeidssted: Identer? = null,
        val arbeidstaker: Identer? = null,
        val navArbeidsforholdId: Long? = null,
        val opplysningspliktig: Identer? = null,
        val permisjoner: List<PermisjonPermittering> = emptyList(),
        val permitteringer: List<PermisjonPermittering> = emptyList(),
        val sistBekreftet: String? = null,
        val type: Kodeverksentitet? = null,
        val utenlandsopphold: List<Utenlandsopphold> = emptyList()
    )

    data class Ansettelsesdetaljer(
        val ansettelsesform: Kodeverksentitet? = null,
        val antallTimerPrUke: Double? = null,
        val arbeidstidsordning: Kodeverksentitet? = null,
        val rapporteringsmaaneder: Rapporteringsmaaneder? = null,
        val sisteLoennsendring: String? = null,
        val sisteStillingsprosentendring: String? = null,
        val avtaltStillingsprosent: Double? = null,
        val yrke: Kodeverksentitet? = null,
        val fartsomraade: Kodeverksentitet? = null,
        val skipsregister: Kodeverksentitet? = null,
        val fartoeystype: Kodeverksentitet? = null
    )

    data class Ansettelsesperiode(
        val sluttaarsak: Kodeverksentitet? = null,
        val varsling: Kodeverksentitet? = null,
        val startdato: String? = null,
        val sluttdato: String? = null
    )

    data class Identer(
        val identer: List<Ident>? = null,
        val type: String? = null
    )

    data class Ident(
        val ident: String? = null,
        val type: String? = null,
    )

    data class PermisjonPermittering(
        val startdato: String? = null,
        val sluttdato: String? = null,
        val id: String? = null,
        val prosent: Double? = null,
        val type: Kodeverksentitet? = null
    )

    data class Rapporteringsmaaneder(
        val fra: String? = null,
        val til: String? = null
    )

    data class TimerMedTimeloenn(
        val antall: Double? = null,
        val startdato: String? = null,
        val sluttdato: String? = null,
        val rapporteringsmaaned: String? = null,
    )

    data class Utenlandsopphold(
        val startdato: String? = null,
        val sluttdato: String? = null,
        val land: Kodeverksentitet? = null,
        val rapporteringsmaaned: String? = null,
    )

    data class Kodeverksentitet(
        val kode: String,
        val beskrivelse: String
    )
}
