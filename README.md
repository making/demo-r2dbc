# Demo R2DBC

## Usage

```
brew install mysql
brew services start mysql
mysql -u root -e 'create database demo_r2dbc;'
```

```
TODO_ID=$(curl -s localhost:8080/todos -H "Content-Type: application/json" -d '{"todoTitle": "Demo"}' | jq -r .todoId)
curl -s localhost:8080/todos
curl -s localhost:8080/todos/${TODO_ID}
curl -s -X PUT localhost:8080/todos/${TODO_ID} -H "Content-Type: application/json" -d '{"finished": "true"}'
curl -s -X DELETE localhost:8080/todos/${TODO_ID}
curl -s localhost:8080/todos
```