package no.nav.tms.arbeidsforhold.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.utils.io.*
import no.nav.tms.token.support.user.token.verification.Issuer
import no.nav.tms.token.support.user.token.verification.LevelOfAssurance
import no.nav.tms.token.support.user.token.verificaton.mock.userTokenMock
import java.text.DateFormat
import java.time.LocalDateTime

abstract class RouteTest {

    companion object {
        const val testIdent = "01234567890"
    }

    private val objectMapper = jacksonObjectMapper()

    fun apiTest(
        arbeidsforholdRouteConfig: (HttpClient) -> (Route.() -> Unit) = { routeConfig {  } },
        legacyRouteConfig: (HttpClient) -> (Route.() -> Unit) = { routeConfig {  } },
        userIdent: String = testIdent,
        userLoa: LevelOfAssurance = LevelOfAssurance.High,
        corsAllowedOrigins: String = "*",
        corsAllowedSchemes: String = "http",
        block: suspend ApplicationTestBuilder.(HttpClient) -> Unit
    ) = testApplication {

        val serverClient = client.config {
            install(ContentNegotiation) {
                jackson {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    registerModule(JavaTimeModule())
                    dateFormat = DateFormat.getDateTimeInstance()
                }
            }
        }

        application {
            mainModule(
                arbeidsforholdRouteConfig(serverClient),
                legacyRouteConfig(serverClient),
                serverClient,
                corsAllowedOrigins = corsAllowedOrigins,
                corsAllowedSchemes = corsAllowedSchemes,
                authInstaller = {
                    authentication {
                        userTokenMock {
                            enableDefaultAuthentication {
                                tokenIssuer = Issuer.IdPorten
                                tokenIdent = userIdent
                                tokenLoa = userLoa
                            }
                        }
                        userTokenMock(SYSTEM_FACING_API) {
                            configureIssuers(Issuer.Tokenx)
                            enableDefaultAuthentication {
                                tokenIdent = userIdent
                                tokenLoa = userLoa
                            }
                        }
                    }
                },
            )
        }

        this.block(
            client.config {
                install(ContentNegotiation) {
                    jackson {
                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        registerModule(JavaTimeModule())
                        dateFormat = DateFormat.getDateTimeInstance()
                    }
                }
            }
        )
    }

    fun ApplicationTestBuilder.externalService(host: String, route: Route.() -> Unit) {
        externalServices {
            hosts(host) {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    jackson {
                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        registerModule(JavaTimeModule())
                        dateFormat = DateFormat.getDateTimeInstance()
                    }
                }
                routing(route)
            }
        }
    }

    suspend fun ApplicationCall.respondJson(text: String) = respondText(text, contentType = ContentType.Application.Json)

    suspend fun HttpResponse.json() = bodyAsText().let { objectMapper.readTree(it) }

    fun JsonNode.asTextOrNull() = if(isNull) null else asText()
    fun JsonNode.asLocalDateTime() = LocalDateTime.parse(asText())
}

typealias InternalRouteConfig = (HttpClient) -> (Route.() -> Unit)

fun routeConfig(block: Route.() -> Unit) = block
