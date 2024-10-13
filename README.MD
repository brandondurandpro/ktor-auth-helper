# Plugin d'Authentification Personnalisé pour Ktor

Ce plugin étend les fonctionnalités d'authentification du framework Ktor en permettant l'implémentation d'une logique métier personnalisée pour l'authentification.

## Fonctionnalités

- Intégration transparente avec le plugin d'authentification de Ktor
- Personnalisation de la logique métier d'authentification des routes
- Gestion personnalisée des réponses en cas d'échec d'authentification

## Installation

Ajoutez la dépendance suivante à votre fichier `build.gradle.kts` :

```kotlin
implementation("com.votre.package:ktor-custom-auth:1.0.0")
```

## Utilisation

Voici un exemple d'utilisation du plugin dans votre application Ktor :

```kotlin
fun Application.module() {
    install(Authentication) {
        routeAuth("custom-auth") {
            validate { call: ApplicationCall ->
                val token = call.request.headers["Authorization"]
                if (token == "valid-token") {
                    UserIdPrincipal("authorized-user")
                } else {
                    null
                }
            }
            challenge { call: ApplicationCall ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid or missing token")
            }
        }
    }
    
    routing {
        authenticate("custom-auth") {
            get("/") {
                call.respondText("Hello world!")
            }
        }
    }
}
```

## Configuration

### `validate`

La fonction `validate` est utilisée pour définir la logique de validation de l'authentification. Elle doit retourner un `Principal` si l'authentification réussit, ou `null` si elle échoue.

### `challenge`

La fonction `challenge` est optionnelle et permet de personnaliser la réponse en cas d'échec d'authentification. Si elle n'est pas définie, le message par défaut "Authentication Failed" sera utilisé.


## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir une issue ou à soumettre une pull request.

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.