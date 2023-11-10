import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.*;
import service.manager.HttpTaskManager;
import service.server.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final String KV_SERVER_URL = "http://localhost:8078";
    private HttpTaskManager manager;
    private static KVServer kvServer;

    private int taskId;
    private int epicId;
    private int subtaskId;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    void beforeEach() {
        manager = new HttpTaskManager(KV_SERVER_URL);
        manager.loadFromServer();
        LocalDateTime time1 = LocalDateTime.now().plusDays(1);
        LocalDateTime time3 = LocalDateTime.now().plusDays(5);
        Duration duration = Duration.ofSeconds(1);

        Task task = new Task("TASK 1", "DESCRIPTION 1", duration, time1);
        taskId = manager.addTask(task);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time3);
        subtaskId = manager.addSubtask(subtask1);

        manager.getSubtaskById(subtaskId);
    }

    @AfterEach
    void afterEach() {
        manager.clearData();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }

    @Test
    void readAllTasksFromServer() {
        List<Task> allTasks = manager.getAllTasks();
        List<Subtask> allSubtasks = manager.getAllSubtasks();
        List<Epic> epics = manager.getAllEpics();

        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, allTasks.size(), "Tasks size is incorrect");
        assertEquals(1, allSubtasks.size(), "Subtasks size is incorrect");
        assertEquals(1, epics.size(), "Epics size is incorrect");
        assertEquals(1, history.size(), "History size is incorrect");
    }

    @Test
    void getTasksFromServer() {
        Task taskById = manager.getTaskById(taskId);
        Task epicById = manager.getEpicById(epicId);
        Task subtaskById = manager.getSubtaskById(subtaskId);
        assertEquals(taskId, taskById.getId(), "Task id is incorrect");
        assertEquals(epicId, epicById.getId(), "Epic id is incorrect");
        assertEquals(subtaskId, subtaskById.getId(), "Subtask id is incorrect");
    }

    @Test
    void deleteTasksFromServer() {
        manager.deleteTaskById(taskId);
        manager.deleteEpicById(epicId);
        manager.deleteSubtaskById(subtaskId);

        List<Task> allTasks = manager.getAllTasks();
        List<Subtask> allSubtasks = manager.getAllSubtasks();
        List<Epic> epics = manager.getAllEpics();
        List<Task> history = manager.historyManager.getHistory();

        assertEquals(0, allTasks.size(), "Tasks size is incorrect");
        assertEquals(0, allSubtasks.size(), "Subtasks size is incorrect");
        assertEquals(0, epics.size(), "Epics size is incorrect");
        assertEquals(0, history.size(), "History size is incorrect");
    }

    @Test
    void loadFromServer() {
        manager.loadFromServer();

        List<Task> allTasks = manager.getAllTasks();
        List<Subtask> allSubtasks = manager.getAllSubtasks();
        List<Epic> epics = manager.getAllEpics();
        manager.getSubtaskById(subtaskId);

        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, allTasks.size(), "Tasks size is incorrect");
        assertEquals(1, allSubtasks.size(), "Subtasks size is incorrect");
        assertEquals(1, epics.size(), "Epics size is incorrect");
        assertEquals(1, history.size(), "History size is incorrect");
        assertEquals(subtaskId, history.get(0).getId(), "History elements is incorrect");
    }
}