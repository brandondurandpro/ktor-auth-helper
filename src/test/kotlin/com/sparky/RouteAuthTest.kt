package com.sparky

import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.test.Test
import kotlin.test.assertEquals


class RouthAuthTest {
    @Test
    fun testRoot() = testApplication {
        val errorMessage =  "Invalid or missing token"
        val successMessage = "Hello world ! "
        application {
            install(Authentication) {
                routeAuth("custom-auth") {
                    validate {
                        val token = request.headers["Authorization"]
                        if (token == "valid-token") {
                            UserIdPrincipal("authorized-user")
                        } else {
                            null
                        }
                    }
                    challenge {
                        request.call.respond(HttpStatusCode.Unauthorized, errorMessage)
                    }
                }
            }
            routing {
                authenticate("custom-auth") {
                    get("/") {
                        call.respondText(successMessage)
                    }
                }
            }
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals(errorMessage, bodyAsText())
        }

        client.get("/"){
            header(HttpHeaders.Authorization, "valid-token")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(successMessage, bodyAsText())
        }
    }

}
