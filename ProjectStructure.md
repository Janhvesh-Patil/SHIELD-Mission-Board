# SHIELD Mission Board — Project Structure Reference

## The Core Idea

This backend is a raw Java HTTP server. No Spring Boot. No frameworks. Every HTTP
request comes in as plain bytes over a socket, gets parsed manually, routed to the
right handler, processed against a PostgreSQL database via JDBC, and a JSON response
is written back over the same socket.

Understanding this project means understanding exactly what Spring Boot does for you
later — because you're doing it all by hand here.

---

## The Layered Architecture

```
HTTP Request
     ↓
 HttpServer          ← accepts the raw TCP connection
     ↓
 HttpRequest         ← parses raw bytes into a usable object
     ↓
 Router              ← decides which handler to call
     ↓
 MissionHandler      ← processes the request, builds the response
 HeroHandler
     ↓
 MissionDao          ← executes SQL against the database
 HeroDao
     ↓
 DatabaseConnection  ← provides the JDBC Connection object
     ↓
 PostgreSQL
```

Each layer only talks to the layer directly below it.
A Handler never touches DatabaseConnection directly.
A DAO never parses HTTP.
Violations of this rule make the code unmaintainable fast.

---

## Package Breakdown

### `org.shield` (root)

#### `Main.java`
The entry point. Contains the `main()` method.
Its only job is to instantiate `HttpServer` and call `.start()`.
Nothing else lives here.

```
Main.java
  └── creates HttpServer
  └── calls server.start()
```

---

### `org.shield.server`

This package knows everything about HTTP and nothing about missions or heroes.

#### `HttpServer.java`
- Creates a `ServerSocket` bound to a port (e.g. 8080)
- Runs an infinite loop: `accept()` → hand off to a thread → loop again
- Uses an `ExecutorService` (thread pool) so multiple requests can be handled concurrently
- Each accepted connection is passed to a `Runnable` that reads the request,
  calls the Router, and writes the response back

Key concepts used: `ServerSocket`, `ExecutorService`, `Executors.newFixedThreadPool()`

```
while (true) {
    Socket client = serverSocket.accept();
    threadPool.submit(() -> handleClient(client));
}
```

#### `HttpRequest.java`
- Takes the raw input stream from the socket
- Parses it into structured fields: method, path, headers, body
- A raw HTTP request looks like this over the wire:

```
POST /missions HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 97

{"title":"Operation Nighthawk","priority":"high","category":"rescue"}
```

- `HttpRequest` reads this and gives you clean fields:
    - `getMethod()`    → "POST"
    - `getPath()`      → "/missions"
    - `getHeader(key)` → "application/json"
    - `getBody()`      → the JSON string

#### `Router.java`
- Receives a parsed `HttpRequest`
- Matches the method + path combination to the right handler method
- Examples:
    - GET  + /missions        → MissionHandler.getAll()
    - POST + /missions        → MissionHandler.create()
    - PATCH + /missions/5/status → MissionHandler.updateStatus(5)
    - DELETE + /missions/5    → MissionHandler.delete(5)
- Returns the response string back to HttpServer to write to the socket
- Handles the case where no route matches → 404 response

---

### `org.shield.handler`

This package knows about HTTP requests and responses, and knows about DAOs.
It does not know about SQL or database connections.

#### `MissionHandler.java`
Contains one method per endpoint:

| Method | What it does |
|--------|-------------|
| `getAll()` | calls MissionDao.findAll(), serializes list to JSON, returns 200 |
| `getById(int id)` | calls MissionDao.findById(id), returns 200 or 404 |
| `create(String body)` | parses JSON body into Mission object, calls MissionDao.insert(), returns 201 |
| `updateStatus(int id, String body)` | parses new status from body, calls MissionDao.updateStatus(), returns 200 |
| `delete(int id)` | calls MissionDao.delete(id), returns 204 |

Each method returns a String — the full HTTP response including status line,
headers, and JSON body.

A response looks like:
```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 142

[{"missionId":1,"title":"Operation Nighthawk",...}]
```

Uses Gson to serialize Java objects → JSON strings.

#### `HeroHandler.java`
Simpler than MissionHandler. Likely just one method:
- `getAll()` → returns the list of all heroes as JSON (used to populate the
  hero dropdown in the frontend when assigning a hero to a mission)

---

### `org.shield.dao`

DAO = Data Access Object.
This package knows about SQL and the database. It does not know about HTTP.
Every database operation in the entire project lives in this package — nowhere else.

#### `MissionDao.java`
One method per database operation:

| Method | SQL it runs |
|--------|------------|
| `findAll()` | SELECT with JOIN to get hero name alongside mission |
| `findById(int id)` | SELECT ... WHERE mission_id = ? |
| `insert(Mission m)` | INSERT INTO mission (...) VALUES (?) |
| `updateStatus(int id, String status)` | UPDATE mission SET status = ? WHERE mission_id = ? |
| `delete(int id)` | DELETE FROM mission WHERE mission_id = ? |

All methods use `PreparedStatement` — never string concatenation for SQL.
String concatenation opens you to SQL injection attacks.

```java
// WRONG — never do this
String sql = "SELECT * FROM mission WHERE mission_id = " + id;

// RIGHT — always do this
String sql = "SELECT * FROM mission WHERE mission_id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, id);
```

#### `HeroDao.java`
One method:
- `findAll()` → returns List<Hero>

---

### `org.shield.db`

#### `DatabaseConnection.java`
The only class in the project that knows the database credentials.

- Reads `config.properties` from `src/main/resources/` using the classloader
- Calls `DriverManager.getConnection(url, user, password)`
- Returns a `Connection` object
- Every DAO calls `DatabaseConnection.getConnection()` at the start of each method

```java
public static Connection getConnection() throws SQLException {
    // reads config.properties
    // returns DriverManager.getConnection(...)
}
```

Why not store the connection as a static field and reuse it?
Because database connections can time out and go stale. Opening and closing
per-operation is safe and correct at this scale. Connection pooling
(HikariCP, c3p0) is the production answer — that's a Spring Boot conversation.

---

### `org.shield.model`

Plain Java classes. No logic. No database code. No HTTP code.
Just fields, a constructor, and getters/setters.
These are the objects that travel between layers.

#### `Mission.java`
Fields mirror the database columns:
```
int missionId
String title
String description
String status        (matches mission_status enum values)
String priority      (matches mission_priority enum values)
String category      (matches mission_category enum values)
Integer heroId       (Integer not int — can be null)
String heroName      (populated via JOIN, not a DB column on mission table)
String createdAt
```

#### `Hero.java`
```
int heroId
String heroName
```

---

## File Structure

```
SHIELD-Mission-Board/
├── src/
│   └── main/
│       ├── java/
│       │   └── org/shield/
│       │       ├── Main.java
│       │       ├── server/
│       │       │   ├── HttpServer.java
│       │       │   ├── HttpRequest.java
│       │       │   └── Router.java
│       │       ├── handler/
│       │       │   ├── MissionHandler.java
│       │       │   └── HeroHandler.java
│       │       ├── dao/
│       │       │   ├── MissionDao.java
│       │       │   └── HeroDao.java
│       │       ├── db/
│       │       │   └── DatabaseConnection.java
│       │       └── model/
│       │           ├── Mission.java
│       │           └── Hero.java
│       └── resources/
│           ├── config.properties          ← NEVER committed (in .gitignore)
│           └── config.properties.example ← committed, empty values
├── schema.sql    ← run once to create tables and types
├── seed.sql      ← run once to insert hero data
├── .gitignore
├── pom.xml
└── PROJECT_STRUCTURE.md  ← this file
```

---

## Request Lifecycle — Full Example

**Request:** `PATCH /missions/3/status` with body `{"status":"active"}`

```
1. HttpServer.java
   - serverSocket.accept() gets the connection
   - submits to thread pool

2. HttpRequest.java
   - reads raw bytes from socket input stream
   - parses: method="PATCH", path="/missions/3/status"
   - parses body: {"status":"active"}

3. Router.java
   - sees PATCH + path matching /missions/{id}/status pattern
   - extracts id=3 from path
   - calls MissionHandler.updateStatus(3, body)

4. MissionHandler.java
   - uses Gson to parse body → extracts status="active"
   - validates: is "active" a valid status value?
   - calls MissionDao.updateStatus(3, "active")
   - receives confirmation
   - builds HTTP 200 response string with updated mission JSON

5. MissionDao.java
   - calls DatabaseConnection.getConnection()
   - prepares: UPDATE mission SET status = ? WHERE mission_id = ?
   - sets parameters: "active", 3
   - executes

6. DatabaseConnection.java
   - reads config.properties
   - returns Connection to PostgreSQL

7. Response travels back up the chain
   - MissionHandler returns response string to Router
   - Router returns it to HttpServer
   - HttpServer writes it to socket output stream
   - Connection closed
```

---

## Key Concepts Practiced in This Project

| Concept | Where it appears |
|---------|-----------------|
| ServerSocket + threads | HttpServer.java |
| ExecutorService / thread pool | HttpServer.java |
| Manual HTTP parsing | HttpRequest.java |
| String routing logic | Router.java |
| JSON serialization | Handler classes (Gson) |
| JDBC + PreparedStatement | DAO classes |
| Properties file loading | DatabaseConnection.java |
| Foreign key JOIN queries | MissionDao.findAll() |
| PostgreSQL ENUMs | schema.sql + DAO validation |
| Separation of concerns | The entire architecture |

---

## What This Prepares You For

When you learn Spring Boot, you will recognise every piece:

| What you built manually | What Spring Boot gives you |
|------------------------|---------------------------|
| HttpServer + ServerSocket | Embedded Tomcat |
| Router.java | @RequestMapping, @GetMapping |
| HttpRequest parsing | @RequestBody, @PathVariable |
| DatabaseConnection.java | application.properties + DataSource |
| DAO classes | Spring Data JPA / JdbcTemplate |
| Handler response building | @ResponseBody, ResponseEntity |

You are not wasting time doing this manually.
You are building the intuition that makes Spring Boot make sense.