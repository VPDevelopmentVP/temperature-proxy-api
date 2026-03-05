Temperature Proxy API

## Current task:
A small REST API that fetches current temperature from Open-Meteo and returns a normalized response. Propose endpoint structure.
Integration
Call Open-Meteo Forecast API:
- Base: https://api.open-meteo.com/v1/forecast
- Use the current parameter to fetch current conditions (Open-Meteo supports current variables). (Open Meteo - https://open-meteo.com/en/docs)
- Example upstream call (this returns real current values):
curl "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"
(Open-Meteo documents /v1/forecast and the current parameter for current conditions.) (Open Meteo)
Your API response shape
```
    {
       "location": { "lat": 52.52, "lon": 13.41 },
       "current": {
          "temperatureC": 1.2,
          "windSpeedKmh": 9.7
       },
       "source": "open-meteo",
       "retrievedAt": "2026-01-11T10:12:54Z"
    }
```
Explicit requirements
- Timeout to the upstream (e.g., 1s)
- Cache by (lat, lon) for 60 seconds
- Besides that project should contain all important bits you can think the production code should have
- Bonus points
- k8s containerization
- health checks, resource limits, configuration (timeouts, cache TTLs), scaling


## Tool choice:
The actual code:
- Kotlin 2.3.0
- Spring Boot 4.0.0
- Caffeine
Testing: 
- Groovy 4.0.30
- Spock 2.4-groovy-5.0
- Rest Assured
- ArchUnit
- Pitest
Observability:
- Prometheus
- Grafana
Documentation:
- Swagger

## Architecture:
Project follows Hexagonal architecture to separate layers and their responsibilities and consists of 3 layers:
- application - layer that handles incoming traffic. It includes REST/gRPC/GraphQL endpoints, streaming components, and queue consumers.
It's also a place where requests are mapped into domain objects and then into responses.
- domain - layer of business logic. It includes domain DTOs, services, facades, etc. Framework-free layer (except DI).
- infra - layer that handles external communication. It includes communication with databases, streaming producers, queue publishers, and clients that interact with external services.
It's also a place where domain DTOs are mapped into client requests or streaming/queue messages and then back into domain DTOs.

Rules:
1) Application can access only domain structs/services; it cannot communicate with the infra layer.
2) Domain is the central layer; it can be accessed by the application, and it communicates with infra.
3) Infra can access only domain structs/services; it cannot communicate with the application layer.
4) Communication between layers looks like: application <-> domain <-> infra.
5) Application cannot directly communicate with infra, and domain cannot directly communicate with infra implementations — they must use interfaces and implementations (Ports and Adapters) as binders.
Additionally:
ArchiUnit is used to enforce rules of hexagonal architecture to ensure a strict but flexible project structure.
Pitest is a mutation testing framework that is used to enhance test coverage quality.

## Codebase Quality:
- Codebase is expected to follow hexagonal architecture described above.
- KISS, DRY, and SOLID principles are followed as well.
- The codebase uses the latest features of Kotlin, Groovy, Spring Boot, and Spock where appropriate.
