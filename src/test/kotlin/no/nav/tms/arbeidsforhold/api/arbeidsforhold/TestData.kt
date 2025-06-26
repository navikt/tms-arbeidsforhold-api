package no.nav.tms.arbeidsforhold.api.arbeidsforhold

object TestData {

    const val navArbeidsforholdId = "11223344"
    const val eksternArbeidsforholdId = "A100000000B20000C3000"

    const val yrke = "POTETPLUKKER"
    const val yrkeMedKode = "POTETPLUKKER (9210103)"
    const val ansettelsesperiodeStart = "2020-01-01"

    const val arbeidsgiverOrgnr = "111222333"
    const val arbeidsgiverType = "Organisasjon"
    const val arbeidsgiverOrgnavn = "PETTER POTTIT"
    const val opplysningspliktigOrgnr = "444555666"
    const val opplysningspliktigType = "Organisasjon"
    const val opplysningspliktigOrgnavn = "POTETKARTELLET AS"

    const val permisjonId = "12345"
    const val permisjonType = "Permisjon med foreldrepenger"
    const val permisjonPeriodeStart = "2022-01-01"
    const val permisjonProsent = "50 %"

    const val permitteringId = "23456"
    const val permitteringType = "Permittering"
    const val permitteringPeriodeStart = "2020-10-30"
    const val permitteringProsent = "40 %"

    const val enkeltArbeidsforhold = """
{
  "id": "A100000000B20000C3000",
  "type": {
    "kode": "ordinaertArbeidsforhold",
    "beskrivelse": "Ordinært arbeidsforhold"
  },
  "arbeidstaker": {
    "identer": [
      {
        "type": "AKTORID",
        "ident": "0001234567890",
        "gjeldende": true
      },
      {
        "type": "FOLKEREGISTERIDENT",
        "ident": "01234567890",
        "gjeldende": true
      }
    ]
  },
  "arbeidssted": {
    "type": "Underenhet",
    "identer": [
      {
        "type": "ORGANISASJONSNUMMER",
        "ident": "111222333"
      }
    ]
  },
  "opplysningspliktig": {
    "type": "Hovedenhet",
    "identer": [
      {
        "type": "ORGANISASJONSNUMMER",
        "ident": "444555666"
      }
    ]
  },
  "ansettelsesperiode": {
    "startdato": "2020-01-01"
  },
  "ansettelsesdetaljer": [
    {
      "type": "Ordinaer",
      "arbeidstidsordning": {
        "kode": "ikkeSkift",
        "beskrivelse": "Ikke skift"
      },
      "ansettelsesform": {
        "kode": "fast",
        "beskrivelse": "Fast ansettelse"
      },
      "yrke": {
        "kode": "9210103",
        "beskrivelse": "POTETPLUKKER"
      },
      "antallTimerPrUke": 37.5,
      "avtaltStillingsprosent": 100,
      "sisteStillingsprosentendring": "2020-01-01",
      "sisteLoennsendring": "2020-01-01",
      "rapporteringsmaaneder": {
        "fra": "2020-12",
        "til": null
      }
    },
    {
      "type": "Ordinaer",
      "arbeidstidsordning": {
        "kode": "ikkeSkift",
        "beskrivelse": "Ikke skift"
      },
      "ansettelsesform": {
        "kode": "fast",
        "beskrivelse": "Fast ansettelse"
      },
      "yrke": {
        "kode": "9210103",
        "beskrivelse": "POTETPLUKKER"
      },
      "antallTimerPrUke": 37.5,
      "avtaltStillingsprosent": 100,
      "sisteStillingsprosentendring": "2020-01-01",
      "sisteLoennsendring": "2020-01-01",
      "rapporteringsmaaneder": {
        "fra": "2020-01-01",
        "til": "2020-12-01"
      }
    }
  ],
  "permisjoner": [
    {
      "id": "12345",
      "type": {
        "kode": "permisjonMedForeldrepenger",
        "beskrivelse": "Permisjon med foreldrepenger"
      },
      "startdato": "2022-01-01",
      "prosent": 50
    }
  ],
  "permitteringer": [
    {
      "id": "23456",
      "type": {
        "kode": "permittering",
        "beskrivelse": "Permittering"
      },
      "startdato": "2020-10-30",
      "prosent": 40
    }
  ],
  "innrapportertEtterAOrdningen": true,
  "rapporteringsordning": {
    "kode": "a-ordningen",
    "beskrivelse": "Rapportert via a-ordningen (2015-d.d.)"
  },
  "navArbeidsforholdId": 11223344,
  "navVersjon": 5,
  "navUuid": "00000000-0000-0000-0000-000000000000",
  "opprettet": "2022-01-01T12:00:00.000",
  "sistBekreftet": "2022-01-01T12:00:00.000",
  "sistEndret": "2022-01-01T12:00:00.000",
  "bruksperiode": {
    "fom": "2022-01-01T12:00:00.000",
    "tom": null
  }
}
"""

    const val alleArbeidsforhold = "[$enkeltArbeidsforhold]"

    const val underenhet = """
{
  "organisasjonsnummer": "111222333",
  "navn": {
    "sammensattnavn": "PETTER POTTIT",
    "navnelinje1": "PETTER POTTIT",
    "bruksperiode": {
      "fom": "2010-01-01T00:00:00.000"
    },
    "gyldighetsperiode": {
      "fom": "2010-01-01"
    }
  },
  "enhetstype": "BEDR",
  "adresse": {
    "type": "Forretningsadresse",
    "adresselinje1": "Sørkedalsveien 450",
    "postnummer": "0758",
    "landkode": "NO",
    "kommunenummer": "0301",
    "bruksperiode": {
      "fom": "2010-01-01T00:00:00.000"
    },
    "gyldighetsperiode": {
      "fom": "2010-01-01"
    }
  },
  "opphoersdato": "2023-01-01"
}
    """

    const val hovedenhet = """
{
  "organisasjonsnummer": "444555666",
  "navn": {
    "sammensattnavn": "POTETKARTELLET AS",
    "navnelinje1": "POTETKARTELLET AS",
    "bruksperiode": {
      "fom": "2010-01-01T00:00:00.000"
    },
    "gyldighetsperiode": {
      "fom": "2010-01-01"
    }
  },
  "enhetstype": "AS",
  "adresse": {
    "type": "Forretningsadresse",
    "adresselinje1": "Sørkedalsveien 450",
    "postnummer": "0758",
    "landkode": "NO",
    "kommunenummer": "0301",
    "bruksperiode": {
      "fom": "2010-01-01T00:00:00.000"
    },
    "gyldighetsperiode": {
      "fom": "2010-01-01"
    }
  },
  "opphoersdato": "2023-01-01"
}
"""
}
