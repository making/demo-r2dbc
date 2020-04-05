# Demo R2DBC

Spring Boot + Spring Data R2DBC + TestContainers

## Usage

```
brew install mysql
brew services start mysql
mysql -u root -e 'create database demo_r2dbc;'
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