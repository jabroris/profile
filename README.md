# Ivaylo Blagoev – Portfolio Application

A **Kotlin / Spring Boot** portfolio application that demonstrates enterprise-grade backend
engineering skills: REST APIs, Apache Kafka, Spring Data JPA, Spring Security, containerisation,
GKE deployment, and a full CI/CD pipeline.

🔗 **Live:** https://portfolio.example.com  
📖 **API Docs:** https://portfolio.example.com/swagger-ui.html

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     GitHub Actions                      │
│  CI (build·test·lint·OWASP·CodeQL·Trivy)               │
│  CD (Docker push → GKE staging → approval → GKE prod)  │
└────────────────────────┬────────────────────────────────┘
                         │
          ┌──────────────▼──────────────┐
          │    Artifact Registry        │
          │  (Docker images)            │
          └──────────────┬──────────────┘
                         │
          ┌──────────────▼──────────────────────────────┐
          │            GKE Cluster (us-central1)        │
          │                                             │
          │  ┌─────────────────────────────────────┐   │
          │  │  Namespace: portfolio-production     │   │
          │  │                                     │   │
          │  │  Deployment (3 replicas)  ──────────┼──►│── Cloud Load Balancer
          │  │  ├── portfolio-app container        │   │
          │  │  └── cloud-sql-proxy sidecar        │   │
          │  │                                     │   │
          │  │  HPA (2–10 pods, CPU/mem targets)   │   │
          │  │  PodDisruptionBudget (min 1 alive)  │   │
          │  └─────────────────────────────────────┘   │
          │                                             │
          │  ┌─────────────────────────────────────┐   │
          │  │  Namespace: kafka                   │   │
          │  │  Strimzi Kafka (3 brokers + ZK)     │   │
          │  └─────────────────────────────────────┘   │
          └──────────┬────────────────────┬─────────────┘
                     │                    │
          ┌──────────▼──────┐  ┌──────────▼──────┐
          │  Cloud SQL      │  │  Secret Manager  │
          │  (PostgreSQL 15)│  │  (db-password)   │
          └─────────────────┘  └──────────────────┘
```

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.0 |
| Framework | Spring Boot 3.3 |
| Messaging | Apache Kafka (Strimzi on GKE) |
| Database | H2 (dev) / PostgreSQL 15 (prod) |
| Security | Spring Security 6 (BCrypt, HTTP Basic) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Gradle 9, JaCoCo, ktlint, OWASP |
| Container | Docker (eclipse-temurin:17-jre-alpine) |
| Orchestration | GKE (Kubernetes 1.29) + Kustomize |
| IaC | Terraform 1.7 |
| CI/CD | GitHub Actions |
| Registry | GCP Artifact Registry |
| Auth (GHA→GCP) | Workload Identity Federation (keyless) |

---

## 🖥️ Local Development

### Prerequisites
- JDK 17+
- Docker & Docker Compose

### Run without Kafka (H2 only)
```bash
./gradlew bootRun
# Visit: http://localhost:8080
```

### Run with Kafka
```bash
docker-compose up -d          # starts Zookeeper + Kafka + Kafka-UI
./gradlew bootRun
# Kafka UI: http://localhost:9080
```

### Useful endpoints (local)

| URL | Description |
|-----|-------------|
| http://localhost:8080/ | Resume page + live demo |
| http://localhost:8080/swagger-ui.html | API documentation |
| http://localhost:8080/h2-console | H2 DB (jdbc:h2:mem:portfoliodb, sa/password) |
| http://localhost:8080/actuator/health | Health check |
| http://localhost:8080/api/experience | Work history (JPA) |
| http://localhost:8080/api/skills | Technical skills |
| http://localhost:8080/api/events/publish | Publish Kafka event |

---

## 🧪 Quality Gates

```bash
./gradlew test                    # Unit + integration tests
./gradlew jacocoTestReport        # Coverage report → build/reports/jacoco/
./gradlew ktlintCheck             # Kotlin lint
./gradlew dependencyCheckAnalyze  # OWASP CVE scan
```

---

## 🐳 Docker

```bash
# Build image locally
docker build -t portfolio-app:local .

# Run with production profile (needs PostgreSQL)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=staging \
  -e DB_PASSWORD=your-password \
  portfolio-app:local
```

---

## ☁️ GCP Setup

### 1. Provision Infrastructure with Terraform

```bash
cd terraform/

# Create the Terraform state bucket (one-time)
gsutil mb -p profile-123456789 gs://profile-123456789-tf-state

# Initialise
terraform init

# Plan (supply your DB password)
terraform plan -var="db_password=your-secure-password"

# Apply
terraform apply -var="db_password=your-secure-password"
```

### 2. Install Strimzi Kafka Operator on GKE

```bash
kubectl create namespace kafka
kubectl apply -f https://strimzi.io/install/latest?namespace=kafka -n kafka
kubectl apply -f k8s/kafka/kafka-cluster.yaml
kubectl wait kafka/portfolio-kafka --for=condition=Ready --timeout=5m -n kafka
```

### 3. Configure GitHub Actions Secrets

Go to **Settings → Secrets and variables → Actions** and add:

| Secret | Description |
|--------|-------------|
| `GCP_WORKLOAD_IDENTITY_PROVIDER` | `projects/<PROJECT_NUM>/locations/global/workloadIdentityPools/github-pool/providers/github` |
| `DB_PASSWORD` | Staging Cloud SQL password |
| `DB_PASSWORD_PROD` | Production Cloud SQL password |
| `NVD_API_KEY` | (Optional) NIST NVD API key to speed up OWASP scans |

### 4. Set up Workload Identity Federation (keyless GCP auth)

```bash
# Create the WIF pool and provider (run once)
gcloud iam workload-identity-pools create "github-pool" \
  --project="profile-123456789" \
  --location="global" \
  --display-name="GitHub Actions Pool"

gcloud iam workload-identity-pools providers create-oidc "github" \
  --project="profile-123456789" \
  --location="global" \
  --workload-identity-pool="github-pool" \
  --display-name="GitHub OIDC" \
  --attribute-mapping="google.subject=assertion.sub,attribute.repository=assertion.repository" \
  --issuer-uri="https://token.actions.githubusercontent.com" \
  --attribute-condition="assertion.repository=='jabroris/portfolio'"

gcloud iam service-accounts add-iam-policy-binding \
  portfolio-cicd@profile-123456789.iam.gserviceaccount.com \
  --project="profile-123456789" \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/projects/<PROJECT_NUM>/locations/global/workloadIdentityPools/github-pool/attribute.repository/jabroris/portfolio"
```

### 5. Configure GitHub Environments

In **Settings → Environments** create:
- `staging` — no approval required
- `production` — **required reviewer: @jabroris**, deployment protection rules enabled

---

## 🔄 CI/CD Pipeline Flow

```
Feature branch push
       │
       ▼
   CI Workflow ──────────────────────────────────────────────────────►
   ├─ Build & Test (JUnit, JaCoCo ≥ 60%)
   ├─ Lint (ktlint)
   ├─ OWASP Dependency Check (fail on CVSS ≥ 7)
   ├─ CodeQL SAST
   └─ Docker Build + Trivy scan (fail on CRITICAL/HIGH CVEs)

Merge to main
       │
       ▼
   CD Workflow
   ├─ Build JAR → Docker image
   ├─ Push to Artifact Registry (tagged with git SHA)
   ├─ Deploy to portfolio-staging namespace
   ├─ Wait for rollout
   └─ Smoke test /actuator/health

Create GitHub Release (tag v*.*.*)
       │
       ▼
   CD Workflow (production gate)
   ├─ ⏸ Wait for @jabroris approval (GitHub Environment)
   ├─ Deploy to portfolio-production namespace
   ├─ Wait for rollout
   ├─ Production health check
   └─ Auto-rollback on failure (kubectl rollout undo)
```

---

## 📁 Project Structure

```
.
├── src/
│   ├── main/kotlin/com/ivo/profile/
│   │   ├── config/          SecurityConfig, KafkaConfig
│   │   ├── controller/      REST + Thymeleaf controllers
│   │   ├── domain/          Entities, DTOs
│   │   ├── kafka/           EventProducer, EventConsumer
│   │   ├── repository/      Spring Data JPA repos
│   │   └── service/         Business logic + DataInitializer
│   └── main/resources/
│       ├── application.yml              (dev/H2)
│       ├── application-staging.yml      (PostgreSQL, lenient)
│       ├── application-production.yml   (PostgreSQL, strict)
│       └── templates/resume.html        (Thymeleaf)
├── k8s/
│   ├── base/                Kustomize base manifests
│   ├── overlays/staging/    Staging overrides
│   ├── overlays/production/ Production overrides
│   └── kafka/               Strimzi CRDs
├── terraform/               GCP infrastructure (IaC)
├── .github/
│   ├── workflows/ci.yml     CI pipeline
│   └── workflows/cd.yml     CD pipeline
├── Dockerfile               Multi-stage build
├── docker-compose.yml       Local Kafka + ZooKeeper + Kafka-UI
└── build.gradle.kts
```

---

## 📄 License

MIT © Ivaylo Blagoev
