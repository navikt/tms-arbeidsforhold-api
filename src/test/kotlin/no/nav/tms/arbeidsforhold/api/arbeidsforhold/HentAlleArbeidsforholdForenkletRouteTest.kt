package no.nav.tms.arbeidsforhold.api.arbeidsforhold

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.tms.arbeidsforhold.api.InternalRouteConfig
import no.nav.tms.arbeidsforhold.api.RouteTest
import no.nav.tms.arbeidsforhold.api.routeConfig
import no.nav.tms.arbeidsforhold.api.setup.HeaderHelper
import no.nav.tms.arbeidsforhold.api.setup.TokenExchanger
import org.junit.jupiter.api.Test

class HentAlleArbeidsforholdForenkletRouteTest : RouteTest() {

    private val eregServicesUrl = "http://ereg-services"
    private val aaregServicesUrl = "http://aareg-services"
    private val aaregServicesToken = "<aareg-token>"

    private val tokenExchanger: TokenExchanger = mockk<TokenExchanger>().also {
        coEvery { it.aaregServicesToken(any()) } returns aaregServicesToken
    }

    private val internalRouteConfig: InternalRouteConfig = { client ->
        routeConfig {
            arbeidsforholdRoutes(
                ArbeidsforholdService(
                    AaregServicesConsumer(client, aaregServicesUrl, tokenExchanger),
                    EregServicesConsumer(client, eregServicesUrl),
                )
            )
        }
    }

    private val hentAlleForholdPath = "/arbeidsforhold/forenklet/alle"

    @Test
    fun `henter alle arbeidsforhold fra aareg-services og orgnavn fra ereg-services`() = apiTest(internalRouteConfig) {
        externalService(aaregServicesUrl) {
            get("/api/v2/arbeidstaker/arbeidsforhold") {
                call.respondJson(TestData.alleArbeidsforhold)
            }
        }

        externalService(eregServicesUrl) {
            get("/v2/organisasjon/{orgnr}/noekkelinfo") {
                when (call.pathParameters["orgnr"]) {
                    TestData.arbeidsgiverOrgnr -> call.respondJson(TestData.underenhet)
                    TestData.opplysningspliktigOrgnr -> call.respondJson(TestData.hovedenhet)
                    else -> call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        val response = client.get(hentAlleForholdPath)

        response.status shouldBe HttpStatusCode.OK

        val jsonResponse = response.json()
            .shouldNotBeEmpty()
            .first()

        jsonResponse.let {
            it["navArbeidsforholdId"].asText() shouldBe TestData.navArbeidsforholdId
            it["eksternArbeidsforholdId"].asText() shouldBe TestData.eksternArbeidsforholdId
            it["yrke"].asText() shouldBe TestData.yrke

            it["arbeidsgiver"]["orgnr"].asText() shouldBe TestData.arbeidsgiverOrgnr
            it["arbeidsgiver"]["fnr"].asTextOrNull() shouldBe null
            it["arbeidsgiver"]["type"].asText() shouldBe TestData.arbeidsgiverType
            it["arbeidsgiver"]["orgnavn"].asText() shouldBe TestData.arbeidsgiverOrgnavn

            it["opplysningspliktigarbeidsgiver"]["orgnr"].asText() shouldBe TestData.opplysningspliktigOrgnr
            it["opplysningspliktigarbeidsgiver"]["fnr"].asTextOrNull() shouldBe null
            it["opplysningspliktigarbeidsgiver"]["type"].asText() shouldBe TestData.opplysningspliktigType
            it["opplysningspliktigarbeidsgiver"]["orgnavn"].asText() shouldBe TestData.opplysningspliktigOrgnavn

            it["ansettelsesperiode"]["periode"]["periodeFra"].asText() shouldBe TestData.ansettelsesperiodeStart
            it["ansettelsesperiode"]["periode"]["periodeTil"].asTextOrNull() shouldBe null
            it["ansettelsesperiode"]["varslingskode"].asTextOrNull() shouldBe null
            it["ansettelsesperiode"]["sluttaarsak"].asTextOrNull() shouldBe null

            it["utenlandsopphold"].isEmpty shouldBe true

            it["permisjonPermittering"][0].let { permisjon ->
                permisjon["id"].shouldBeNull()
                permisjon["type"].asText() shouldBe  "Permisjon med foreldrepenger"
                permisjon["periode"]["periodeFra"].asText() shouldBe "2022-01-01"
                permisjon["periode"]["periodeTil"].asTextOrNull() shouldBe null
                permisjon["prosent"].asText() shouldBe  "50 %"
            }

            it["permisjonPermittering"][1].let { permittering ->
                permittering["id"].shouldBeNull()
                permittering["type"].asText() shouldBe "Permittering"
                permittering["periode"]["periodeFra"].asText() shouldBe "2020-10-30"
                permittering["periode"]["periodeTil"].asTextOrNull() shouldBe null
                permittering["prosent"].asText() shouldBe "40 %"
            }
        }
    }

    @Test
    fun `bruker riktige headers mot aareg-services`() = apiTest(internalRouteConfig) {
        var aaregHeaders: Headers? = null

        externalService(aaregServicesUrl) {
            get("/api/v2/arbeidstaker/arbeidsforhold") {
                aaregHeaders = call.request.headers
                call.respondJson(TestData.alleArbeidsforhold)
            }
        }

        externalService(eregServicesUrl) {
            get("/v2/organisasjon/{orgnr}/noekkelinfo") {
                when (call.pathParameters["orgnr"]) {
                    TestData.arbeidsgiverOrgnr -> call.respondJson(TestData.underenhet)
                    TestData.opplysningspliktigOrgnr -> call.respondJson(TestData.hovedenhet)
                    else -> call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        client.get(hentAlleForholdPath)

        aaregHeaders.shouldNotBeNull().let {
            it[HeaderHelper.CALL_ID_HEADER].shouldNotBeNull()
            it[HeaderHelper.NAV_CONSUMER_ID_HEADER] shouldBe HeaderHelper.NAV_CONSUMER_ID
            it[HeaderHelper.NAV_PERSONIDENT_HEADER] shouldBe testIdent
        }
    }

    @Test
    fun `bruker riktige headers mot ereg-services`() = apiTest(internalRouteConfig) {
        externalService(aaregServicesUrl) {
            get("/api/v2/arbeidstaker/arbeidsforhold") {
                call.respondJson(TestData.alleArbeidsforhold)
            }
        }

        var eregHeaders: Headers? = null

        externalService(eregServicesUrl) {
            get("/v2/organisasjon/{orgnr}/noekkelinfo") {
                eregHeaders = call.request.headers
                when (call.pathParameters["orgnr"]) {
                    TestData.arbeidsgiverOrgnr -> call.respondJson(TestData.underenhet)
                    TestData.opplysningspliktigOrgnr -> call.respondJson(TestData.hovedenhet)
                    else -> call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        client.get(hentAlleForholdPath)

        eregHeaders.shouldNotBeNull().let {
            it[HeaderHelper.CALL_ID_HEADER].shouldNotBeNull()
            it[HeaderHelper.NAV_CONSUMER_ID_HEADER] shouldBe HeaderHelper.NAV_CONSUMER_ID
        }
    }

    @Test
    fun `svarer med InternalServiceError dersom aareg-services er nede`() = apiTest(internalRouteConfig) {
        externalService(aaregServicesUrl) {
            get("/api/v2/arbeidstaker/arbeidsforhold") {
                call.respond(HttpStatusCode.ServiceUnavailable)
            }
        }

        externalService(eregServicesUrl) {
            get("/v2/organisasjon/{orgnr}/noekkelinfo") {
                when (call.pathParameters["orgnr"]) {
                    TestData.arbeidsgiverOrgnr -> call.respondJson(TestData.underenhet)
                    TestData.opplysningspliktigOrgnr -> call.respondJson(TestData.hovedenhet)
                    else -> call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        val response = client.get(hentAlleForholdPath)

        response.status shouldBe HttpStatusCode.InternalServerError
    }

    @Test
    fun `bruker orgnr som fallback dersom aareg-services er nede`() = apiTest(internalRouteConfig) {
        externalService(aaregServicesUrl) {
            get("/api/v2/arbeidstaker/arbeidsforhold") {
                call.respondJson(TestData.alleArbeidsforhold)
            }
        }

        externalService(eregServicesUrl) {
            get("/v2/organisasjon/{orgnr}/noekkelinfo") {
                call.respond(HttpStatusCode.ServiceUnavailable)
            }
        }

        val response = client.get(hentAlleForholdPath)

        response.status shouldBe HttpStatusCode.OK

        val jsonResponse = response.json()
            .shouldNotBeEmpty()
            .first()

        jsonResponse.let {

            it["arbeidsgiver"]["orgnr"].asText() shouldBe TestData.arbeidsgiverOrgnr
            it["arbeidsgiver"]["orgnavn"].asText() shouldNotBe TestData.arbeidsgiverOrgnavn
            it["arbeidsgiver"]["orgnavn"].asText() shouldBe TestData.arbeidsgiverOrgnr

            it["opplysningspliktigarbeidsgiver"]["orgnr"].asText() shouldBe TestData.opplysningspliktigOrgnr
            it["opplysningspliktigarbeidsgiver"]["orgnavn"].asText() shouldNotBe TestData.opplysningspliktigOrgnavn
            it["opplysningspliktigarbeidsgiver"]["orgnavn"].asText() shouldBe TestData.opplysningspliktigOrgnr
        }
    }
}
