# Parallax Parking Demo

A small, end-to-end demo system for managing vehicle registrations and blacklist checks for a campus / facility parking environment.

This repository contains:

- A polished, Apple-inspired front-end (HTML/CSS/JavaScript).
- A lightweight Java HTTP backend (no frameworks) currently using in-memory repositories behind well-defined interfaces.
- A planned integration with SQLite for persistence.
- A planned Python microservice for license-plate recognition.

> **NOTE:** This is a demo / prototype system, not production software.

---

## 1. High-Level Overview

Parallax provides:

- **Account management**
  - User registration (email + phone, country/region, basic profile).
  - Login with email or phone number + password.
  - My Account: update email, phone, and password with verification steps.
- **Vehicle management**
  - Register one or more vehicles (plate, manufacturer, model, year).
  - View and remove registered vehicles.
- **Blacklist**
  - Mark a vehicle as blacklisted / remove from blacklist (admin only).
  - Query a plate's blacklist status via a public "Query" page.
- **Extensibility hooks**
  - Data access is abstracted via `UserRepository` and `VehicleRepository` interfaces, ready for a SQLite implementation.
  - Future Python microservice for image-based plate recognition over HTTP.

---

## 2. Architecture

### 2.1 Components

- **Front-end (static)**
  - Located under `frontend/` (or project root depending on your structure).
  - Files:
    - `index.html`: Main UI (sign-in, registration, My Vehicles, My Account, Query).
    - `styles.css`: Apple-style UI theming.
    - `app.js`: All front-end logic (auth flows, navigation, form handling, API calls, local state).

- **Backend (Java HTTP server)**
  - Located under `backend/`.
  - Key packages:
    - `parallax.backend.http`
      - `HttpServerApp`: Boots the HTTP server and registers routes.
      - `*Handler` classes: HTTP handlers for auth, account, vehicles, query, health check, etc.
    - `parallax.backend.config`
      - `AppConfig`: Central configuration, including backend port, admin account credentials, and Python service URL.
    - `parallax.backend.db`
      - `UserRepository` / `VehicleRepository`: Interfaces for data access.
      - `InMemoryUserRepository` / `InMemoryVehicleRepository`: Current in-memory implementations (backed by maps, no DB).

- **Planned external services**
  - **SQLite**: Will replace the in-memory repositories with a persistent implementation (e.g., `SQLiteUserRepository`, `SQLiteVehicleRepository`) behind the existing interfaces.
  - **Python plate-recognition microservice**:
    - A standalone HTTP service (e.g., `POST /recognize`) that receives an uploaded image and returns a detected license plate string.
    - Java backend calls this service and then checks blacklist status.

### 2.2 Data Model (Conceptual)

**User**

- `username` (primary key, normalized email)
- `email`
- `password` (hashed or plain text in demo)
- `firstName`, `lastName`, `displayName`
- `country` (ISO region code)
- `birthDate` (month/day/year)
- `phoneCountry` (e.g. `+1`)
- `phone` (digits only)
- `contactMethod` (`text` or `call`)
- `createdAt`

**Vehicle**

- `ownerUsername` (foreign key to User)
- `licenseNumber` (normalized plate string)
- `make` (manufacturer)
- `model`
- `year`
- `blacklisted` (boolean)
- `createdAt`

---

## 3. Features in Detail

### 3.1 Authentication & Accounts

- **Sign-in**
  - 2-stage flow: identifier (email or phone), then password.
  - Front-end only stores a simple session token in `localStorage`:
    - `ft_session` → `{ "username": "<user-email>" }`.
  - Back-end validates credentials on each request requiring authentication.

- **Registration**
  - Captures:
    - Country/Region (from a curated list).
    - First name / Last name.
    - Birth date (Month/Day/Year with dynamic day options).
    - Email (with validation).
    - Password (with strength rules, e.g. 8–20 chars, mixed case + digit).
    - Phone (country code + number).
    - Preferred verification method.
  - Server-side ensures email and phone are unique.

- **My Account**
  - Update email and phone (requires current password).
  - Update password (requires current password and captcha).
  - Delete account (removes user and their vehicles from the current persistence layer, except admin account).
  - Admin account is configured in `AppConfig` and:
    - Cannot be deleted from the front-end.
    - May be disabled / enabled via configuration.

### 3.2 Vehicles & Blacklist

- **My Vehicles**
  - Add vehicle:
    - License plate: 1–7 chars `[A-Z0-9-]`.
    - Manufacturer: from a curated list (Toyota, Honda, BMW, etc.).
    - Model: dynamic, based on selected manufacturer.
    - Year: range (e.g. 1980 → current year).
  - List vehicles owned by the current user.
  - Remove vehicle (owner only).

- **Admin: Manage Vehicles**
  - Admin login (configured email + password in `AppConfig`).
  - "Registered vehicles" table shows:
    - Plate
    - Manufacturer
    - Model
    - Year
    - Blacklist Status
    - Owner email and phone
  - Actions (admin only):
    - **Blacklist** / **Remove from blacklist** per vehicle.
  - Filter / search (front-end) across table columns (e.g. plate, user email, manufacturer).

### 3.3 Query

- **Plate Query (text input)**
  - Anyone (or only authenticated users, depending on your policy) can:
    - Enter a license plate number.
    - System responds with:
      - Whether the plate exists in the system.
      - Whether it is blacklisted.
    - Response is intentionally minimal (no owner or sensitive details).

- **Plate Query (image upload – planned)**
  - Additional module on the Query page:
    - Upload an image (e.g. `jpeg`, `png`).
    - Backend sends the image to the Python recognition service.
    - Python returns detected plate text.
    - Java backend then:
      - Runs the same blacklist check for the detected plate.
      - Returns: recognized plate text + blacklist status.
  - Designed to be fully HTTP-based:
    - Java ↔ Python over a configurable URL (read from `AppConfig`).

---

## 4. Technology Stack

- **Front-end**
  - Pure HTML5, CSS3, vanilla JavaScript.
  - Apple-inspired design (typography, gradients, minimal layout).
  - No bundler or framework required.

- **Back-end**
  - Java 17+ (recommended).
  - `com.sun.net.httpserver.HttpServer` for lightweight HTTP.
  - Gson for JSON serialization.
  - Modular handlers for each logical area (auth, vehicles, query, etc.).

- **Data Access**
  - `UserRepository` / `VehicleRepository` interfaces abstract persistence.
  - `InMemoryUserRepository` / `InMemoryVehicleRepository` as current implementations.
  - Future: drop-in SQLite implementations without touching HTTP handlers.

- **Planned external**
  - SQLite via a lightweight JDBC integration.
  - Python microservice (FastAPI / Flask or equivalent) for plate recognition.

---

## 5. Getting Started

### 5.1 Prerequisites

- Java 17 or above.
- Maven 3.x.
- A static HTTP server for the front-end (or simply open `index.html` from disk during early development).

Optional (future):

- SQLite installed locally.
- Python 3.x + required libraries for the recognition microservice.

### 5.2 Run the Backend

From the `backend/` directory:

```bash
mvn clean package
java -cp target/parallax-backend-1.0.0.jar parallax.backend.http.HttpServerApp
```

By default the server listens on a configured port (e.g. `8080`).

The `/api/health` endpoint should respond with a JSON health object:

```json
{ "status": "ok" }
```

### 5.3 Run the Front-end

For a quick test:

1. Open `frontend/index.html` in a modern browser, **or**

2. Serve the front-end via a static HTTP server, for example:

   ```bash
   cd frontend
   python -m http.server 8000
   ```

3. Open `http://localhost:8000/` in the browser.

Make sure the backend base URL configured in the front-end (e.g. in `app.js` or a small `config.js`) matches the actual backend URL (`http://localhost:8080` during development).

---

## 6. Configuration

Configuration is centralized in the backend (e.g. `AppConfig` class and/or `config.properties` file). Typical configuration values:

- **Server**
  - `server.port` (e.g. `8080`).

- **Admin account**
  - `admin.email`
  - `admin.password`
  - `admin.enabled` (true/false)

- **Python recognition service**
  - `plateRecognition.baseUrl` (e.g. `http://localhost:5000`)
  - Endpoint path (e.g. `/recognize`).

- **CORS / Security (optional)**
  - Allowed origins.
  - Basic rate-limiting and logging config (TBD).

The front-end can consume the backend base URL either:

- By hard-coding it in `app.js` for a simple deployment, or
- By loading a small JSON/JS config file (for flexible environments).

---

## 7. API Overview (Simplified)

Actual paths may differ; this section describes the intended shape.

### 7.1 Authentication & Account

- `POST /auth/register`  
  Create a user account.

- `POST /auth/login`  
  Authenticate user and create a session (stateless in demo; front-end stores username).

- `GET /account/me`  
  Get account details for the current user.

- `PUT /account/contact`  
  Update email and phone (requires current password).

- `PUT /account/password`  
  Change password (requires current password + captcha).

- `DELETE /account`  
  Delete current account (admin account excluded by config).

### 7.2 Vehicles

- `GET /vehicles`  
  List current user's vehicles (or all vehicles if admin).

- `POST /vehicles`  
  Register a new vehicle for current user.

- `DELETE /vehicles`  
  Remove a vehicle for current user (plate specified via body or query).

- `POST /vehicles/blacklist`  
  (Admin) Change blacklist status for a given vehicle.

  ```json
  {
    "username": "user@example.com",
    "licenseNumber": "ABC1234",
    "blacklisted": true
  }
  ```

### 7.3 Query

- `GET /vehicles/query?license=ABC1234`  
  Returns plate existence and blacklist status only (no PII).

- (Planned) `POST /vehicles/query/image`
  - Multipart form data: image file.
  - Backend calls Python service to detect plate text.
  - Response includes recognized plate and blacklist status.

---

## 8. Development Workflow

Recommended workflow:

1. **Front-end iteration**
   - Prototype UI/UX in `index.html`, `styles.css`, and `app.js`.
   - Use fake data / local state while backend is unavailable.

2. **Backend contracts**
   - Define repository interface methods and HTTP handler contracts first.
   - Keep the HTTP layer stable as you replace in-memory repositories with DB-backed ones.

3. **Persistence evolution**
   - Implement `SQLiteUserRepository` / `SQLiteVehicleRepository`.
   - In `HttpServerApp`, swap `new InMemory…` for `new SQLite…`.

4. **Integration with Python service**
   - Agree on JSON contract between Java and Python.
   - Implement a dedicated handler for image upload.
   - Configure the Python service URL in `AppConfig`.

5. **Hardening**
   - Add input validation on both front-end and back-end.
   - Add logging, error handling, and basic security measures (rate limiting, etc. if needed).

---

## 9. Roadmap

Planned enhancements:

- Full SQLite persistence.
- Proper password hashing and secure credential storage.
- Fine-grained authorization (user vs admin capabilities).
- Production-ready deployment (reverse proxy via Nginx, HTTPS, environment-based config).
- i18n support (localization of UI text and region lists).
- Extended reporting for admin (stats, usage, blacklist history).

---

## 10. License

This project is currently distributed as a demo.

Add your chosen license here (e.g. MIT, Apache-2.0) once finalized.
