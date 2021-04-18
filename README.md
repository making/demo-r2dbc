# Demo R2DBC

Spring Boot + Spring Data R2DBC + TestContainers

## Usage

```
docker run --rm \
 -p 5432:5432 \
 -e POSTGRES_DB=demo \
 -e POSTGRES_USER=demo \
 -e POSTGRES_PASSWORD=demo \
 bitnami/postgresql:11.11.0-debian-10-r59
```

```
./mvnw clean package
java -jar target/demo-r2dbc-0.0.1-SNAPSHOT.jar
```

```
TARGET_URL=http://localhost:8080
TODO_ID=$(curl -s ${TARGET_URL}/todos -H "Content-Type: application/json" -d '{"todoTitle": "Demo"}' | jq -r .todoId)
curl -s ${TARGET_URL}/todos
curl -s ${TARGET_URL}/todos/${TODO_ID}
curl -s -X PUT ${TARGET_URL}/todos/${TODO_ID} -H "Content-Type: application/json" -d '{"finished": "true"}'
curl -s -X DELETE ${TARGET_URL}/todos/${TODO_ID}
curl -s ${TARGET_URL}/todos
```