# Salary Calculation Service

A Spring Boot service to automatically calculate monthly salaries for employees, including proration, allowances, and unpaid leave deductions.

---

## 🚀 Features

- Monthly salary calculation with proration for partial-month employees
- Deduction for approved unpaid leaves
- Unique salary records per employee per month
- REST API endpoints to calculate and query salaries
- Liquibase database migrations
- Dockerized local environment (MySQL + App)
- Optional scheduled job for automatic monthly salary calculation

---

## 🛠 Requirements

- Docker & Docker Compose
- Java 17+ (only needed if running without Docker)
- Maven 3.6+

---

## ▶️ Running the Project

### **1. Build and Package**
Build the project and package the JAR (skip tests for faster build):

```bash
mvn clean package -DskipTests
```

### **2. Build Docker Containers (no cache)**

```bash
docker compose build --no-cache
```

### **3. Start Services**
```bash
docker compose up -d
```

### **4. Verify Containers**
```bash
docker ps
```
The application will be available at:

API Base URL: `http://localhost:8080`

MySQL: `localhost:3306`

## 📌 API Endpoints

### **Trigger Salary Calculation**
```bash
POST /salaries/calculate?month=YYYY-MM
```

### **Get Salaries for a Month**
```bash
GET /salaries?month=YYYY-MM
```

### **Get Salary History for an Employee**
```bash
GET /salaries/{employeeId}
```

## 📁 Project Structure

```text
src/main/java/com/gold/salarycalculation
│
├── controller        # REST controllers
├── entity            # JPA entities (Employee, LeaveRequest, Salary)
├── service           # Business logic & salary calculation
├── repository        # Spring Data JPA repositories
├── scheduler         # Scheduled salary calculation job
├── dto               # Response DTOs
├── enums             # Enums (EmployeeStatus, LeaveType, etc.)
├── exception         # Custom exceptions & global handler
└── util              # Helper classes
```
```text
src/main/resources/db/changelog
└── Liquibase migration files for tables and seed data
```

## 🕒 Optional Scheduled Job

Runs automatically on the last day of each month at 23:59:
```java
@Scheduled(cron = "59 23 L * *")
```
