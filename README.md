# Lost & Found Application

This Spring Boot application facilitates the management of lost and found items. It enables administrators to upload item information via files and allows users to view and claim these items. Authentication is handled using JWT, and data is persisted in a PostgreSQL database.

## 1. Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK):** Version 21 or newer.
*   **Apache Maven:** Version 3.6.x or newer (for building the project).
*   **Docker & Docker Compose:** (Required for the recommended Docker-based setup).

## 2. Core Technologies

*   **Backend Framework:** Spring Boot (utilizing Spring Web, Spring Data JPA, Spring Security)
*   **Programming Language:** Java 21
*   **Database:** PostgreSQL
*   **Authentication:** JSON Web Tokens (JWT)
*   **File Parsing:** Apache PDFBox (for PDF content extraction)
*   **Build Tool:** Apache Maven
*   **Utility:** Lombok

## 3. Configuration

Primary application settings are located in `src/main/resources/application.properties`.

*   **Database Connection:**
    ```properties
    spring.datasource.url=jdbc:postgresql://db:5432/lostfounddb # For Docker Compose: 'db' is the service name
    # For local PostgreSQL: jdbc:postgresql://localhost:5432/lostfounddb
    spring.datasource.username=admin
    spring.datasource.password=admin123
    ```
    Adjust these settings if using an external PostgreSQL instance.

*   **JWT Configuration:**
    ```properties
    jwt.secret=YourStrongUniqueSecretKeyHere!ChangeThisValue!
    jwt.expiration.ms=3600000 # Default: 1 hour
    ```
    **Security Note:** For production, `jwt.secret` should be managed securely (e.g., via environment variables or a secrets manager) and not hardcoded.

## 4. Building the Project

To compile the application and create an executable JAR:

1.  Navigate to the project's root directory (where `pom.xml` is).
2.  Run the Maven command:
    ```bash
    mvn clean install
    ```
    This will create a JAR file (e.g., `lost-found-0.0.1-SNAPSHOT.jar`) in the `target/` directory.

## 5. Running the Application

You have two main options to run the application:

### Option 1: Using Docker Compose (Recommended)

This method runs both the application and its PostgreSQL database in Docker containers.

1.  **Start with Docker Compose:**
    From the project root, run:
    ```bash
    docker-compose up --build
    ```
    The application will be available at `http://localhost:8080`.
2. **To stop:** Press `Ctrl+C`, or if running in the background (`-d` flag), use `docker-compose down`.

### Option 2: Locally with Maven (Requires External PostgreSQL)

1.  **Ensure PostgreSQL is running and configured** in `src/main/resources/application.properties`.
2.  **Build the JAR (if you haven't already):**
    ```bash
    mvn clean install
    ```