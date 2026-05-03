# Hospital Management System

A full-stack hospital management platform built with a **Spring Boot microservices** backend and a **React + TypeScript** frontend. The system covers patient registration, doctor management, appointment scheduling, payments, and email notifications — all secured with JWT and coordinated through RabbitMQ.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Services & Ports](#services--ports)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Local Setup (Without Docker)](#local-setup-without-docker)
- [Docker Setup](#docker-setup)
- [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
- [User Roles](#user-roles)
- [Key Flows](#key-flows)
- [Project Structure](#project-structure)
- [Default Credentials](#default-credentials)

---

## Architecture Overview

```
                        ┌─────────────────┐
                        │    Frontend      │
                        │  React + Vite    │
                        │  localhost:5173  │
                        └────────┬────────┘
                                 │ HTTP
                        ┌────────▼────────┐
                        │   API Gateway   │  ← JWT validation, routing
                        │   Port 8080     │
                        └────────┬────────┘
                                 │ lb:// (Eureka)
          ┌──────────────────────┼──────────────────────┐
          │              ┌───────┼───────┐               │
          ▼              ▼       ▼       ▼               ▼
    ┌──────────┐  ┌──────────┐ ...  ┌──────────┐  ┌──────────┐
    │  Auth    │  │  Doctor  │      │ Payment  │  │  Notif.  │
    │  :8082   │  │  :8081   │      │  :8087   │  │  :8086   │
    └──────────┘  └──────────┘      └──────────┘  └──────────┘
          │              │                │               ▲
          └──────────────┴────────────────┘               │
                         │ RabbitMQ events                 │
                  ┌──────▼──────┐                         │
                  │  RabbitMQ   │─────────────────────────┘
                  │  Port 5672  │
                  └─────────────┘

  ┌──────────────────────────────────────────────────────┐
  │                   Service Registry                    │
  │              Eureka Server  Port 8761                 │
  └──────────────────────────────────────────────────────┘

  Databases:  MySQL :3306 (Auth, Doctor, Staff, Appointment, Payment)
              PostgreSQL :5432 (Patient, Notification)
```

---

## Tech Stack

### Backend
| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Cloud | Spring Cloud 2025 (Eureka, Gateway) |
| Messaging | RabbitMQ (AMQP) |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Payments | Stripe Java SDK (mock mode) |
| Email | Spring Boot Mail (JavaMailSender) |

### Frontend
| Layer | Technology |
|-------|-----------|
| Language | TypeScript |
| Framework | React 18 + Vite |
| Styling | Tailwind CSS |
| State | Zustand (auth store) |
| Data Fetching | TanStack React Query |
| HTTP Client | Axios |
| Icons | Lucide React |
| Routing | React Router v6 |

### Infrastructure
| Service | Technology |
|---------|-----------|
| Service Registry | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Message Broker | RabbitMQ |
| Primary DB | MySQL 8.0 |
| Secondary DB | PostgreSQL 15 |
| Containerization | Docker + Docker Compose |

---

## Services & Ports

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| `frontend` | 5173 / 3000 | — | React UI |
| `api-gateway` | 8080 | — | JWT validation, routing |
| `eureka-server` | 8761 | — | Service registry |
| `auth-service` | 8082 | MySQL `hms_auth` | Registration, login, JWT, password reset |
| `doctor-service` | 8081 | MySQL `hms_doctor` | Doctor profiles, availability |
| `patient-service` | 8083 | PostgreSQL `hms_patient` | Patient profiles |
| `staff-service` | 8084 | MySQL `hms_staff` | Staff management |
| `appointment-service` | 8085 | MySQL `hms_appointment` | Booking, scheduling |
| `notification-service` | 8086 | PostgreSQL `hms_notification` | Email notifications |
| `payment-service` | 8087 | MySQL `hms_payment` | Payments (Stripe mock) |
| `rabbitmq` | 5672 / 15672 | — | Message broker + management UI |

---

## Features

### Patient
- Self-registration and login
- Browse available doctors by specialization
- Book, view and cancel appointments
- Password reset via email link
- Pay for confirmed appointments (Stripe mock)
- View full payment history

### Doctor
- Login and manage own profile
- Set and update availability status
- View assigned appointments
- Confirm and complete appointments

### Admin
- Register doctors and staff
- Manage all patients, doctors, and staff
- View and manage all appointments
- **Billing panel** — full payment overview, revenue stats, issue refunds
- Deactivate accounts

### Staff
- View patients and appointments
- Assist with appointment management

### System
- JWT access tokens (24h) + refresh tokens (7d) with rotation
- Event-driven architecture via RabbitMQ (user creation, payment events)
- HTML email notifications for password reset and payment receipts
- Auto-create PostgreSQL databases on first run
- Swagger UI on every service (`/swagger-ui.html`)

---

## Prerequisites

Make sure the following are installed:

- **Java 17+** — [Download](https://adoptium.net/)
- **Node.js 18+** — [Download](https://nodejs.org/)
- **Maven 3.8+** — [Download](https://maven.apache.org/)
- **MySQL 8.0** — [Download](https://dev.mysql.com/downloads/)
- **PostgreSQL 15** — [Download](https://www.postgresql.org/download/)
- **RabbitMQ 3.x** — [Download](https://www.rabbitmq.com/download.html)
- **Docker + Docker Compose** *(for containerized setup)* — [Download](https://www.docker.com/)

---

## Local Setup (Without Docker)

### 1. Clone the repository

```bash
git clone https://github.com/SuwanthaDarshana/hospital-management-system.git
cd hospital-management-system
```

### 2. Start infrastructure services

Start MySQL, PostgreSQL, and RabbitMQ locally.

For PostgreSQL, create the notification database manually:
```sql
CREATE DATABASE hms_notification;
```
> All MySQL databases are auto-created on first run via `createDatabaseIfNotExist=true`.

### 3. Configure environment variables

Each service reads from its own `.env` file. Copy the examples and fill in your values:

```bash
cp auth-service/.env.example          auth-service/.env
cp api-gateway/.env.example           api-gateway/.env
cp doctor-service/.env.example        doctor-service/.env
cp patient-service/.env.example       patient-service/.env
cp staff-service/.env.example         staff-service/.env
cp appointment-service/.env.example   appointment-service/.env
cp notification-service/.env.example  notification-service/.env
cp payment-service/.env.example       payment-service/.env
```

See [Environment Variables](#environment-variables) for details on each file.

### 4. Start backend services

Start services **in order** (each service depends on Eureka and RabbitMQ being up):

```bash
# 1. Service registry
cd eureka-server && mvn spring-boot:run

# 2. Core services (open separate terminals)
cd auth-service        && mvn spring-boot:run
cd doctor-service      && mvn spring-boot:run
cd patient-service     && mvn spring-boot:run
cd staff-service       && mvn spring-boot:run
cd appointment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd payment-service     && mvn spring-boot:run

# 3. Gateway (last)
cd api-gateway && mvn spring-boot:run
```

### 5. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173)

---

## Docker Setup

The easiest way to run the entire stack.

```bash
# Build and start everything
docker-compose up --build

# Or run in background
docker-compose up --build -d
```

> **First run:** PostgreSQL will auto-run `postgres-init/create-databases.sql` to create `hms_notification`.

| URL | Service |
|-----|---------|
| http://localhost:3000 | Frontend |
| http://localhost:8080 | API Gateway |
| http://localhost:8761 | Eureka Dashboard |
| http://localhost:15672 | RabbitMQ Management (guest/guest) |

To stop:
```bash
docker-compose down
# To also remove volumes (wipes all data):
docker-compose down -v
```

---

## Environment Variables

### `auth-service/.env`
```env
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=change_me_to_a_long_random_secret_key   # min 32 chars
JWT_EXPIRATION=86400000                             # 24h in ms
FRONTEND_URL=http://localhost:5173                  # used in password reset links
```

### `api-gateway/.env`
```env
JWT_SECRET=change_me_to_a_long_random_secret_key   # must match auth-service
```

### `doctor-service/.env` / `staff-service/.env` / `appointment-service/.env`
```env
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

### `patient-service/.env`
```env
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password
```

### `notification-service/.env`
```env
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

# Set MAIL_MOCK=true to log emails to console instead of sending
MAIL_MOCK=true
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_16_char_gmail_app_password
MAIL_FROM=your_email@gmail.com
```

> **Gmail App Password:** myaccount.google.com → Security → 2-Step Verification → App passwords

### `payment-service/.env`
```env
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# Set STRIPE_MOCK=true for simulated payments (no Stripe account needed)
STRIPE_MOCK=true
STRIPE_SECRET_KEY=sk_test_your_stripe_test_key
```

---

## API Documentation

Every service exposes a Swagger UI at `http://localhost:<port>/swagger-ui.html`.

| Service | Swagger URL |
|---------|------------|
| Auth | http://localhost:8082/swagger-ui.html |
| Doctor | http://localhost:8081/swagger-ui.html |
| Patient | http://localhost:8083/swagger-ui.html |
| Staff | http://localhost:8084/swagger-ui.html |
| Appointment | http://localhost:8085/swagger-ui.html |
| Notification | http://localhost:8086/swagger-ui.html |
| Payment | http://localhost:8087/swagger-ui.html |

A Postman collection is also included: `HMS_API_Tests.postman_collection.json`

### Key Endpoints

```
POST   /api/v1/auth/register/patient     Public — patient self-registration
POST   /api/v1/auth/register/doctor      ADMIN only
POST   /api/v1/auth/register/staff       ADMIN only
POST   /api/v1/auth/login                Public
POST   /api/v1/auth/refresh              Public
POST   /api/v1/auth/forgot-password      Public
POST   /api/v1/auth/reset-password       Public

GET    /api/v1/doctors                   All roles
PUT    /api/v1/doctors/{authUserId}       ADMIN / own DOCTOR
PATCH  /api/v1/doctors/{authUserId}/availability

GET    /api/v1/patients                  ADMIN / STAFF / DOCTOR
GET    /api/v1/patients/{authUserId}      Authenticated

POST   /api/v1/appointments              PATIENT
GET    /api/v1/appointments              ADMIN / STAFF
PATCH  /api/v1/appointments/{id}/status  DOCTOR / ADMIN

POST   /api/v1/payments/create-intent    PATIENT
POST   /api/v1/payments/confirm          PATIENT
GET    /api/v1/payments                  ADMIN (billing panel)
GET    /api/v1/payments/patient/{id}     PATIENT
POST   /api/v1/payments/{id}/refund      ADMIN
```

---

## User Roles

| Role | Description |
|------|-------------|
| `ADMIN` | Full system access — manages doctors, staff, patients, billing |
| `DOCTOR` | Views own appointments, manages own availability and profile |
| `PATIENT` | Books appointments, manages own profile, makes payments |
| `STAFF` | Assists with patients and appointments, limited edit access |

---

## Key Flows

### User Registration & Profile Sync (RabbitMQ)
```
Frontend → POST /auth/register/patient
  → auth-service creates User in hms_auth DB
  → publishes PatientCreatedEvent to patient.exchange
  → patient-service consumes event → creates Patient profile in hms_patient DB
```

### Password Reset
```
Frontend → POST /auth/forgot-password (email)
  → auth-service generates UUID token (15 min expiry)
  → publishes PasswordResetEvent to password.reset.exchange
  → notification-service sends HTML email with reset link
  → User clicks link → POST /auth/reset-password (token + new password)
```

### Payment Flow (Mock Stripe)
```
Patient confirms appointment is CONFIRMED
  → POST /payments/create-intent (mock pi_mock_... ID generated)
  → POST /payments/confirm
    → payment marked COMPLETED
    → PaymentCompletedEvent published to payment.completed.exchange
    → notification-service sends payment receipt email
```

### JWT Authentication
```
Login → access token (24h) + refresh token (7d, UUID, stored in DB)
All requests → api-gateway validates JWT
  → injects X-User-Email and X-User-Role headers downstream
  → services read headers via GatewayHeaderAuthFilter
Refresh → old token revoked, new pair issued (token rotation)
```

---

## Project Structure

```
hospital-management-system/
├── api-gateway/              # Spring Cloud Gateway — JWT filter, routing
├── eureka-server/            # Netflix Eureka service registry
├── auth-service/             # Authentication, JWT, password reset
├── doctor-service/           # Doctor profiles, availability
├── patient-service/          # Patient profiles
├── staff-service/            # Staff management
├── appointment-service/      # Appointment booking & scheduling
├── notification-service/     # Email notifications (RabbitMQ consumer)
├── payment-service/          # Payments — mock Stripe integration
├── frontend/                 # React + TypeScript + Tailwind UI
│   └── src/
│       ├── api/              # Axios service clients
│       ├── components/       # Shared components (Layout, Sidebar, modals)
│       ├── pages/            # Route-level page components
│       ├── store/            # Zustand auth store
│       └── types/            # TypeScript interfaces
├── postgres-init/            # SQL scripts run on first PostgreSQL startup
├── docker-compose.yml        # Full stack orchestration
├── HMS_API_Tests.postman_collection.json
└── API_TESTING_GUIDE.md
```

---

## Default Credentials

An admin account is bootstrapped automatically on first run:

| Field | Value |
|-------|-------|
| Email | `admin@hospital.com` |
| Password | `admin123` |

> Change this password immediately in a production environment.

---

## RabbitMQ Exchanges

| Exchange | Routing Key | Publisher → Consumer |
|----------|-------------|----------------------|
| `doctor.exchange` | `doctor.created` | auth → doctor-service |
| `patient.exchange` | `patient.created` | auth → patient-service |
| `staff.exchange` | `staff.created` | auth → staff-service |
| `doctor.update.exchange` | `doctor.updated` | doctor-service → auth |
| `patient.update.exchange` | `patient.updated` | patient-service → auth |
| `staff.update.exchange` | `staff.updated` | staff-service → auth |
| `password.reset.exchange` | `password.reset` | auth → notification-service |
| `payment.completed.exchange` | `payment.completed` | payment-service → notification-service |
