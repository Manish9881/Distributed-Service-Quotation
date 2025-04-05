# 🧾 LifeCo REST - Distributed Quotation System

A REST-based distributed quotation system composed of multiple microservices. Each service provides insurance quotations based on specific criteria. The project uses Spring Boot for building RESTful APIs and Docker for containerized deployment.

---

## 📚 Table of Contents

- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [How to Run](#-how-to-run)
  - [Using Docker Compose](#using-docker-compose)
  - [Manual Local Execution](#manual-local-execution)
- [Service Registration](#-service-registration)
- [Verification](#-verification)
- [Known Issues](#-known-issues)

---

## 🔧 Tech Stack

- Java 11
- Spring Boot 2.2.4
- Maven
- Docker & Docker Compose
- OkHttp (REST client)
- Jackson (JSON serialization)
- Postman (API testing)

---

## 🗂️ Project Structure

```
Distributed Quotation Services/
├── core/             # Shared models and interfaces
├── auldfellas/       # Auldfellas Quotation Service
├── dodgygeezers/     # DodgyGeezers Quotation Service
├── girlsallowed/     # GirlsAllowed Quotation Service
├── broker/           # Broker for collecting quotations
├── client/           # REST client using OkHttp
├── docker-compose.yml
└── README.md
```

---

## ▶️ How to Run

### Prerequisites

- Git
- Java 11+
- Maven
- Docker & Docker Compose

---

### 📥 Clone the Repository

```bash
git clone https://github.com/Manish9881/Distributed-Service-Quotation.git
cd Distributed-Service-Quotation/Distributed Quotation Services
```

---

### 🐳 Using Docker Compose

This is the recommended approach for running all services together:

1. **Start all services:**

```bash
docker compose up --build
```

2. **In a new terminal, run the client:**

```bash
mvn exec:java -pl client
```

---

### 💻 Manual Local Execution

Alternatively, you can run each service in a separate terminal:

```bash
mvn compile spring-boot:run -pl auldfellas
mvn compile spring-boot:run -pl dodgygeezers
mvn compile spring-boot:run -pl girlsallowed
mvn compile spring-boot:run -pl broker
```

Once all services are up, run the client:

```bash
mvn exec:java -pl client
```

---

## 🔄 Service Registration

Each quotation service **auto-registers** with the broker using a POST request to:

```
POST /services
```

You can check all registered services by hitting:

```
GET /services
```

This enables dynamic discovery of quotation services at runtime.

---

## ✅ Verification

- All REST endpoints were verified using **Postman**.
- GET and POST endpoints for each service are tested.
- Final system verified with Docker Compose and manual execution.
- Sample request/response screenshots can be captured using Postman.

---

## ⚠️ Known Issues

- The client may pause for ~15 seconds and display warnings after execution.
- This is likely caused by the `Codehaus Mojo` plugin thread behavior.
- A possible workaround is to run the client as a standalone JAR, though some dependency issues might arise.

**More Info:**
- [OkHttp GitHub Issue #3957](https://github.com/square/okhttp/issues/3957)
- [StackOverflow Thread](https://stackoverflow.com/questions/77783739/thread-will-linger-despite-being-asked-to-die-via-interruption)

---

## 🧑‍💻 Author

Manish9881

