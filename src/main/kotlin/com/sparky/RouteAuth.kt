package com.sparky

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

private val tokenAuthenticationChallengeKey: String = "RouteAuth"
typealias RouthAuthValidationFunction = suspend ApplicationCall.() -> UserIdPrincipal?
typealias RouthAuthChallengeFunction = suspend ApplicationCall.() -> Unit

class RouthAuthProvider(config: Config) : AuthenticationProvider(config) {
    private val authenticationFunction = config.authenticationFunction
    private val challengeFunction = config.challengeFunction

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call

        val principal = authenticationFunction(call)
        if (principal == null) {
            context.challenge(tokenAuthenticationChallengeKey, AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
                challengeFunction(call)
                challenge.complete()
            }
        } else {
            context.principal(principal)
        }
    }

    class Config(name: String) : AuthenticationProvider.Config(name) {
        internal var authenticationFunction: RouthAuthValidationFunction = {
            throw NotImplementedError("Validation function is not specified. Use tokenAuth { validate { ... } } to fix.")
        }

        internal var challengeFunction: RouthAuthChallengeFunction = {
            request.call.respond(HttpStatusCode.Unauthorized, "Authorization Failed")

        }

        fun validate(body: RouthAuthValidationFunction) {
            authenticationFunction = body
        }

        fun challenge(body: RouthAuthChallengeFunction) {
            challengeFunction = body
        }
    }
}


fun AuthenticationConfig.routeAuth(
    name: String,
    configure: RouthAuthProvider.Config.() -> Unit
) {
    val config = RouthAuthProvider.Config(name).apply(configure)
    val provider = RouthAuthProvider(config)
    register(provider)
}
