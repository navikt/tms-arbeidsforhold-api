package no.nav.tms.arbeidsforhold.api.arbeidsforhold

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.text.DecimalFormat

data class Arbeidsforhold(
    val navArbeidsforholdId: Long?,
    val eksternArbeidsforholdId: String?,
    val yrke: String?,
    val arbeidsgiver: Arbeidsgiver?,
    val opplysningspliktigarbeidsgiver: Arbeidsgiver?,
    val ansettelsesperiode: Ansettelsesperiode?,
    val utenlandsopphold: List<Utenlandsopphold>,
    val permisjonPermittering: List<PermisjonPermittering>,
    @JsonUnwrapped
    val ansettelsesdetaljer: Ansettelsesdetaljer?
) {
    data class Ansettelsesdetaljer(
        val type: String?,
        val sistBekreftet: String?,
        val arbeidsavtaler: List<Arbeidsavtale>,
        val ansettelsesform: String?,
        val antallTimerForTimelonnet: List<AntallTimerForTimeloennetDto>?,
        val antallTimerPrUke: Double?,
        val arbeidstidsordning: String?,
        val sisteStillingsendring: String?,
        val sisteLoennsendring: String?,
        val stillingsprosent: Double?,
        val fartsomraade: String?,
        val skipsregister: String?,
        val skipstype: String?
    )

    companion object {
        fun mapArbeidsforhold(
            response: AaregResponse.Arbeidsforhold,
            arbeidsgivernavn: String?,
            opplysningspliktignavn: String?
        ): Arbeidsforhold {

            return Arbeidsforhold(
                navArbeidsforholdId = response.navArbeidsforholdId,
                eksternArbeidsforholdId = response.id,
                yrke = response.ansettelsesdetaljer.gjeldende()?.yrke?.beskrivelse,
                arbeidsgiver = response.arbeidssted?.let { mapArbeidsgiver(it, arbeidsgivernavn) },
                opplysningspliktigarbeidsgiver = response.opplysningspliktig?.let { mapArbeidsgiver(it, opplysningspliktignavn) },
                ansettelsesperiode = response.ansettelsesperiode?.let { mapAnsettelsesperiode(it) },
                utenlandsopphold = response.utenlandsopphold.map { mapUtenlandsopphold(it) },
                permisjonPermittering = (response.permisjoner + response.permitteringer)
                    .map { mapPermisjonPermittering(it) }
                    .sortedBy { it.id },
                ansettelsesdetaljer = null
            )
        }

        fun mapDetaljertForhold(
            response: AaregResponse.Arbeidsforhold,
            arbeidsgivernavn: String?,
            opplysningspliktignavn: String?
        ): Arbeidsforhold {

            val ansettelse = response.ansettelsesdetaljer.gjeldende()

            return if (ansettelse != null) {

                mapArbeidsforhold(response, arbeidsgivernavn, opplysningspliktignavn).copy(
                    yrke = ansettelse.yrke?.let {
                        "${it.beskrivelse} (Yrkeskode: ${ it.kode })"
                    },
                    ansettelsesdetaljer = Ansettelsesdetaljer(
                        type = response.type?.beskrivelse,
                        sistBekreftet = response.sistBekreftet,
                        antallTimerForTimelonnet = response.timerMedTimeloenn
                            .map { mapTimerMedTimeloenn(it) },
                        arbeidsavtaler = when {
                            response.ansettelsesdetaljer.size > 1 -> {
                                response.ansettelsesdetaljer.map { mapArbeidsavtale(it) }
                            }
                            else -> emptyList()
                        },
                        ansettelsesform = ansettelse.ansettelsesform?.beskrivelse,
                        antallTimerPrUke = ansettelse.antallTimerPrUke,
                        arbeidstidsordning = ansettelse.arbeidstidsordning?.beskrivelse,
                        sisteStillingsendring = ansettelse.sisteStillingsprosentendring,
                        stillingsprosent = ansettelse.avtaltStillingsprosent,
                        sisteLoennsendring = ansettelse.sisteLoennsendring,
                        fartsomraade = ansettelse.fartsomraade?.beskrivelse,
                        skipsregister = ansettelse.skipsregister?.beskrivelse,
                        skipstype = ansettelse.fartoeystype?.beskrivelse
                    )
                )
            } else {
                mapArbeidsforhold(response, arbeidsgivernavn, opplysningspliktignavn)
            }
        }

        private fun List<AaregResponse.Ansettelsesdetaljer>.gjeldende() =
            firstOrNull { it.rapporteringsmaaneder?.til == null }

        private fun mapArbeidsavtale(detaljer: AaregResponse.Ansettelsesdetaljer) = Arbeidsavtale(
            ansettelsesform = detaljer.ansettelsesform?.beskrivelse,
            antallTimerPrUke = detaljer.antallTimerPrUke,
            arbeidstidsordning = detaljer.arbeidstidsordning?.beskrivelse,
            sisteStillingsendring = detaljer.sisteStillingsprosentendring,
            stillingsprosent = detaljer.avtaltStillingsprosent,
            sisteLoennsendring = detaljer.sisteLoennsendring,
            yrke = detaljer.yrke?.let {
                "${it.beskrivelse} (Yrkeskode: ${ it.kode })"
            },
            gyldighetsperiode = PeriodeDto(
                periodeFra = detaljer.rapporteringsmaaneder?.fra,
                periodeTil = detaljer.rapporteringsmaaneder?.til
            ),
            fartsomraade = detaljer.fartsomraade?.beskrivelse,
            skipsregister = detaljer.skipsregister?.beskrivelse,
            skipstype = detaljer.fartoeystype?.beskrivelse
        )


        private fun mapAnsettelsesperiode(periode: AaregResponse.Ansettelsesperiode) = Ansettelsesperiode(
            periode = PeriodeDto(
                periodeFra = periode.startdato,
                periodeTil = periode.sluttdato
            ),
            varslingskode = periode.varsling?.beskrivelse,
            sluttaarsak = periode.sluttaarsak?.beskrivelse
        )

        private fun mapArbeidsgiver(
            arbeidsgiver: AaregResponse.Identer,
            arbeidsgivernavn: String?
        ) = Arbeidsgiver(
            type = if (AaregIdentHelper.erOrganisasjon(arbeidsgiver)) {
                AaregIdentHelper.ORGANISASJON
            } else  {
                arbeidsgiver.type
            },
            orgnr = arbeidsgiver.identer?.firstOrNull { AaregIdentHelper.ORGANISASJONSNUMMER == it.type }?.ident,
            fnr = arbeidsgiver.identer?.firstOrNull { AaregIdentHelper.FOLKEREGISTERIDENT == it.type }?.ident,
            orgnavn = arbeidsgivernavn
        )

        private fun mapUtenlandsopphold(opphold: AaregResponse.Utenlandsopphold) = Utenlandsopphold(
            land = opphold.land?.beskrivelse,
            periode = PeriodeDto(
                periodeFra = opphold.startdato,
                periodeTil = opphold.sluttdato
            ),
            rapporteringsperiode = opphold.rapporteringsmaaned,
        )

        private val prosentFormat = DecimalFormat("#.# '%'")

        private fun mapPermisjonPermittering(permisjonPermittering: AaregResponse.PermisjonPermittering) =
            PermisjonPermittering(
                id = permisjonPermittering.id,
                type = permisjonPermittering.type?.beskrivelse,
                periode = PeriodeDto(
                    periodeFra = permisjonPermittering.startdato,
                    periodeTil = permisjonPermittering.sluttdato
                ),
                prosent = permisjonPermittering.prosent?.let {
                    prosentFormat.format(it)
                } ?: throw IllegalArgumentException("permisjonsprosent kan ikke være null")
            )

        private val antallTimerFormat = DecimalFormat("#.##")

        private fun mapTimerMedTimeloenn(timer: AaregResponse.TimerMedTimeloenn) =
            AntallTimerForTimeloennetDto(
                antallTimer = antallTimerFormat.format(timer.antall),
                periode = PeriodeDto(
                    periodeFra = timer.startdato,
                    periodeTil = timer.sluttdato
                ),
                rapporteringsperiode = timer.rapporteringsmaaned
            )
    }
}

data class Arbeidsavtale(
    val ansettelsesform: String?,
    val antallTimerPrUke: Double?,
    val arbeidstidsordning: String?,
    val sisteStillingsendring: String?,
    val sisteLoennsendring: String?,
    val yrke: String?,
    val gyldighetsperiode: PeriodeDto?,
    val stillingsprosent: Double?,
    val fartsomraade: String?,
    val skipsregister: String?,
    val skipstype: String?
)

data class Ansettelsesperiode(
    val periode: PeriodeDto?,
    val varslingskode: String?,
    val sluttaarsak: String?
)

data class AntallTimerForTimeloennetDto(
    val antallTimer: String?,
    val periode: PeriodeDto?,
    val rapporteringsperiode: String?
)

data class PermisjonPermittering(
    @JsonIgnore val id: String?,
    val periode: PeriodeDto?,
    val type: String?,
    val prosent: String?
)

data class Utenlandsopphold(
    val periode: PeriodeDto?,
    val rapporteringsperiode: String?,
    val land: String?
)

data class Arbeidsgiver(
    val orgnr: String?,
    val fnr: String?,
    val type: String?,
    val orgnavn: String?
)

data class PeriodeDto(
    val periodeFra: String?,
    val periodeTil: String?
)

