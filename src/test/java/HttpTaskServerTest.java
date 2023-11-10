import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.*;
import service.manager.FileBackedTasksManager;
import service.server.HttpTaskServer;
import utils.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.manager.FileBackedTasksManager.loadFromFile;


public class HttpTaskServerTest extends TaskManagerTest<FileBackedTasksManager> {
    private static final Path path = Paths.get("src", "main", "resources", "test.csv");
    private static final String TASK_SERVER_URL = "http://localhost:8080";
    private static HttpTaskServer server;
    private static HttpClient client;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private int taskId;
    private int epicId;
    private int subtaskId;

    @BeforeAll
    static void beforeAll() throws IOException {
        server = new HttpTaskServer();
        server.start(path.toString());
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void beforeEach() {
        server.manager = loadFromFile(path.toString());
        LocalDateTime time1 = LocalDateTime.now().plusDays(1);
        LocalDateTime time3 = LocalDateTime.now().plusDays(5);
        Duration duration = Duration.ofSeconds(1);

        Task task = new Task("TASK 1", "DESCRIPTION 1", duration, time1);
        taskId = server.manager.addTask(task);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        epicId = server.manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time3);
        subtaskId = server.manager.addSubtask(subtask1);

        server.manager.getSubtaskById(subtaskId);
    }

    @AfterEach
    void afterEach() {
        server.manager.createEmptyFile(path.toString());
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<? extends Task> task = gson.fromJson(response.body(), listType);
        assertEquals(2, task.size(), "Tasks size is incorrect");
        assertEquals(taskId, task.get(0).getId(), "Tasks sorted incorrectly");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/task?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode(), "Task is not returned");
        assertEquals(taskId, task.getId(), "Task id is incorrect");
    }

    @Test
    void getNotExistingTaskById() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/task?id=" + 100);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Status code is wrong");
    }

    @Test
    void useWrongMethod() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/task?id=" + 100);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode(), "Status code is wrong");
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/task");

        Task task = new Task("TASK 1", "DESCRIPTION 1", Duration.ofSeconds(1), LocalDateTime.now().plusDays(2));
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Task is created");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/task");

        Task task = server.manager.getTaskById(taskId);
        task.setName("new name");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Task is updated");
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/task/?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode(), "Task is not deleted");
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode(), "Tasks is not deleted");
    }

    @Test
    void getEpicSubtasks() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/subtask/epic/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> task = gson.fromJson(response.body(), listType);
        assertEquals(200, response.statusCode(), "Subtasks has not been got");
        assertEquals(1, task.size(), "Tasks size is incorrect");
        assertEquals(subtaskId, task.get(0).getId(), "Subtask id is incorrect");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        URI url = URI.create(TASK_SERVER_URL + "/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> task = gson.fromJson(response.body(), listType);
        assertEquals(200, response.statusCode(), "History has not been got");
        assertEquals(1, task.size(), "Tasks size is incorrect");
        assertEquals(subtaskId, task.get(0).getId(), "Tasks sorted incorrectly");
    }
}