# 🏥 Hospital Management System — API Testing Guide

## Service Ports

| Service        | Port  | URL                              |
|---------------|-------|----------------------------------|
| Eureka Server | 8761  | http://localhost:8761            |
| API Gateway   | 8080  | http://localhost:8080 ← **use this** |
| Doctor Service| 8081  | http://localhost:8081 (direct)   |
| Auth Service  | 8082  | http://localhost:8082 (direct)   |
| Patient Service| 8083 | http://localhost:8083 (direct)   |

> ✅ **Always use the API Gateway (port 8080)** — it handles JWT validation and routes to services.

---

## 📋 Prerequisites

Before testing, make sure these are running:

1. **MySQL** on port `3306` (databases: `hms_auth`, `hms_doctor`)
2. **PostgreSQL** on port `5432` (database: `hms_patient`)
3. **RabbitMQ** on port `5672` (default guest/guest)
4. **Start services in this order:**
   ```
   1. eureka-server   (port 8761)
   2. auth-service    (port 8082)
   3. doctor-service  (port 8081)
   4. patient-service (port 8083)
   5. api-gateway     (port 8080)
   ```

---

## 🧪 Option A — Swagger UI (Easiest)

Each service has built-in Swagger. Access directly (bypass gateway):

| Service         | Swagger URL                                      |
|----------------|--------------------------------------------------|
| Auth Service    | http://localhost:8082/swagger-ui.html            |
| Doctor Service  | http://localhost:8081/swagger-ui.html            |
| Patient Service | http://localhost:8083/swagger-ui.html            |

**Steps in Swagger:**
1. Go to the Auth service Swagger
2. POST `/api/v1/auth/login` with `admin@hospital.com` / `admin123`
3. Copy the `accessToken` from the response
4. Click the **Authorize 🔒** button (top right)
5. Paste `Bearer <your_token>` → click **Authorize**
6. Now all protected endpoints work

---

## 🧪 Option B — Postman Collection (Recommended)

A ready-to-use collection file is included: `HMS_API_Tests.postman_collection.json`

**Import steps:**
1. Open Postman
2. Click **Import** → drag `HMS_API_Tests.postman_collection.json`
3. Click **Run collection** (Collection Runner) to execute all tests in order

The collection auto-saves `ACCESS_TOKEN`, `REFRESH_TOKEN`, and `AUTH_USER_ID` between requests.

---

## 🧪 Option C — Manual cURL / Postman

### Step 1 — Login (get tokens)

```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@hospital.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken":  "eyJhbGci...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType":    "Bearer",
    "email":        "admin@hospital.com",
    "role":         "ADMIN",
    "id":           "1"
  }
}
```

Save both `accessToken` and `refreshToken`.

---

### Step 2 — Register a Patient (public, no token needed)

```bash
POST http://localhost:8080/api/v1/auth/register/patient
Content-Type: application/json

{
  "email":       "jane.doe@gmail.com",
  "firstName":   "Jane",
  "lastName":    "Doe",
  "password":    "secret123",
  "phone":       "0771234567",
  "address":     "123 Main St, Colombo",
  "gender":      "Female",
  "dateOfBirth": "1990-05-15",
  "bloodGroup":  "O+"
}
```

Expected: `201 Created`

---

### Step 3 — Register a Doctor (ADMIN token required)

```bash
POST http://localhost:8080/api/v1/auth/register/doctor
Content-Type: application/json
Authorization: Bearer <ADMIN_ACCESS_TOKEN>

{
  "email":          "dr.smith@hospital.com",
  "firstName":      "John",
  "lastName":       "Smith",
  "password":       "doctor123",
  "phone":          "0779876543",
  "specialization": "Cardiology"
}
```

Expected: `201 Created`

---

### Step 4 — Refresh Access Token (token rotation)

```bash
POST http://localhost:8080/api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response:** New `accessToken` + new `refreshToken` (old refresh token is invalidated).
> ⚠️ After refresh, the old refresh token is invalid. Always use the new one.

---

### Step 5 — Logout

```bash
POST http://localhost:8080/api/v1/auth/logout
Content-Type: application/json

{
  "refreshToken": "<your_refresh_token>"
}
```

Expected: `200 OK`. After logout the refresh token is deleted from DB.

---

### Step 6 — Get All Doctors

```bash
GET http://localhost:8080/api/v1/doctors
Authorization: Bearer <ACCESS_TOKEN>
```

---

### Step 7 — Get Doctor by Auth User ID

```bash
GET http://localhost:8080/api/v1/doctors/{authUserId}
Authorization: Bearer <ACCESS_TOKEN>
```

---

### Step 8 — Search Doctors by Specialization

```bash
GET http://localhost:8080/api/v1/doctors/search?specialization=Cardiology
Authorization: Bearer <ACCESS_TOKEN>
```

---

### Step 9 — Update Doctor (partial update — only send what you want to change)

```bash
PUT http://localhost:8080/api/v1/doctors/{authUserId}
Content-Type: application/json
Authorization: Bearer <ACCESS_TOKEN>

{
  "phone":          "0779999999",
  "specialization": "Neurology",
  "availability":   "{\"monday\": \"9:00-17:00\", \"tuesday\": \"9:00-17:00\"}"
}
```

---

### Step 10 — Get All Patients

```bash
GET http://localhost:8080/api/v1/patients
Authorization: Bearer <ACCESS_TOKEN>
```

---

### Step 11 — Dynamic Patient Search

```bash
GET http://localhost:8080/api/v1/patients/dynamic-search?bloodGroup=O%2B&isActive=true
Authorization: Bearer <ACCESS_TOKEN>
```

Available filters (all optional): `name`, `phone`, `email`, `bloodGroup`, `isActive`

---

### Step 12 — Update Patient

```bash
PUT http://localhost:8080/api/v1/patients/{authUserId}
Content-Type: application/json
Authorization: Bearer <ACCESS_TOKEN>

{
  "firstName":   "Jane",
  "lastName":    "Doe",
  "email":       "jane.doe@gmail.com",
  "phone":       "0771234567",
  "address":     "456 New Street, Colombo",
  "gender":      "Female",
  "dateOfBirth": "1990-05-15",
  "bloodGroup":  "O+"
}
```

---

## 🔐 Security Test Cases

| Test                                   | Expected |
|----------------------------------------|----------|
| Login with wrong password              | `401`    |
| Register doctor without any token      | `401`    |
| Register doctor with a PATIENT token   | `403`    |
| Access `/doctors` without token        | `401`    |
| Access `/patients` with fake token     | `401`    |
| Refresh with already-used token        | `401/404`|
| Patient updating another patient       | `403`    |
| Doctor updating another doctor         | `400`    |

---

## 🔄 Token Flow Diagram

```
Login ──────────────────────────────────────────────────────────┐
  └── Returns: accessToken (15min/24h) + refreshToken (7 days)  │
                                                                 │
Use accessToken in Authorization header ─────────────────────── │
  └── "Authorization: Bearer <accessToken>"                      │
                                                                 │
When accessToken expires ────────────────────────────────────── │
  └── POST /auth/refresh { "refreshToken": "..." }               │
        └── Returns NEW accessToken + NEW refreshToken           │
              (old refreshToken is invalidated — token rotation) │
                                                                 │
Logout ─────────────────────────────────────────────────────────┘
  └── POST /auth/logout { "refreshToken": "..." }
        └── Deletes refreshToken from DB — forces re-login
```

---

## 📦 Environment Variables Required

Each service needs these set (via `.env` files already in place):

**auth-service/.env:**
```
JWT_SECRET=HOSPITAL_SECRET_KEY_HOSPITAL_SECRET_KEY
JWT_EXPIRATION=86400000
DB_USERNAME=root
DB_PASSWORD=root
```

**api-gateway/.env:**
```
JWT_SECRET=HOSPITAL_SECRET_KEY_HOSPITAL_SECRET_KEY
```

**doctor-service/.env:**
```
DB_USERNAME=root
DB_PASSWORD=root
```

**patient-service/.env:**
```
DB_USERNAME=postgres
DB_PASSWORD=root
```

> ⚠️ `JWT_SECRET` must be **identical** in both `auth-service` and `api-gateway`.
