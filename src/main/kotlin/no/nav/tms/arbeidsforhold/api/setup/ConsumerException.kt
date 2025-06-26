package no.nav.tms.arbeidsforhold.api.setup

import io.ktor.client.statement.*

class ConsumerException(
    val externalService: String,
    val endpoint: String,
    val status: Int,
    val responseContent: String
): RuntimeException() {
    companion object {
        suspend fun fromResponse(externalService: String, response: HttpResponse) = ConsumerException(
            externalService = externalService,
            endpoint = response.request.url.toString(),
            status = response.status.value,
            responseContent = response.bodyAsText(),
        )
    }
}
