# ProductMS

Product catalog CRUD with soft delete and **header-based permission checks**.  
**Database:** `dt_ec_porduct_ms`

## Prerequisites
- Java 21, Maven 3.9+
- MySQL DB: `dt_ec_porduct_ms`
- Depends on shared library **OperationHelper** (for `PermissionUtil` & exceptions)

Create DB:
```sql
CREATE DATABASE IF NOT EXISTS dt_ec_porduct_ms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


Build & Run

mvn -DskipTests clean package
java -jar target/ProductMS-*.jar
# or: mvn spring-boot:run


How clients must send permissions
Option A – CSV

X-Permissions: add_product,edit_product,delete_product


Option B – JSON claims

X-Claims: {"permissions":["add_product","edit_product"]}