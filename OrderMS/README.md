# OrderMS

Orders with order items (parent/child), soft delete, computed totals, and **header-based permission checks**.  
**Database:** `dt_ec_order_ms`

## Prerequisites
- Java 21, Maven 3.9+
- MySQL DB: `dt_ec_order_ms`
- Depends on shared library **OperationHelper** (for `PermissionUtil` & exceptions)

Create DB:
```sql
CREATE DATABASE IF NOT EXISTS dt_ec_order_ms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

Build & Run
mvn -DskipTests clean package
java -jar target/OrderMS-*.jar
# or: mvn spring-boot:run

How clients must send permissions
Option A – CSV

X-Permissions: add_order,edit_order,delete_order


Option B – JSON claims

X-Claims: {"permissions":["add_order","edit_order"]}