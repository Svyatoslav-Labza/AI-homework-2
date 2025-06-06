# Backend Application

A robust Spring Boot backend application with JWT authentication, PostgreSQL database integration, and containerized deployment support.

## Technologies Used

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Docker
- TestContainers for testing

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (if running locally)

## Project Structure

```
backend-app/
├── src/                    # Source code
├── docker/                 # Docker related files
├── docker-compose.yml      # Docker compose configuration
├── Dockerfile             # Docker build file
├── pom.xml                # Maven dependencies and build configuration
└── README.md              # Project documentation
```

## Features

- RESTful API endpoints
- JWT-based authentication and authorization
- PostgreSQL database integration
- Containerized deployment support
- Comprehensive test suite with TestContainers

## Getting Started

### Local Development

1. Clone the repository
2. Configure your PostgreSQL database settings in `application.properties` or `application.yml`
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Docker Deployment

1. Build the Docker image:
   ```bash
   docker build -t backend-app .
   ```

2. Run with Docker Compose:
   ```bash
   docker-compose up
   ```

## Testing

Run the test suite using Maven:

```bash
mvn test
```

The project uses TestContainers for integration tests, ensuring a consistent testing environment.

## Dependencies

Major dependencies include:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- JWT (io.jsonwebtoken)
- PostgreSQL Driver
- TestContainers

For a complete list of dependencies, please refer to the `pom.xml` file.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

[Add your license information here] 