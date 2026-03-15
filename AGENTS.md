# AGENTS.md

## Project Overview

**JayReq** (`io.badgod:jayreq`) is a dependency-free Java HTTP client library. It is a thin, opinionated wrapper around `java.net.http.HttpClient` providing a clean, minimal API for synchronous HTTP requests. Zero runtime dependencies -- just Java 21+.

- **Author:** Robert Pelger (`robpelger`)
- **License:** Apache 2.0
- **Repository:** `git@github.com:pelgero/jayreq.git`
- **Current version:** 0.0.6

### Design Philosophy

- **Zero runtime dependencies.** The library uses only JDK standard library classes (`java.net.http.*`).
- **Minimal surface area.** 6 classes total. No feature bloat.
- **Immutability.** `Request`, `Response`, `Body`, and `Headers` are immutable after construction.
- **Synchronous only.** No async/reactive support by design.
- **String bodies only.** Request and response bodies are always strings; callers use `Body.Converter<T>` for deserialization.
- **Unchecked errors.** All exceptions are wrapped in `JayReq.Error` (extends `RuntimeException`).

## Architecture

All production code lives in the `io.badgod.jayreq` package under `src/main/java/io/badgod/jayreq/`. There are exactly 6 source files:

| Class | Role |
|-------|------|
| `JayReq` | Main API interface. Contains static `get()` shortcut, interface methods for all HTTP verbs, inner `Client` implementation class, and inner `Error` exception class. |
| `Request` | Immutable request model (`URI`, `Method`, `Body`, `Headers`). Implements `Serializable`. |
| `Response` | Immutable response model (`Request`, `Body`, `int status`, `Headers`). Supports body conversion via `Body.Converter<T>`. Implements `Serializable`. |
| `Headers` | Case-insensitive header container backed by `TreeMap(String.CASE_INSENSITIVE_ORDER)`. Factory methods: `of()`, `authBearer()`, `authBasic()`, `accept()`, `mergeAll()`. |
| `Body` | Simple string wrapper with `Optional<String> value()`. Contains `Converter<T>` functional interface for response body transformation. |
| `Method` | Enum: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`. |

### Key Implementation Details

- `JayReq.Client` wraps a `java.net.http.HttpClient` and executes requests synchronously via `httpClient.send()`.
- `JayReq.Client.createRequest()` uses a switch expression (Java 21) to map `Method` to the appropriate `HttpRequest.Builder` method.
- `JayReq.Error` is an unchecked exception that preserves the original `Request` and optionally the `Response` for debugging.
- `Headers.toStringArray()` flattens headers to a `String[]` of alternating key/value pairs for use with `HttpRequest.Builder.headers()`.
- `Response.body(Body.Converter<T>)` returns `Optional<T>`, enabling functional-style body transformation without coupling to any JSON library.

## Build & Development

### Prerequisites

- Java 21+ (project uses Temurin distribution)
- Docker (required for integration tests via Testcontainers)

### Build System

- **Gradle 9.4.0** with Kotlin DSL
- Wrapper scripts included (`./gradlew` / `gradlew.bat`)

### Common Commands

```bash
# Build the project (compiles + runs tests)
./gradlew build

# Run tests only
./gradlew test

# Generate JaCoCo test coverage report
./gradlew jacocoTestReport
# Report at: build/reports/jacoco/test/jacocoTestReport.csv

# Clean build
./gradlew clean build
```

### Dependencies

**Runtime:** None.

**Test only:**

| Dependency | Purpose |
|------------|---------|
| JUnit 5 (5.12.2) | Test framework |
| Hamcrest 3.0 | Fluent assertion matchers |
| Testcontainers 1.21.3 | Docker-based integration testing |
| SLF4J Simple 2.0.17 | Logging (required by Testcontainers) |
| Gson 2.13.1 | JSON parsing in tests (demonstrating body conversion) |

### Publishing

The project publishes to Maven Central via the `com.vanniktech.maven.publish` plugin (v0.36.0). Artifacts are GPG-signed. Configuration is in `build.gradle.kts` (lines 44-73).

## Testing Strategy

Tests live in `src/test/java/io/badgod/` (note: `io.badgod` package, not `io.badgod.jayreq` -- tests exercise the library from a consumer's perspective).

### Integration Tests

- `HttpBinIntegrationTest` is the abstract base class that spins up an **httpbin** Docker container via Testcontainers.
- All HTTP method test classes extend this base: `JayReqGetTest`, `JayReqPostTest`, `JayReqPutTest`, `JayReqDeleteTest`, `JayReqPatchTest`.
- Tests use real HTTP interactions, not mocks, providing high confidence in correctness.
- `JayReqErrorTest` tests error handling (connection failures, error wrapping).

### Unit Tests

- `BodyTest` -- Body creation, null/blank handling, `isEmpty()`, `toString()`.
- `HeadersTest` -- Header creation, merging, factory methods, `get()`, auth helpers, equality, edge cases.
- `RequestTest` -- Request construction, header merging, defaults, `toString()`.
- `ResponseTest` -- Response accessors, body conversion with `Converter`, empty body handling.

### Coverage

Near-100% instruction and branch coverage across all 6 classes (verified via JaCoCo). Only 2 branch misses in `Headers` (edge-case null checks).

**When adding new functionality, maintain this coverage level.** All public API surface should have both unit and integration test coverage.

## Code Conventions

### Style

- 4-space indentation, LF line endings, UTF-8 (see `.editorconfig`)
- Trim trailing whitespace, ensure final newline
- Use Java 21 features: switch expressions, `var`, records (in tests)
- No wildcard imports

### Package Structure

- Production: `io.badgod.jayreq` (flat -- all 6 classes at the same level)
- Tests: `io.badgod` (one level up, simulating external consumer)

### Error Handling

- All exceptions from `HttpClient.send()` are caught and wrapped in `JayReq.Error`
- `JayReq.Error` always contains the original `Request`; `Response` is optional (empty if the request never completed)
- No checked exceptions in the public API

### Commit Message Style

- Present tense, imperative or descriptive: "adds PATCH method", "improve test coverage for Body and Headers classes"
- Include context in commit body for non-trivial changes
- No conventional commits prefix required

## CI/CD

### GitHub Actions

File: `.github/workflows/gradle.yml`

- **Triggers:** Push and PR to `main`
- **Runner:** `ubuntu-latest`
- **Steps:** Checkout -> Setup JDK 21 (Temurin) -> Setup Gradle -> `./gradlew build`

### Branches

- `main` -- primary branch, CI-protected
- `mtls-wip` -- abandoned local branch from a mutual TLS experiment (feature was removed in commit `cfae200`)

## Maintaining This Document

Agents should update this file (`AGENTS.md`) regularly to reflect recent changes to the project. After making significant modifications -- such as adding new classes, changing the public API, updating dependencies, altering the build configuration, or adding new test patterns -- review this document and update the relevant sections. Keeping this file accurate ensures that all agents and contributors have reliable context about the project's current state.

## Project History

The project evolved through these major phases:

### Phase 1: Initial Implementation (Jan 2024)
Commits `11eb161` through `1d13246`. Started as a simple GET-only client with Gson-based JSON body handling. Quickly added POST support, response status codes, optional response bodies, error handling, request headers via a new `Headers` class, and switched tests from external services to Testcontainers/httpbin.

### Phase 2: Major Refactor (Feb 2024)
Commits `778db06` through `04327a7`. Removed Gson as a runtime dependency (moved to test-only). Introduced `Body.Converter<T>` for caller-driven body transformation. Inlined the `JayReqHttpClient` implementation into `JayReq` as an inner `Client` class. Made `Headers` case-insensitive. Simplified error hierarchy to a single `JayReq.Error`.

### Phase 3: Auth & Publishing (Apr-May 2024)
Commits `48a4ded` through `0d14267`. Added `authBearer()`, `authBasic()`, and `accept()` header helpers. Configured Maven Central publishing. Multiple version bumps.

### Phase 4: Modernization (Mar 2026)
Commits `26114a2` through `038c236`. Updated Gradle to 9.4.0, updated all dependencies, cleaned up build scripts. Experimented with mutual TLS support but removed it (`cfae200`). Added PUT, DELETE, and PATCH methods. Reorganized tests with shared base class. Added comprehensive unit tests for all model classes achieving near-100% coverage. Added JaCoCo reporting. Wrote the full project README.
