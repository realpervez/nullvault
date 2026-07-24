# Nullvault 🔐

A secure, zero-knowledge password manager built with Java and Spring Boot. Passwords are encrypted with AES-256-GCM before being stored — the server never has access to your plaintext credentials.

**Live Demo:** [nullvault.vercel.app](https://nullvault.vercel.app)

---

## What it does

- Register and log in with a master password
- Store passwords for any website or service
- Passwords are AES-256-GCM encrypted before hitting the database
- Master password is hashed with PBKDF2 (310,000 iterations) — never stored in plaintext
- JWT-based stateless authentication (HMAC-SHA256)
- View, add, decrypt, and delete saved credentials
- Response DTOs prevent sensitive fields from leaking via API

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4 |
| Security | Spring Security, JWT (JJWT 0.12.6) |
| Encryption | AES-256-GCM, PBKDF2 |
| Database | MySQL 8 |
| ORM | Hibernate / JPA |
| Build Tool | Maven |
| Deployment | Railway |

---

## Architecture

```
[ Browser / Frontend ]
	↓
[ JwtFilter ]        → validates token on every request
	↓
[ Controller ]       → handles HTTP requests
	↓
[ Service ]          → business logic + encryption
	↓
[ Repository ]       → database access via JPA
	↓
[ MySQL Database ]   → stores only encrypted ciphertext
```

---

## Security Design

**Zero-Knowledge Architecture**
The server never stores or sees your plaintext passwords. Every password is encrypted client-side with AES-256-GCM before being saved. Even a full database breach exposes only ciphertext.

**Master Password Hashing**
The master password is hashed using PBKDF2 with 310,000 iterations and a unique per-user salt. It is never stored in plaintext. The derived key is used for encryption — not the password itself.

**JWT Authentication**
Stateless authentication using JWT tokens signed with HMAC-SHA256. Tokens expire after 1 hour. The secret key is stored as an environment variable — never in source code.

**Response DTOs**
API responses never expose sensitive fields like `masterPasswordHash` or `salt` — only safe fields are returned to the client.

---

## Running Locally

### Prerequisites
- Java 17+
- MySQL 8
- Maven

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/realpervez/password-vault.git
cd password-vault
```

**2. Create the database**
```sql
CREATE DATABASE passwordvault;
```

**3. Set environment variables**
```bash
export DB_USERNAME=your_mysql_username
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=your-256-bit-secret-key-must-be-long-enough!!
```

Or set them in IntelliJ: Edit Configurations → Environment Variables.

**4. Run the app**
```bash
./mvnw spring-boot:run
```

**5. Open the frontend**

Open `frontend/index.html` directly in your browser.

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | None | Register new user |
| POST | `/auth/login` | None | Login, returns JWT |
| POST | `/vault/add` | JWT | Add encrypted entry |
| GET | `/vault/list` | JWT | List all entries |
| POST | `/vault/decrypt/{id}` | JWT | Decrypt a password |
| DELETE | `/vault/delete/{id}` | JWT | Delete an entry |
| GET | `/vault/search` | JWT | Search by site name |

---

## Project Structure
src/main/java/com/pervez/password_vault/

```
├── PasswordVaultApplication.java\
├── config/          # Security + CORS config
├── controller/      # HTTP request handlers
├── dto/             # Response DTOs
├── model/           # JPA entity classes
├── repository/      # Database queries
├── security/        # JWT filter + utility
└── service/         # Business logic + encryption
```

---

## Environment Variables

| Variable | Description |
|---|---|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | HMAC-SHA256 signing secret |

---

## Status

| Feature | Status |
|---|---|
| User registration + login | ✅ Done |
| AES-256-GCM encryption | ✅ Done |
| PBKDF2 password hashing | ✅ Done |
| Password vault CRUD | ✅ Done |
| JWT authentication | ✅ Done |
| Response DTOs | ✅ Done |
| Frontend UI | ✅ Done |
| Deployment | ✅ Live on Railway |

---

## Author

**Syed Pervez Mohiuddin**
[linkedin.com/in/realpervez](https://linkedin.com/in/realpervez) · [github.com/realpervez](https://github.com/realpervez)
