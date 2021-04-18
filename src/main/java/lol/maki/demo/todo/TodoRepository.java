package lol.maki.demo.todo;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Repository
public class TodoRepository {
    private final DatabaseClient databaseClient;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public TodoRepository(DatabaseClient databaseClient, R2dbcEntityOperations r2dbcEntityOperations) {
        this.databaseClient = databaseClient;
		this.r2dbcEntityOperations = r2dbcEntityOperations;
	}

    public Mono<Todo> findById(String todoId) {
        return this.r2dbcEntityOperations.selectOne(query(where("todo_id").is(todoId)), Todo.class);
    }

    public Flux<Todo> findAll() {
        return this.r2dbcEntityOperations.select(Query.empty().sort(Sort.by(Sort.Order.asc("created_at"))), Todo.class);
    }

    @Transactional
    public Mono<Todo> create(Todo todo) {
        return this.r2dbcEntityOperations.insert(todo);
    }

    @Transactional
    public Mono<Todo> updateById(Todo todo) {
        return this.r2dbcEntityOperations.update(todo);
    }

    @Transactional
    public Mono<Void> deleteById(String todoId) {
        return this.r2dbcEntityOperations.delete(Todo.class)
                .matching(query(where("todo_id").is(todoId)))
                .all()
				.then();
    }

    Mono<Void> clear() {
        return this.databaseClient.sql("TRUNCATE TABLE todo")
                .then();
    }
}
