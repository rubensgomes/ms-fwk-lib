# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/claude-code) when working with this repository.

## Project Overview

This is a Spring Boot infrastructure framework library (`ms-fwk-lib`) for microservices, written in Kotlin. It provides common web filters, exception handling, MDC (Mapped Diagnostic Context) utilities, and configuration components.

## Build System

- **Build tool**: Gradle 9.1.0 with Kotlin DSL
- **Language**: Kotlin (JVM)
- **Java version**: 25 (configured via toolchain)
- **Framework**: Spring Boot 4.x

## Common Commands

```bash
# Clean build artifacts
./gradlew clean

# Build the project
./gradlew build

# Run tests
./gradlew test

# Apply code formatting (Spotless)
./gradlew :lib:spotlessApply

# Check code quality
./gradlew check

# Build JAR
./gradlew jar

# Full assemble
./gradlew assemble

# Display Java toolchains
./gradlew -q javaToolchains
```

## Project Structure

```
ms-fwk-lib/
├── lib/                          # Main library module
│   ├── build.gradle.kts          # Module build configuration
│   ├── gradle.properties         # Module properties (version, artifactId)
│   └── src/
│       ├── main/kotlin/com/rubensgomes/msfwklib/
│       │   ├── common/           # Constants and utilities (MDCConstants, RootErrorMessageResolver)
│       │   ├── config/           # Spring configurations (FilterConfiguration, CommonConfiguration)
│       │   ├── threadlocal/      # Thread-local context utilities (ContextHolder)
│       │   └── web/
│       │       ├── aspect/       # AOP components (GlobalExceptionHandler, RequestBodyMDCUpdateAdvice)
│       │       └── filter/       # Servlet filters (MDCClearFilter, JsonRequestIntrospectFilter)
│       └── test/kotlin/          # Unit tests
├── settings.gradle.kts           # Gradle settings (version catalog, repositories)
├── gradle.properties             # Root properties (developer info, release plugin)
└── gradle/wrapper/               # Gradle wrapper files
```

## Key Configuration Files

- `lib/gradle.properties`: Contains `artifactId`, `version`, and module-specific settings
- `gradle.properties`: Contains developer info, repository URLs, and release plugin settings
- `settings.gradle.kts`: Configures version catalogs from `com.rubensgomes:gradle-catalog`

## Testing

Tests use JUnit 5 (JUnit Platform) with SpringMockK for mocking. Run tests with:
```bash
./gradlew test
```

## Publishing

The library is published to GitHub Packages at `https://maven.pkg.github.com/rubensgomes/jvm-libs`. Requires `GITHUB_USER` and `GITHUB_TOKEN` environment variables.

## Branching Strategy

- **main**: Trunk-based development branch with release tagging
- **release**: Contains the most recently released code
