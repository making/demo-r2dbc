package lol.maki.demo.todo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("todos")
public class TodoController {
    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping(path = "")
    public Flux<Todo> getTodos() {
        return this.todoRepository.findAll();
    }

    @GetMapping(path = "/{todoId}")
    public Mono<ResponseEntity<Todo>> getTodo(@PathVariable("todoId") String todoId) {
        return this.todoRepository.findById(todoId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(path = "")
    public Mono<ResponseEntity<Todo>> postTodos(@RequestBody Todo todo, UriComponentsBuilder builder) {
        todo.setTodoId(UUID.randomUUID().toString());
        todo.setCreatedAt(LocalDateTime.now());
        return this.todoRepository.create(todo)
                .map(created -> {
                    final URI uri = builder.pathSegment("todos", created.getTodoId()).build().toUri();
                    return ResponseEntity.created(uri).body(created);
                });
    }

    @PutMapping(path = "/{todoId}")
    public Mono<ResponseEntity<Todo>> putTodo(@PathVariable("todoId") String todoId, @RequestBody Todo todo) {
        return this.todoRepository.findById(todoId)
                .flatMap(t -> {
                    if (todo.getTodoTitle() != null) {
                        t.setTodoTitle(todo.getTodoTitle());
                    }
                    if (!Objects.equals(todo.isFinished(), t.isFinished())) {
                        t.setFinished(todo.isFinished());
                    }
                    t.setUpdatedAt(LocalDateTime.now());
                    return this.todoRepository.updateById(t);
                })
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @DeleteMapping(path = "/{todoId}")
    public Mono<ResponseEntity<Void>> deleteTodo(@PathVariable("todoId") String todoId) {
        return this.todoRepository.deleteById(todoId)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }
}
