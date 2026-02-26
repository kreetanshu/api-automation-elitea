# OpenAPIStore API Automation (RestAssured + TestNG + Maven)

Automated functional API tests for **https://fakestoreapi.com** using **Java + RestAssured + TestNG**, including JSON Schema validation.

## Run
```bash
mvn test
```

### Override base URL
```bash
mvn test -Dapi.baseUrl=https://fakestoreapi.com
```

## Where tests live
- Tests: `src/test/java/com/openapistore/tests/**`
- Schemas: `src/test/resources/schemas/**`
- Models & utilities: `src/main/java/com/openapistore/**`
