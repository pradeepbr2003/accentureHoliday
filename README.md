# Holiday API

A simple Spring Boot service that parses a text file of holiday entries and exposes REST endpoints to query mandatory and floating holidays, filter by month, and search by name.

<!-- CI badge; update the owner/repo after pushing to GitHub -->
![CI](https://github.com/pradeepbr2003/accentureHoliday/actions/workflows/ci.yml/badge.svg)

## Quick start (Windows)

- Requirements: Java 17+, Maven Wrapper (included)
- Build:

```powershell
# From the project root
.\mvnw.cmd -q clean package
```

- Run (default data file):

```powershell
# Using Maven wrapper (works from project root)
.\mvnw.cmd spring-boot:run

# Or run the built jar (from project root so the default file path resolves)
java -jar target/accentureHoliday-0.0.1-SNAPSHOT.jar
```

Tip: When running the jar from a different working directory, set a custom data file path (see Configuration) because the default path points to a file relative to the project root.

## Simple UI

A minimal static UI is available to explore the APIs without external tools.

- Location: `src/main/resources/static/`
- Entry page: `http://localhost:8080/` (serves `static/index.html`)
- Features:
  - Buttons to load all mandatory or all floating holidays
  - Filters by month for mandatory/floating
  - Case-insensitive search by name
  - Results table with Name, Date, Day, City, Type
  - Quick links to Swagger and raw endpoints

If you configure a custom server context path, the UI uses relative links and should continue to work under that path.

## Project layout

- `src/main/java/org/accenture/holiday` — Spring Boot app code
  - `controller/HolidayController.java` — REST endpoints under `/api/holidays`
  - `service/HolidayService.java` — Parses holidays from a text file
  - `model/Holiday.java` — Immutable holiday model
- `src/main/resources/bangalore_holidays.log` — Default holiday data source
- `src/main/resources/application.properties` — Spring configuration; override input file via `holidays.file.path`

## Build

```powershell
# From the project root
.\mvnw.cmd clean package
```

The build produces `target/accentureHoliday-0.0.1-SNAPSHOT.jar`.

## Run

You can run with the default input file (works when executed from the project root):

```powershell
# Using Maven wrapper
.\mvnw.cmd spring-boot:run

# Or run the built jar
java -jar target/accentureHoliday-0.0.1-SNAPSHOT.jar
```

To use a custom holidays file, set `holidays.file.path` (relative or absolute path):

```powershell
# Via command-line property
java -jar target/accentureHoliday-0.0.1-SNAPSHOT.jar --holidays.file.path=C:\\data\\holidays.log

# Or with Spring's environment JSON (alternative)
$env:SPRING_APPLICATION_JSON='{"holidays.file.path":"C:/data/holidays.log"}'; java -jar target/accentureHoliday-0.0.1-SNAPSHOT.jar
```

Notes:
- The default path is `src/main/resources/bangalore_holidays.log` (relative to the current working directory). When running the jar outside the project root, provide an explicit path.
- On Windows, escape backslashes when passing file paths as JVM/Spring properties.

## Data format

Each line in the holidays file should match:

```
Mandatory|Floating holiday for <Name> on <DayOfWeek>, <d-MMM-yyyy> in <City>
```

Examples:

```
Mandatory holiday for Republic Day on Monday, 26-Jan-2026 in Bangalore
Floating holiday for Family Day on Friday, 13-Mar-2026 in Bangalore
```

Parsing rules:
- Lines not matching the pattern are ignored.
- Holidays falling on Saturday or Sunday are filtered out.
- Date format is `d-MMM-yyyy` (English locale), e.g., `15-Jan-2026`.

## API

Base path: `/api/holidays`

- `GET /api/holidays/mandatory` — List all mandatory holidays
- `GET /api/holidays/floating` — List all floating holidays
- `GET /api/holidays/mandatory/month/{month}` — Mandatory holidays in month (1–12)
- `GET /api/holidays/floating/month/{month}` — Floating holidays in month (1–12)
- `GET /api/holidays/search/{keyword}` — Case-insensitive substring search on holiday name

Behavior notes:
- Month outside 1–12 returns an empty list (no explicit validation).
- If the data file cannot be read, a 500 error is returned by Spring’s default exception handling.

### Example requests

```powershell
# All mandatory
curl http://localhost:8080/api/holidays/mandatory

# Floating holidays in March
curl http://localhost:8080/api/holidays/floating/month/3

# Search by name
curl http://localhost:8080/api/holidays/search/rep
```

### Response shape

```json
[
  {
    "name": "Republic Day",
    "date": "2026-01-26",
    "dayOfWeek": "MONDAY",
    "city": "Bangalore",
    "type": "MANDATORY"
  }
]
```

## API documentation (OpenAPI/Swagger UI)

This project includes SpringDoc OpenAPI. When the app is running:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

Use the Swagger UI to explore and test endpoints interactively.

## Testing

Run unit tests:

```powershell
.\mvnw.cmd -q test
```

Test reports are written to `target/surefire-reports/`.

## Configuration

| Property | Default | Description |
|---|---|---|
| `holidays.file.path` | `src/main/resources/bangalore_holidays.log` | Path to the holidays data file. Relative to current working directory. |
| `springdoc.api-docs.path` | `/v3/api-docs` | OpenAPI endpoint path. |
| `springdoc.swagger-ui.path` | `/swagger-ui` | Swagger UI base path. |

You can also use standard Spring Boot properties (e.g., `server.port`) via `application.properties`, environment variables, or command-line flags.

## Development notes

- The controller injects `holidays.file.path` with default `src/main/resources/bangalore_holidays.log`.
- Parsing uses English locale and `d-MMM-yyyy` format.
- API docs provided by `springdoc-openapi-starter-webmvc-ui`.

## GitHub onboarding

Run the helper script (PowerShell) from the project root to create the repo under your account and push:

```powershell
# Default: owner pradeepbr2003, repo accentureHoliday, public visibility
powershell -ExecutionPolicy Bypass -File .\scripts\onboard.ps1

# Or customize
powershell -ExecutionPolicy Bypass -File .\scripts\onboard.ps1 -Owner pradeepbr2003 -RepoName accentureHoliday -Visibility public
```

Alternatively, follow one of the manual options below from a PowerShell prompt in the project root (C:\Personal\Codebase\holiday).

Option A — Using GitHub CLI (recommended):

```powershell
# Initialize git if not already
if (-not (Test-Path .git)) { git init }

git add .
# First commit
if (-not (git rev-parse --quiet --verify HEAD)) { git commit -m "chore: initial import" } else { git commit -m "chore: update" }

# Create GitHub repo under pradeepbr2003 (public by default per script; add --private if you prefer)
gh repo create pradeepbr2003/accentureHoliday --public --source . --remote origin --push
```

Option B — Manual remote setup (no GitHub CLI):

```powershell
# Initialize and commit
if (-not (Test-Path .git)) { git init }

git add .
if (-not (git rev-parse --quiet --verify HEAD)) { git commit -m "chore: initial import" } else { git commit -m "chore: update" }

# Create an empty repo named "accentureHoliday" in your GitHub account via the web UI first,
# then set the remote and push (replace PAT with a personal access token if prompted)
git remote remove origin 2>$null

git remote add origin https://github.com/pradeepbr2003/accentureHoliday.git

git branch -M main

git push -u origin main
```

After pushing, GitHub Actions will automatically run the CI workflow defined in `.github/workflows/ci.yml`. The badge at the top of this README will reflect the build status.

## Troubleshooting

- File not found: Provide an absolute path via `--holidays.file.path=C:\\path\\to\\file.log` or run the app from the project root so the default relative path resolves.
- Windows path escaping: When passing properties on the command line, double the backslashes or use forward slashes.
- 500 errors on requests: Check that the data file exists and lines match the expected pattern.

## License

This project is intended for educational/demo purposes. No license is specified.
