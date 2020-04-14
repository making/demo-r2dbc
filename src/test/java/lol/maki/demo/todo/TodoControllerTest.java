package lol.maki.demo.todo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TodoControllerTest {
    private final TodoRepository todoRepository;
    private final int port;
    private final WebTestClient webClient;
    private final LocalDateTime now = LocalDateTime.now();
    private final Todo todo1 = new Todo() {{
        setTodoId(UUID.randomUUID().toString());
        setTodoTitle("Todo1");
        setFinished(false);
        setCreatedAt(now);
    }};
    private final Todo todo2 = new Todo() {{
        setTodoId(UUID.randomUUID().toString());
        setTodoTitle("Todo2");
        setFinished(true);
        setCreatedAt(now.plusHours(1));
        setUpdatedAt(now.plusHours(2));
    }};

    public TodoControllerTest(@Autowired TodoRepository todoRepository, @LocalServerPort int port, @Autowired WebTestClient webClient) {
        this.todoRepository = todoRepository;
        this.port = port;
        this.webClient = webClient;
    }

    @BeforeEach
    void init() {
        this.todoRepository.clear().block();
        this.todoRepository.create(this.todo1).block();
        this.todoRepository.create(this.todo2).block();
    }

    @Test
    void checkHealth() {
        this.webClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class).isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    void getTodos_ok() {
        this.webClient.get().uri("/todos").exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .consumeWith(result -> {
                    final JsonNode body = result.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.size()).isEqualTo(2);
                    assertThat(body.get(0).get("todoId").asText()).isEqualTo(this.todo1.getTodoId());
                    assertThat(body.get(0).get("todoTitle").asText()).isEqualTo("Todo1");
                    assertThat(body.get(0).get("finished").asBoolean()).isFalse();
                    assertThat(body.get(0).get("createdAt").isNull()).isFalse();
                    assertThat(body.get(0).get("updatedAt").isNull()).isTrue();
                    assertThat(body.get(1).get("todoId").asText()).isEqualTo(this.todo2.getTodoId());
                    assertThat(body.get(1).get("todoTitle").asText()).isEqualTo("Todo2");
                    assertThat(body.get(1).get("finished").asBoolean()).isTrue();
                    assertThat(body.get(1).get("createdAt").isNull()).isFalse();
                    assertThat(body.get(1).get("updatedAt").isNull()).isFalse();
                });
    }

    @Test
    void getTodo_ok() {
        this.webClient.get().uri("/todos/{todoId}", this.todo1.getTodoId()).exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .consumeWith(result -> {
                    final JsonNode body = result.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.get("todoId").asText()).isEqualTo(this.todo1.getTodoId());
                    assertThat(body.get("todoTitle").asText()).isEqualTo("Todo1");
                    assertThat(body.get("finished").asBoolean()).isFalse();
                    assertThat(body.get("createdAt").isNull()).isFalse();
                    assertThat(body.get("updatedAt").isNull()).isTrue();
                });
    }

    @Test
    void getTodo_notFound() {
        this.webClient.get().uri("/todos/{todoId}", "xxxxxxxxxx").exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void postTodos_created() {
        this.webClient.post().uri("/todos")
                .bodyValue(Map.of("todoTitle", "Demo"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(JsonNode.class)
                .consumeWith(result -> {
                    final JsonNode body = result.getResponseBody();
                    final HttpHeaders headers = result.getResponseHeaders();
                    final String todoId = body.get("todoId").asText();
                    assertThat(todoId).isNotNull();
                    assertThat(body.get("todoTitle").asText()).isEqualTo("Demo");
                    assertThat(body.get("finished").asBoolean()).isFalse();
                    assertThat(body.get("createdAt").isNull()).isFalse();
                    assertThat(body.get("updatedAt").isNull()).isTrue();
                    assertThat(headers.getLocation()).isEqualTo(URI.create("http://localhost:" + port + "/todos/" + todoId));
                });
    }


    @Test
    void postTodo_ok() {
        this.webClient.put().uri("/todos/{todoId}", this.todo1.getTodoId())
                .bodyValue(Map.of("finished", true))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class)
                .consumeWith(result -> {
                    final JsonNode body = result.getResponseBody();
                    final HttpHeaders headers = result.getResponseHeaders();
                    final String todoId = body.get("todoId").asText();
                    assertThat(todoId).isNotNull();
                    assertThat(body.get("todoTitle").asText()).isEqualTo("Todo1");
                    assertThat(body.get("finished").asBoolean()).isTrue();
                    assertThat(body.get("createdAt").isNull()).isFalse();
                    assertThat(body.get("updatedAt").isNull()).isFalse();
                });
    }

    @Test
    void putTodo_notFound() {
        this.webClient.put().uri("/todos/{todoId}", "xxxxxxxxxx")
                .bodyValue(Map.of("finished", true))
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void deleteTodo_noContent() {
        this.webClient.delete().uri("/todos/{todoId}", this.todo1.getTodoId())
                .exchange()
                .expectStatus().isNoContent();
        StepVerifier.create(this.todoRepository.findById(this.todo1.getTodoId()))
                .verifyComplete();
    }
}
