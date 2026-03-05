## Project summary and explanation
- Project implementation relied heavily on Claude Code tools to speed up the development process - entire app architecture, workflow, tools were proposed by developer and described in claude.md to wield Claude as much as possible to streamline the development.
- While most of the code was generated, developer understands 100% of the codebase, its advantaged and downsides and used these tools multiple times before
- The developer mostly worked with Java and Scala, but Kotlin seems to be extremely similar to Scala features and would be able to write everything without AI
- Since the team uses primarily this lang, project was written in this language, though the primary framework was chosen Spring Boot instead of Ktor/other more common Kotlin frameworks, to simplify development process

## How to run the application:
1) Docker-composer - the simplest option

Prerequisites:
- Install Postman
- Install Docker Desktop and run it

Steps:
- Type in the console `docker compose up -d` to run entire docker-compose that contains the app and all dependencies.
- Import postman collection from ./postman folder into your Postman and send the request

2) Kubernetes + Helm

Prerequisites:
- Install Postman
- Install kubectl
- Install Docker Desktop, run it and run Kubernetes cluster on the left panel

Steps:
- Type in the console `docker build -t temperature-proxy-api:latest .` to create docker image
- Type in the console `helm upgrade my-release helm/temperature-proxy-api` to create K8S cluster using Helm which simplifies deployment
- Give it a minute and then type the following commands to verify the setup is running correctly:
  ==> `kubectl get pods -n default`    <== contains our Application pods
  ==> `kubectl get pods -n monitoring` <== contains our Prometheus and Grafana observability
  You should see similar output:
```
kubectl get pods -n default
NAME                                                READY   STATUS    RESTARTS   AGE
my-release-temperature-proxy-api-7d89776f9c-2t8qk   1/1     Running   0          24m
my-release-temperature-proxy-api-7d89776f9c-r56sd   1/1     Running   0          24m

kubectl get pods -n monitoring
NAME                          READY   STATUS    RESTARTS   AGE
prometheus-5fffb4fc59-hktv4   1/1     Running   0          30m
```
- Type in the console `kubectl port-forward service/my-release-temperature-proxy-api 8080:80` to forward your local traffic on 8080 in K8S private cluster
- Import postman collection from ./postman folder into your Postman and send the requests

Addressed
- http://localhost:8080 - App
- http://localhost:9090 - Prometheus
- http://localhost:3000 - Grafana

## Test results

Docker-compose seems to deployment app and dependent resources correctly

Helm deployment K8S resources correctly as well

API was hit and return the appropriate response

The application exposes Actuator as expected including:
- the health check, which is required by Kubernetes to create a gree deployment
- default Prometheus metrics


## What could be improved
- [Development]   -> Assuming that our traffic will increase to millions of requests - we must replace Caffeine cache with Redis to use distributed cache
- [Observability] -> Add business metrics for Prometheus and build Grafana charts
- [Observability] -> Add more observability - ELK, Tracing(Jaeger), Extended error monitoring(Sentry)
- [DevOps] -> With increasing number of requests and importance of our App, it's recommended to change our K8S deployment strategy to more advanced ones(canary/blue green/etc)
- [DevOps] -> Application be deployed in AWS/Azure/GCP in a K8S service <- cloud resources can be created manually, but ideally we should use terraform


