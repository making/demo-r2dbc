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

## Enable TLS

Generate self-signed certificates

```
docker run --rm \
 -v ${PWD}/certs:/certs \
 gcr.io/paketo-buildpacks/run:base-cnb \
 sh /certs/generate-certs.sh
```

Run postgresql with the generated certificates

```
docker run --rm \
 -p 5432:5432 \
 -e POSTGRES_DB=demo \
 -e POSTGRES_USER=demo \
 -e POSTGRES_PASSWORD=demo \
 -e POSTGRESQL_ENABLE_TLS=yes \
 -e POSTGRESQL_TLS_CERT_FILE=/certs/server.crt \
 -e POSTGRESQL_TLS_KEY_FILE=/certs/server.key \
 -e POSTGRESQL_TLS_CA_FILE=/certs/root.crt \
 -e POSTGRESQL_PGHBA_REMOVE_FILTERS=hostssl \
 -v ${PWD}/certs:/certs \
 bitnami/postgresql:11.11.0-debian-10-r59
```

```
 java -jar target/demo-r2dbc-0.0.1-SNAPSHOT.jar --spring.r2dbc.url="r2dbc:postgresql://127-0-0-1.sslip.io:5432/demo?sslMode=VERIFY_FULL&sslRootCert=$(pwd)/certs/root.crt"
```