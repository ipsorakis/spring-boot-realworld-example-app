# Agents

## Overview

This document provides guidance for AI agents working with this codebase.

## Project Structure

- `api` - Web layer implemented by Spring MVC
- `core` - Business model including entities and services
- `application` - High-level services for querying data transfer objects
- `infrastructure` - Implementation classes for technical details

## Getting Started

### Prerequisites

- Java 11

### Running the Application

```bash
./gradlew bootRun
```

### Running Tests

```bash
./gradlew test
```

### Code Formatting

```bash
./gradlew spotlessJavaApply
```

## Key Technologies

- Spring Boot
- MyBatis
- GraphQL (via Netflix DGS Framework)
- SQLite (for local development)

## Architecture Notes

- Follows Domain Driven Design (DDD) principles
- Uses Data Mapper pattern for persistence
- Implements CQRS pattern to separate read and write models
