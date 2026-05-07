# digital policy managment

A Spring Boot backend application for managing digital policies, KPIs (Key Performance Indicators), and their associated indicators and reports.

## Overview

This system allows organizations and policy makers to define policies, attach KPIs to those policies, and track indicator values over time. When indicator values are updated, KPI values are automatically recalculated using configurable mathematical equations.

## Architecture

- **Service**: Java 11, Spring Boot 2.7.12
- **Database**: PostgreSQL 14
- **Build Tool**: Maven
- **Containerization**: Docker
- **API Docs**: OpenAPI / Swagger UI (via SpringDoc)

## Domain Logic

```
Policy
  └── KPI (Key Performance Indicator)
        └── Indicator(s) — used in the KPI equation
              └── IndicatorReport (timestamped value entries)
        └── KpiReport (auto-calculated on each indicator update)
```

- **Policy**: A strategic policy with a name, description, sector, and region.
- **KPI**: A named metric with a mathematical equation (e.g. `A + B / 2`) and optional target value/time. Belongs to a Policy.
- **Indicator**: A named variable (with a symbol) that feeds into one or more KPI equations.
- **IndicatorReport**: A timestamped value entry for an Indicator.
- **KpiReport**: A timestamped calculated value for a KPI, automatically generated whenever one of its indicators is updated.

## API Endpoints

### Policies — `/policy`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/policy/all` | Get all policies |
| GET | `/policy?name={name}` | Get a policy by name |
| POST | `/policy` | Create a new policy |

### KPIs — `/kpi`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/kpi/all` | Get all KPIs |
| GET | `/kpi?name={name}` | Get a KPI by name |
| GET | `/kpi/allPolicy?policyName={name}` | Get all KPIs for a policy |
| POST | `/kpi` | Create a new KPI |
| PUT | `/kpi?name={name}&targetValue={v}&targetTime={t}` | Update target value/time |

### Indicators — `/indicator`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/indicator/all` | Get all indicators |
| GET | `/indicator?name={name}` | Get indicator by name |
| GET | `/indicator?symbol={symbol}` | Get indicator by symbol |
| POST | `/indicator` | Create a new indicator |

### Reports — `/report`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/report/indicator` | Create an indicator report (set absolute value) |
| POST | `/report/indicator/increaseby` | Create a report by incrementing the latest value |
| GET | `/report/indicator?indicatorName={name}` | Get all reports for an indicator |
| GET | `/report/indicator/all` | Get all indicator reports |
| GET | `/report/kpi?kpiName={name}` | Get all KPI reports by name |
| GET | `/report/kpi/all` | Get all KPI reports |

### API Documentation
Swagger UI is available at `/api-ui` and the OpenAPI spec at `/api`.

## Getting Started

### Prerequisites

- Java 11
- Maven (or use the included `mvnw` wrapper)
- Docker & Docker Compose (for containerized setup)
- PostgreSQL 14 (if running locally without Docker)

### Running Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/skillab-project/digital-policy-management
   cd digital-policy-management
   ```

2. **Configure the database** in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/test_db
   spring.datasource.username=root
   spring.datasource.password=<your-password>
   ```

3. **Build and run:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   The application will start on port **8080**.

### Running with Docker Compose

```bash
docker compose up -d
```

This starts two services:
- **backend-policy**: The Spring Boot application, exposed on port `8085`
- **db-policy**: PostgreSQL 14, exposed on port `5455`
> **Note:** The `jbpm-policy` service is defined in `docker-compose.yml` but is currently commented out — it is planned for a future version and not yet in use.

## Running Tests

Tests use an in-memory H2 database and Mockito for service-layer unit tests.

```bash
cd backend
./mvnw test
```

## Project Structure

```
.
├── backend/
│   ├── src/
│   │   ├── main/java/gr/uom/strategicplanning/
│   │   │   ├── controllers/       # REST controllers
│   │   │   ├── models/            # JPA entities
│   │   │   ├── repositories/      # Spring Data JPA repositories
│   │   │   ├── services/          # Business logic
│   │   │   └── config/            # CORS and app configuration
│   │   └── test/                  # Tests
│   ├── Dockerfile
│   └── pom.xml
└── docker-compose.yml
```

## License

This project is licensed under the [Eclipse Public License v2.0](LICENSE).