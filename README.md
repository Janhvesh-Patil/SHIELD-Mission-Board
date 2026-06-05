# SHIELD Mission Board

A full-stack mission management application built entirely from scratch — no web frameworks, no ORM, no shortcuts. The backend is a raw Java HTTP server built on `ServerSocket`, handling concurrent requests through a thread pool, parsing HTTP manually, and communicating with PostgreSQL directly over JDBC. The frontend is a single-page tactical dashboard that consumes the REST API.

**Live Demo:** https://shield-mission-board-frontend.onrender.com  
**Backend API:** https://shield-mission-board.onrender.com  
**Tech Stack:** Java · PostgreSQL · JDBC · HTML · Tailwind CSS · JavaScript · Docker · Render · Supabase

---

## Why this project exists

Most backend tutorials start with Spring Boot, which abstracts away everything that actually matters: how HTTP works at the socket level, how connections are managed across threads, how SQL gets executed safely. This project deliberately avoids every abstraction to build that understanding from the ground up.

Every HTTP request in this application travels through code I wrote — from the TCP connection being accepted, through manual header parsing, through a path-based router, down to a prepared statement executing against a real cloud database, and back out as a formatted HTTP response. No magic.

---

## Architecture

```
Browser (HTML/JS)
        |
        | HTTP/1.1 over TCP
        v
HttpServer.java          — ServerSocket accepts connections, submits to thread pool
HttpRequest.java         — Parses raw bytes: method, path, headers, body
Router.java              — Matches method + path segments to handler methods
        |
        +-- MissionHandler.java    — Request validation, response building
        +-- AgentHandler.java
        |
        +-- MissionDao.java        — SQL execution via JDBC PreparedStatements
        +-- AgentDao.java
        |
        +-- DatabaseConnection.java — Reads credentials from env vars or config file
        |
        v
PostgreSQL (Supabase)
```

The server runs an infinite accept loop on the main thread. Each accepted connection is handed to a fixed thread pool (`ExecutorService`) immediately, so the main thread never blocks on request processing. Ten threads handle concurrent clients in parallel.

---

## Features

**Mission management**
- Create missions with title, description, category, priority, and optional agent assignment
- View all missions in a Kanban board organized by status (Pending, Active, Complete)
- Update mission status independently of other fields
- Assign or reassign agents to existing missions
- Delete missions with confirmation

**Agent management**
- View all personnel in the database
- Enlist new agents directly from the dashboard

**API design**
- Seven REST endpoints over raw HTTP/1.1
- CORS headers on every response for browser compatibility
- PostgreSQL enum types enforced at the database level
- Proper HTTP status codes: 200, 201, 204, 400, 404, 500

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/missions` | Fetch all missions with agent JOIN |
| GET | `/missions/:id` | Fetch single mission |
| POST | `/missions` | Create mission |
| PATCH | `/missions/:id/status` | Update mission status |
| PATCH | `/missions/:id/agent` | Assign agent to mission |
| DELETE | `/missions/:id` | Delete mission |
| GET | `/agents` | Fetch all agents |
| POST | `/agents` | Enlist new agent |

**Create mission request body:**
```json
{
  "title": "Operation Nighthawk",
  "description": "Retrieve stolen HYDRA intel from Vienna safe house",
  "category": "intel_gathering",
  "priority": "high",
  "agentId": 2
}
```

**Update status request body:**
```json
{ "status": "active" }
```

Valid status values: `pending`, `active`, `complete`  
Valid priority values: `low`, `medium`, `high`  
Valid category values: `rescue`, `assassination`, `intel_gathering`, `monitoring`, `neutralization`, `et_response`, `recovery`

---

## Database Schema

```sql
CREATE TYPE mission_status   AS ENUM ('pending', 'active', 'complete');
CREATE TYPE mission_priority AS ENUM ('low', 'medium', 'high');
CREATE TYPE mission_category AS ENUM (
    'rescue', 'assassination', 'intel_gathering',
    'monitoring', 'neutralization', 'et_response', 'recovery'
);

CREATE TABLE agent (
    agent_id   SERIAL PRIMARY KEY,
    agent_name VARCHAR(100) NOT NULL
);

CREATE TABLE mission (
    mission_id  SERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL UNIQUE,
    description TEXT,
    status      mission_status   NOT NULL DEFAULT 'pending',
    priority    mission_priority NOT NULL DEFAULT 'medium',
    category    mission_category NOT NULL,
    agent_id    INTEGER REFERENCES agent(agent_id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Running locally

**Prerequisites:** Java 21+, Maven, PostgreSQL

**1. Clone the repository**
```bash
git clone https://github.com/Janhvesh-Patil/SHIELD-Mission-Board.git
cd SHIELD-Mission-Board
```

**2. Set up the database**

Create a PostgreSQL database, then run the schema and seed files:
```bash
psql -U your_user -d your_database -f schema.sql
psql -U your_user -d your_database -f seed.sql
```

**3. Configure credentials**

Copy the example config and fill in your database details:
```bash
cp backend/src/main/resources/config.properties.example \
   backend/src/main/resources/config.properties
```

```properties
db.url=jdbc:postgresql://localhost:5432/your_database
db.user=your_user
db.password=your_password
```

**4. Build and run**
```bash
cd backend
mvn clean package -DskipTests
java -jar target/SHIELD-Mission-Board-1.0-SNAPSHOT.jar
```

Server starts on port 9090 by default. Set a `PORT` environment variable to override.

**5. Open the frontend**

Open `frontend/index.html` directly in a browser, or serve it with any static file server. The `API_BASE` constant at the top of the script points to `localhost:9090` for local development.

---

## Project structure

```
SHIELD-Mission-Board/
├── backend/
│   ├── src/main/java/org/shield/
│   │   ├── Main.java
│   │   ├── server/
│   │   │   ├── HttpServer.java       — ServerSocket loop, thread pool
│   │   │   ├── HttpRequest.java      — Raw HTTP parsing
│   │   │   ├── Router.java           — Path-based routing
│   │   │   └── HttpResponse.java     — Response formatting
│   │   ├── handler/
│   │   │   ├── MissionHandler.java   — Request validation, Gson serialization
│   │   │   └── AgentHandler.java
│   │   ├── dao/
│   │   │   ├── MissionDao.java       — All mission SQL
│   │   │   └── AgentDao.java
│   │   ├── model/
│   │   │   ├── Mission.java
│   │   │   ├── Agent.java
│   │   │   └── Mission*.java         — Enum types
│   │   └── db/
│   │       └── DatabaseConnection.java
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   └── index.html                    — Single-page dashboard
├── schema.sql
├── seed.sql
└── README.md
```

---

## Deployment

The backend is containerized with Docker and deployed on Render as a Web Service. Database credentials are injected as environment variables at runtime — no credentials exist in the codebase or repository.

The frontend is a single HTML file deployed on Render as a Static Site. No build step required.

```
Render Static Site  →  Render Web Service (Docker)  →  Supabase (PostgreSQL)
frontend/index.html    SHIELD-Mission-Board.jar         Cloud PostgreSQL
```

On deployment, the Java server reads the `PORT` environment variable assigned by Render and starts on that port. `DatabaseConnection.java` checks for `DB_URL`, `DB_USER`, and `DB_PASSWORD` environment variables first, falling back to `config.properties` for local development.

---

## What this taught me

Building a backend without Spring Boot forced a clear understanding of what frameworks do. Every annotation in Spring Boot maps to something real: `@RequestMapping` is a router, embedded Tomcat is a `ServerSocket` loop, `@Autowired` is dependency injection, Spring Data is a DAO layer. Having built each piece manually, the framework becomes a tool rather than a black box.

The specific concepts this project covered in practice: TCP socket lifecycle, HTTP/1.1 request structure, thread safety with shared state, JDBC prepared statements and enum handling, Docker multi-stage builds, environment-based configuration, and CORS preflight handling.
