# Home Energy Tracker

Plateforme de suivi de la consommation énergétique d'appareils connectés d'une maison, construite en **microservices Spring Boot**. Les relevés des appareils arrivent en continu par **Kafka**, sont stockés dans une base de séries temporelles (**InfluxDB**), agrégés par utilisateur, et déclenchent une **alerte e-mail** quand un seuil est dépassé. Un service d'**IA locale (Ollama)** analyse la consommation et propose des conseils d'économie.

## Fonctionnalités

- Gestion des utilisateurs (seuil d'alerte personnel, alertes activables) et de leurs appareils (enceinte, caméra, thermostat, lumière, serrure, sonnette).
- Ingestion des relevés de consommation par API, publiés sur Kafka.
- Simulateur de charge intégré : 1 000 relevés toutes les 5 secondes, répartis sur 10 threads.
- Stockage des relevés dans InfluxDB et agrégation de la dernière heure par appareil puis par utilisateur, toutes les 10 secondes.
- Alerte e-mail quand la consommation d'un utilisateur dépasse son seuil.
- Conseils d'économie et synthèse de consommation générés par un LLM local.
- Point d'entrée unique (API Gateway) sécurisé par JWT avec Keycloak, avec circuit breaker (Resilience4j) et documentation Swagger.
- Supervision : métriques Prometheus et tableau de bord Grafana provisionné automatiquement.

## Architecture

```text
                         Keycloak (JWT)
                               |
Client ──> API Gateway :9000 ──┼──> user-service :8080 ──────┐
                               ├──> device-service :8081 ────┼──> MySQL
                               ├──> ingestion-service :8082  │
                               └──> insight-service :8085    │
                                         │                   │
ingestion-service ──(topic energy-usage)──> Kafka            │
                                             │               │
                                     usage-service :8083 ────┘ (lit appareils et utilisateurs)
                                       │  ├──> InfluxDB (relevés)
                                       │  └──(topic energy-alerts)──> Kafka
                                       │                               │
insight-service ──> usage-service      │                    alert-service :8084 ──> e-mail (Mailpit)
       └──> Ollama (LLM local)
```

### Parcours d'un relevé

1. Le simulateur (ou un appareil) envoie un relevé à `ingestion-service`, qui le publie sur le topic Kafka `energy-usage`.
2. `usage-service` consomme le topic et écrit le point dans InfluxDB.
3. Toutes les 10 secondes, `usage-service` additionne la consommation de la dernière heure par appareil, retrouve le propriétaire via `device-service`, puis compare le total au seuil récupéré dans `user-service`.
4. En cas de dépassement, un événement part sur le topic `energy-alerts`, et `alert-service` envoie l'e-mail.

## Services

| Service | Port | Rôle |
| --- | ---: | --- |
| `api-gateway` | 9000 | Routage, validation JWT, circuit breaker, Swagger agrégé |
| `user-service` | 8080 | Utilisateurs, seuils d'alerte, migrations Flyway de la base |
| `device-service` | 8081 | CRUD des appareils, appareils d'un utilisateur |
| `ingestion-service` | 8082 | Réception des relevés, publication Kafka, simulateur |
| `usage-service` | 8083 | Stockage InfluxDB, agrégation, détection des dépassements |
| `alert-service` | 8084 | Consommation des alertes, envoi des e-mails |
| `insight-service` | 8085 | Conseils et synthèse via Ollama (Spring AI) |

Infrastructure (Docker Compose) :

| Composant | Port hôte | Usage |
| --- | ---: | --- |
| MySQL | 3307 | Base `home_energy_tracker` |
| Kafka | 9094 | Broker (9092 en interne) |
| Kafka UI | 8070 | Visualiser topics et messages |
| InfluxDB | 8072 | Séries temporelles de consommation |
| Mailpit | 8025 (web), 1025 (SMTP) | Boîte mail de test |
| Keycloak | 8091 | Authentification, realm `het-security-realm` |
| Prometheus | 9090 | Collecte des métriques |
| Grafana | 3000 | Tableau de bord « Home Energy Tracker - Overview » |

## Stack technique

Java 21, Spring Boot, Spring Cloud Gateway (MVC), Spring Data JPA, Flyway, Spring Kafka, Spring AI (Ollama), Spring Security OAuth2 Resource Server, Resilience4j, Springdoc OpenAPI, MySQL, InfluxDB 2, Apache Kafka, Keycloak, Prometheus, Grafana, Docker Compose.

## Endpoints principaux

Via la gateway (`http://localhost:9000`, jeton JWT requis) :

| Méthode | Route | Description |
| --- | --- | --- |
| `POST` | `/api/v1/user` | Créer un utilisateur |
| `GET` / `PUT` / `DELETE` | `/api/v1/user/{id}` | Lire, modifier, supprimer un utilisateur |
| `POST` | `/api/v1/device/create` | Créer un appareil |
| `GET` | `/api/v1/device/{id}` | Lire un appareil |
| `PUT` | `/api/v1/device/updated/{id}` | Modifier un appareil |
| `DELETE` | `/api/v1/device/delete/{id}` | Supprimer un appareil |
| `GET` | `/api/v1/device/user/{userId}` | Appareils d'un utilisateur |
| `POST` | `/api/v1/ingestion` | Envoyer un relevé de consommation |
| `GET` | `/api/v1/insight/saving-tips/{userId}` | Conseils d'économie (3 derniers jours) |
| `GET` | `/api/v1/insight/overview/{userId}` | Synthèse de consommation par appareil |

Directement sur `usage-service` : `GET http://localhost:8083/api/v1/usage/{userId}?days=3`.

Swagger : `http://localhost:9000/swagger-ui.html`.

## Lancement en local

### Prérequis

- Java 21
- Docker et Docker Compose
- [Ollama](https://ollama.com/) avec le modèle utilisé par `insight-service` : `ollama pull deepseek-coder`

### 1. Démarrer l'infrastructure

```bash
docker compose up -d
```

Au premier démarrage, InfluxDB crée automatiquement l'organisation `zakariadev`, le bucket `usage-bucket` et le jeton attendu par `usage-service`.

### 2. Configurer Keycloak

Ouvrir `http://localhost:8091` (admin / admin), créer le realm `het-security-realm` et un client pour obtenir des jetons. Le dossier `docker/keycloak/realms/` peut accueillir un export de realm, qui sera importé automatiquement au démarrage.

### 3. Lancer les microservices

Dans chaque dossier de service :

```bash
./mvnw spring-boot:run
```

Ordre conseillé : `user-service` (crée les tables), `device-service`, `usage-service`, `ingestion-service`, `alert-service`, `insight-service`, puis `api-gateway`.

### 4. Observer

- Messages Kafka : `http://localhost:8070`
- E-mails d'alerte : `http://localhost:8025`
- Tableau de bord : `http://localhost:3000`

---

*Projet inspiré de la problématique présentée par LeetJourney.*
