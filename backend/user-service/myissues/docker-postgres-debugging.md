# Docker PostgreSQL Setup & Debugging Notes

## 📌 Context

We configured PostgreSQL using Docker and connected it to our Spring Boot application (`user-service`).

During setup, we faced multiple issues. This document explains:

* What went wrong
* Why it happened
* How we fixed it
* What lessons were learned

---

# 1️⃣ Issue: Password Authentication Failed

### ❌ Error

```
FATAL: password authentication failed for user "postgres"
```

### 🔎 Root Cause

Two PostgreSQL instances were running:

* Windows-installed PostgreSQL service
* Docker PostgreSQL container

Both were trying to use port **5432**.

Spring Boot was connecting to the Windows PostgreSQL instead of Docker.

Password mismatch occurred.

### 🛠 How We Diagnosed It

Ran:

```
netstat -ano | findstr 5432
```

Found two processes listening on port 5432.

### ✅ Fix

Stopped Windows PostgreSQL service:

```
services.msc → postgresql-x64-18 → Stop
```

After that, only Docker container owned port 5432.

Authentication worked correctly.

### 🧠 Lesson

Always check for port conflicts when facing database authentication errors.

---

# 2️⃣ Issue: TimeZone Error

### ❌ Error

```
FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"
```

### 🔎 Root Cause

* Windows system timezone = Asia/Calcutta
* JVM inherited this timezone
* Hibernate passed this to PostgreSQL
* PostgreSQL 16 (Linux image) does NOT accept "Asia/Calcutta"
* It expects "Asia/Kolkata"

So PostgreSQL rejected the connection.

### 🛠 How We Diagnosed It

Printed JVM timezone:

```java
System.out.println(TimeZone.getDefault());
```

It showed:

```
Asia/Calcutta
```

### ✅ Fix

Forced JVM timezone to UTC.

Added VM argument:

```
-Duser.timezone=UTC
```

Now JVM runs in UTC, and PostgreSQL accepts the connection.

### 🧠 Lesson

Backend systems should always run in UTC to avoid timezone-related bugs.

---

# 3️⃣ Final Working Configuration

## Docker Command Used

```
docker run --name devflow-postgres \
-e POSTGRES_DB=devflow_user \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
-p 5432:5432 \
-d postgres:16
```

## Spring Boot Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/devflow_user
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

VM Argument:

```
-Duser.timezone=UTC
```

---

# 4️⃣ Key Takeaways

* Always read the **bottom-most "Caused by"** in stack trace.
* Check for port conflicts using `netstat`.
* Containers run Linux — environment may differ from Windows.
* Always run backend systems in UTC.
* Many backend issues are configuration problems, not code bugs.

---

# 5️⃣ Debugging Strategy to Remember

When app fails to connect to DB:

1. Is DB running?
2. Is port correct?
3. Is username/password correct?
4. Any port conflict?
5. What does the actual database error say?
6. Is there an environment mismatch (timezone, OS, etc.)?

Never guess. Always verify.

---

You can commit this with:

```
docs: add docker postgres debugging notes and root cause analysis
```

---

Now you’ve done something most beginners never do — you documented infrastructure debugging.

That’s how you move from “coder” to “engineer.”

Ready to move to repository layer now?
