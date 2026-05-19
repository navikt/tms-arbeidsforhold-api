package no.nav.tms.arbeidsforhold.api.setup;

import no.nav.tms.token.support.user.token.exchange.UserTokenExchanger

class TokenExchanger(
    private val tokenExchanger: UserTokenExchanger,
    private val aaregServicesClientId: String,
) {
    suspend fun aaregServicesToken(accessToken: String): String {
        return tokenExchanger.exchangeToken(accessToken, aaregServicesClientId)
    }
}
