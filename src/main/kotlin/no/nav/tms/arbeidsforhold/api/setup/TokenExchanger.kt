package no.nav.tms.arbeidsforhold.api.setup;

import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class TokenExchanger(
    private val tokendingsService: TokendingsService,
    private val aaregServicesClientId: String,
) {
    suspend fun aaregServicesToken(accessToken: String): String {
        return tokendingsService.exchangeToken(accessToken, aaregServicesClientId)
    }
}
