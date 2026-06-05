# REST API Test Framework

A Java-based REST API test automation framework built with RestAssured and TestNG,
targeting a Spring Boot application deployed on Render.
Includes a GitHub Actions workflow that wakes up the service and audits all
registered endpoints every morning at 8:30 AM IST.

---

## Tech Stack

| Layer | Tool | Version |
|---|---|---|
| Language | Java | 21 |
| API Testing | RestAssured | 5.4.0 |
| Test Runner | TestNG | 7.9.0 |
| Build Tool | Maven | 3.x |
| Reporting | ExtentReports | 5.1.1 |
| JSON Parsing | Jackson Databind | 2.16.1 |
| CI Scheduling | GitHub Actions | — |

---

## Framework Architecture

```
rest-api-framework/
├── .github/
│   └── workflows/
│       └── swagger-warmup.yml         # Scheduled warm-up + endpoint audit
└── framework/
    ├── src/
    │   ├── main/java/com/qa/framework/
    │   │   ├── config/
    │   │   │   └── ConfigManager.java # Reads config.properties centrally
    │   │   └── utils/
    │   │       └── ReportManager.java # ExtentReports HTML report setup
    │   └── test/java/com/qa/tests/
    │       ├── base/
    │       │   └── BaseTest.java      # Suite setup, RestAssured base spec
    │       └── api/
    │           ├── HealthCheckTest.java  # Service health + docs validation
    │           └── ApiTests.java         # Functional endpoint tests
    ├── src/test/resources/
    │   ├── config.properties          # Base URL, timeouts, report settings
    │   └── testng.xml                 # Test suite execution order
    └── pom.xml                        # Dependencies and Maven config
```

---

## Prerequisites

- Java 21 — [Download](https://adoptium.net)
- Maven 3.x — [Download](https://maven.apache.org/download.cgi)
- VS Code + [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

Verify installation:
```bash
java -version   # should show 21.x.x
mvn -version    # should show 3.x.x
```

---

## Setup

```bash
# Clone the repo
git clone https://github.com/YOUR-USERNAME/rest-api-framework.git
cd rest-api-framework/framework

# Install dependencies
mvn clean install -DskipTests
```

---

## Configuration

All settings are in one place — `src/test/resources/config.properties`:

```properties
BASE_URL=https://firststudyproject.onrender.com
ENV=dev
CONNECTION_TIMEOUT=10000
READ_TIMEOUT=30000
REPORT_PATH=test-output/ExtentReport.html
```

To point the framework at a different environment, change `BASE_URL` here.
No changes needed in any test class.

---

## Running Tests

```bash
# Run full suite
mvn test

# Run a specific test class only
mvn test -Dtest=HealthCheckTest

# Run with console output visible
mvn test -Dsurefire.useFile=false
```

After the run, open the HTML report:
```
framework/test-output/ExtentReport.html
```

---

## Test Suite Structure

Tests run in this order, defined in `testng.xml`:

**1. Health Checks** — run first, every time
| Test | What it verifies |
|---|---|
| `swaggerUiShouldBeReachable` | Swagger UI returns HTTP 200 |
| `apiDocsShouldReturnJson` | `/v3/api-docs` returns valid OpenAPI JSON |
| `apiSpecShouldHaveEndpoints` | At least one endpoint is registered |

**2. API Functional Tests**
| Test | What it verifies |
|---|---|
| `testApiShouldReturn200` | Endpoint returns expected HTTP status |
| `testApiResponseTime` | Response is within 5-second SLA |

---

## GitHub Actions — Morning Warm-Up

The file `.github/workflows/swagger-warmup.yml` runs every day at **8:30 AM IST**.

**What it does:**
- Pings the Render service with retries until it wakes from sleep
- Fetches `/v3/api-docs` and prints every loaded endpoint, grouped by HTTP method
- Fails the job and sends a notification if the service is unreachable

**Why this matters:**
Render's free tier spins down after 15 minutes of inactivity.
This workflow ensures the API is warm and all endpoints are loaded
before the team starts the day — similar to how production banking
systems pre-load services before market open.

To trigger manually: **GitHub → Actions tab → Run workflow**

---

## Design Decisions

**Why RestAssured?**
Industry-standard Java library for REST API testing. Fluent BDD-style syntax
(`given/when/then`) aligns with existing TestNG + Cucumber experience and
is widely used in BFSI automation stacks.

**Why centralized config?**
`ConfigManager` reads `config.properties` once at startup. All test classes
call `ConfigManager.get("BASE_URL")` — changing the URL in one file updates
every test, eliminating hardcoded values across the suite.

**Why Health Checks as a separate test group?**
If the service is down, functional tests will produce misleading failures.
Running health checks first and failing fast gives clearer signal about
whether failures are environment issues or actual defects.

**Why ExtentReports?**
Generates a self-contained HTML report with test status, request/response logs,
and timestamps. No server required to view — open directly in a browser.

---

## Roadmap

- [ ] Add Allure Reports integration
- [ ] Add schema validation using JSON Schema files
- [ ] Add data-driven tests using TestNG `@DataProvider`
- [ ] Integrate with GitHub Actions to run tests on every push
- [ ] Add performance baseline assertions (response time thresholds)
- [ ] Add test for all endpoints discovered dynamically from `/v3/api-docs`

---

## About

Built as part of a team QA setup for a collaborative Spring Boot project.
The framework is designed to grow alongside the API — new endpoints added
by the dev team are automatically discovered via the OpenAPI spec,
and corresponding test cases are added to `ApiTests.java`.

**Author:** Sujay — QA/Test Engineer
**Domain:** REST API Automation | BFSI QA
**Stack:** Java · RestAssured · TestNG · Maven · GitHub Actions