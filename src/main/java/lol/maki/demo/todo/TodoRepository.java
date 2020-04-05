package lol.maki.demo.todo;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
public class TodoRepository {
    private final Map<String, Todo> map = Collections.synchronizedMap(new LinkedHashMap<>());
    private final DatabaseClient databaseClient;

    public TodoRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Todo> findById(String todoId) {
        return this.databaseClient.select()
                .from(Todo.class)
                .matching(where("todo_id").is(todoId))
                .fetch()
                .one();
    }

    public Flux<Todo> findAll() {
        return this.databaseClient.select()
                .from(Todo.class)
                .orderBy(Sort.Order.asc("created_at"))
                .fetch()
                .all();
    }

    @Transactional
    public Mono<Todo> create(Todo todo) {
        return this.databaseClient.insert()
                .into(Todo.class)
                .using(todo)
                .then()
                .thenReturn(todo);
    }

    @Transactional
    public Mono<Todo> updateById(Todo todo) {
        return this.databaseClient.update()
                .table(Todo.class)
                .using(todo)
                .then()
                .thenReturn(todo);
    }

    @Transactional
    public Mono<Void> deleteById(String todoId) {
        return this.databaseClient.delete()
                .from(Todo.class)
                .matching(where("todo_id").is(todoId))
                .then();
    }

    Mono<Void> clear() {
        return this.databaseClient.execute("TRUNCATE TABLE todo")
                .then();
    }
}
