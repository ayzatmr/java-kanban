import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.manager.FileBackedTasksManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.manager.FileBackedTasksManager.loadFromFile;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final Path path = Paths.get("src", "main", "resources", "test.csv");


    @BeforeEach
    void beforeEach() {
        manager = loadFromFile(path.toString());
        LocalDateTime time1 = LocalDateTime.now().plusDays(1);
        LocalDateTime time3 = LocalDateTime.now().plusDays(5);
        Duration duration = Duration.ofSeconds(1);

        Task task = new Task("TASK 1", "DESCRIPTION 1", duration, time1);
        final int taskId = manager.addTask(task);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time3);
        int subtaskId = manager.addSubtask(subtask1);

        manager.getSubtaskById(subtaskId);
    }

    @AfterEach
    void afterEach() {
        manager.createEmptyFile(path.toString());
    }

    @Test
    void readAllTasksFromFile() {
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
    void appendTasksToExistingFile() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time3 = LocalDateTime.now().plusMinutes(20);
        Duration duration = Duration.ofMinutes(5);

        Task task = new Task("TASK 1", "DESCRIPTION 1", duration, time1);
        final int taskId = manager.addTask(task);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time3);
        int subtaskId = manager.addSubtask(subtask1);
        manager.getSubtaskById(subtaskId);

        List<Task> allTasks = manager.getAllTasks();
        List<Subtask> allSubtasks = manager.getAllSubtasks();
        List<Epic> epics = manager.getAllEpics();
        List<Task> history = manager.historyManager.getHistory();


        assertEquals(2, allTasks.size(), "Tasks size is incorrect");
        assertEquals(2, allSubtasks.size(), "Subtasks size is incorrect");
        assertEquals(2, epics.size(), "Epics size is incorrect");
        assertEquals(2, history.size(), "History size is incorrect");
    }

}
