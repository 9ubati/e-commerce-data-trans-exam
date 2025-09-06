# UserMS

User management + simple auth (login, token, introspection).  
**Database:** `dt_ec_user_ms`

## Prerequisites
- Java 21, Maven 3.9+
- MySQL DB: `dt_ec_user_ms`

Create DB:
```sql
CREATE DATABASE IF NOT EXISTS dt_ec_user_ms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


Build & Run
mvn -DskipTests clean package
java -jar target/UserMS-*.jar
# or: mvn spring-boot:run
