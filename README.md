# ![RealWorld Example App using Kotlin and Spring](example-logo.png)

[![Actions](https://github.com/gothinkster/spring-boot-realworld-example-app/workflows/Java%20CI/badge.svg)](https://github.com/gothinkster/spring-boot-realworld-example-app/actions)

> ### Spring boot + MyBatis codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld-example-apps) spec and API.

This codebase was created to demonstrate a fully fledged full-stack application built with Spring boot + Mybatis including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

## Table of Contents

- [Features](#features)
- [GraphQL Support](#graphql-support)
- [How it works](#how-it-works)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Database](#database)
- [Getting started](#getting-started)
- [Docker](#try-it-out-with-docker)
- [Configuration](#configuration)
- [Running Tests](#run-test)
- [Code Format](#code-format)
- [Tech Stack](#tech-stack)
- [Contributing](#help)

## Features

This application implements the RealWorld specification and includes the following features:

- User registration and authentication with JWT tokens
- User profile management with follow/unfollow functionality
- Article CRUD operations with slug-based URLs
- Article feed with pagination support
- Tag-based article filtering
- Article favoriting system
- Comment system for articles
- Both REST API and GraphQL interfaces

## GraphQL Support

Following some Domain Driven Design (DDD) principles, REST or GraphQL is just a kind of adapter. The domain layer remains consistent regardless of the interface used. This repository implements both GraphQL and REST simultaneously.

The GraphQL schema is located at [src/main/resources/schema/schema.graphqls](src/main/resources/schema/schema.graphqls) and the visualization looks like below:

![](graphql-schema.png)

This implementation uses [dgs-framework](https://github.com/Netflix/dgs-framework), Netflix's GraphQL server framework for Java.

### GraphQL Queries

- `article(slug: String!)` - Get a single article by slug
- `articles(first, after, last, before, authoredBy, favoritedBy, withTag)` - List articles with filtering and pagination
- `me` - Get current authenticated user
- `feed(first, after, last, before)` - Get user's article feed
- `profile(username: String!)` - Get user profile
- `tags` - Get all tags

### GraphQL Mutations

**User & Profile:**
- `createUser(input: CreateUserInput)` - Register a new user
- `login(password, email)` - Authenticate user
- `updateUser(changes: UpdateUserInput!)` - Update user profile
- `followUser(username)` / `unfollowUser(username)` - Follow/unfollow users

**Article:**
- `createArticle(input: CreateArticleInput!)` - Create new article
- `updateArticle(slug, changes)` - Update existing article
- `favoriteArticle(slug)` / `unfavoriteArticle(slug)` - Favorite/unfavorite articles
- `deleteArticle(slug)` - Delete an article

**Comment:**
- `addComment(slug, body)` - Add comment to article
- `deleteComment(slug, id)` - Delete a comment

## How it works

The application uses Spring Boot (Web, Mybatis).

- Use the idea of Domain Driven Design to separate the business term and infrastructure term.
- Use MyBatis to implement the [Data Mapper](https://martinfowler.com/eaaCatalog/dataMapper.html) pattern for persistence.
- Use [CQRS](https://martinfowler.com/bliki/CQRS.html) pattern to separate the read model and write model.

## Project Structure

The code is organized following Domain Driven Design principles:

```
src/main/java/io/spring/
├── api/                    # Web layer (REST controllers)
│   ├── ArticleApi.java         # Single article operations (GET, PUT, DELETE)
│   ├── ArticlesApi.java        # Article list and creation
│   ├── ArticleFavoriteApi.java # Favorite/unfavorite articles
│   ├── CommentsApi.java        # Comment operations
│   ├── CurrentUserApi.java     # Current user profile
│   ├── ProfileApi.java         # User profiles and follow
│   ├── TagsApi.java            # Tags listing
│   ├── UsersApi.java           # User registration and login
│   ├── exception/              # Exception handlers
│   └── security/               # Security filters and configuration
├── application/            # High-level services for DTOs and queries
├── core/                   # Business domain models and services
│   ├── article/                # Article entity and repository
│   ├── comment/                # Comment entity and repository
│   ├── favorite/               # Favorite entity and repository
│   ├── service/                # Domain services (JWT, Authorization)
│   └── user/                   # User entity and repository
├── graphql/                # GraphQL resolvers (generated + custom)
└── infrastructure/         # Technical implementation details
    └── mybatis/                # MyBatis mappers and configurations
```

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/users` | Register a new user | No |
| POST | `/users/login` | Login and get JWT token | No |

### User

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/user` | Get current user | Yes |
| PUT | `/user` | Update current user | Yes |

### Profiles

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/profiles/{username}` | Get user profile | Optional |
| POST | `/profiles/{username}/follow` | Follow a user | Yes |
| DELETE | `/profiles/{username}/follow` | Unfollow a user | Yes |

### Articles

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/articles` | List articles (with filters) | Optional |
| GET | `/articles/feed` | Get user's article feed | Yes |
| GET | `/articles/{slug}` | Get single article | Optional |
| POST | `/articles` | Create article | Yes |
| PUT | `/articles/{slug}` | Update article | Yes |
| DELETE | `/articles/{slug}` | Delete article | Yes |

### Favorites

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/articles/{slug}/favorite` | Favorite an article | Yes |
| DELETE | `/articles/{slug}/favorite` | Unfavorite an article | Yes |

### Comments

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/articles/{slug}/comments` | Get article comments | Optional |
| POST | `/articles/{slug}/comments` | Add comment to article | Yes |
| DELETE | `/articles/{slug}/comments/{id}` | Delete comment | Yes |

### Tags

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/tags` | Get all tags | No |

## Security

Integration with Spring Security and add other filter for jwt token process.

The secret key is stored in `application.properties`.

### JWT Configuration

- **Secret Key**: Configured in `application.properties` via `jwt.secret`
- **Session Time**: Default 86400 seconds (24 hours), configurable via `jwt.sessionTime`
- **Token Format**: Bearer token in Authorization header

## Database

It uses a ~~H2 in-memory database~~ sqlite database (for easy local test without losing test data after every restart), can be changed easily in the `application.properties` for any other database.

### Database Configuration

The database is configured in `application.properties`:

```properties
spring.datasource.url=jdbc:sqlite:dev.db
spring.datasource.driver-class-name=org.sqlite.JDBC
```

Database migrations are managed by Flyway and located in `src/main/resources/db/migration/`.

## Getting started

You'll need Java 11 installed.

    ./gradlew bootRun

To test that it works, open a browser tab at http://localhost:8080/tags .  
Alternatively, you can run

    curl http://localhost:8080/tags

### Example API Calls

**Register a new user:**
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"user":{"username":"jake","email":"jake@example.com","password":"password123"}}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user":{"email":"jake@example.com","password":"password123"}}'
```

**Create an article (with JWT token):**
```bash
curl -X POST http://localhost:8080/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Token <your-jwt-token>" \
  -d '{"article":{"title":"How to train your dragon","description":"Ever wonder how?","body":"You have to believe","tagList":["reactjs","angularjs","dragons"]}}'
```

## Try it out with [Docker](https://www.docker.com/)

You'll need Docker installed.
	
    ./gradlew bootBuildImage --imageName spring-boot-realworld-example-app
    docker run -p 8081:8080 spring-boot-realworld-example-app

## Try it out with a RealWorld frontend

The entry point address of the backend API is at http://localhost:8080, **not** http://localhost:8080/api as some of the frontend documentation suggests.

## Configuration

Key configuration options in `application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.url` | Database connection URL | `jdbc:sqlite:dev.db` |
| `jwt.secret` | Secret key for JWT signing | (configured) |
| `jwt.sessionTime` | JWT token validity in seconds | `86400` |
| `image.default` | Default user avatar URL | (configured) |
| `mybatis.configuration.cache-enabled` | Enable MyBatis caching | `true` |

## Run test

The repository contains a lot of test cases to cover both api test and repository test.

    ./gradlew test

### Test Structure

Tests are organized to mirror the main source structure:

- `api/` - REST API integration tests
- `application/` - Application service tests
- `core/` - Domain model unit tests
- `infrastructure/` - Repository and mapper tests

## Code format

Use spotless for code format.

    ./gradlew spotlessJavaApply

To check formatting without applying changes:

    ./gradlew spotlessCheck

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Spring Boot 2.6.3 | Application framework |
| Spring Security | Authentication and authorization |
| MyBatis | SQL mapping framework |
| Netflix DGS | GraphQL framework |
| SQLite | Database (development) |
| Flyway | Database migrations |
| JWT (jjwt) | Token-based authentication |
| Lombok | Boilerplate code reduction |
| Spotless | Code formatting |
| JUnit 5 | Testing framework |
| REST Assured | API testing |

## Help

Please fork and PR to improve the project.
