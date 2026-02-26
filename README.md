"# FakeStore API Test Automation (RestAssured + TestNG + Maven)

Automated API tests for **https://fakestoreapi.com** using **Java + RestAssured + TestNG**.

## Prerequisites
- Java 17+
- Maven 3.9+

## How to run
```bash
mvn test
```

Override base URL (optional):
```bash
mvn test -DbaseUrl=https://fakestoreapi.com
```

## Project structure
- `src/test/java/com/openapistore/tests` – Test classes grouped by API domain (auth/products/carts/users)
- `src/main/java/com/openapistore/models` – POJOs for request/response serialization
- `src/test/resources/schemas` – JSON Schemas used with RestAssured JSON Schema Validator

## Notes on negative tests
FakeStore API is a public demo API and may not enforce strict validation for some negative cases.
Where applicable, tests accept a small set of **known deviations** (e.g., `400` vs `422`, or `404` vs `200` with empty object) and log a warning via TestNG `Reporter`." 
