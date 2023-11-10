import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.manager.InMemoryTaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void before() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addTaskToHistoryManager() {
        Task task = new Task("TASK 11", "DESCRIPTION 11");
        final int taskId = manager.addTask(task);

        manager.getTaskById(taskId);
        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, history.size(), "Tasks has not been added to history");
        assertEquals(taskId, history.get(0).getId(), "Task id is incorrect");
    }

    @Test
    void addEpicToHistoryManager() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        manager.getEpicById(epicId);
        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, history.size(), "Epic has not been added to history");
        assertEquals(epicId, history.get(0).getId(), "Epic id is incorrect");
    }

    @Test
    void addSubtaskToHistoryManager() {
        Epic epic1 = new Epic("EPIC 11", "EPIC DESCRIPTION 11");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 11", "SUBTASK DESCRIPTION 11", epicId);
        int subtaskId = manager.addSubtask(subtask1);
        manager.getSubtaskById(subtaskId);

        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, history.size(), "Subtask has not been added to history");
        assertEquals(subtaskId, history.get(0).getId(), "SubtaskId is incorrect");
    }

    @Test
    void checkDuplicates() {
        Task task = new Task("TASK 12", "DESCRIPTION 12");
        final int taskId = manager.addTask(task);

        for (int i = 0; i < 2; i++) {
            manager.getTaskById(taskId);
        }

        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, history.size(), "History has duplicates");
        assertEquals(taskId, history.get(0).getId(), "Task id is incorrect");
    }

    @Test
    void removeFromEndOfHistoryManager() {
        Task task = new Task("TASK 13", "DESCRIPTION 13");
        final int taskId1 = manager.addTask(task);

        Task task2 = new Task("TASK 21", "DESCRIPTION 21");
        final int taskId2 = manager.addTask(task2);

        for (Integer taskId : List.of(taskId1, taskId2)) {
            manager.getTaskById(taskId);
        }

        manager.historyManager.remove(taskId2);
        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, history.size(), "Task has not been deleted from history");
        assertEquals(taskId1, history.get(0).getId(), "Task id is incorrect");
    }

    @Test
    void removeFromTopOfHistoryManager() {
        Task task = new Task("TASK 13", "DESCRIPTION 13");
        final int taskId1 = manager.addTask(task);

        Task task2 = new Task("TASK 21", "DESCRIPTION 21");
        final int taskId2 = manager.addTask(task2);

        for (Integer taskId : List.of(taskId1, taskId2)) {
            manager.getTaskById(taskId);
        }

        manager.historyManager.remove(taskId1);
        List<Task> history = manager.historyManager.getHistory();

        assertEquals(1, history.size(), "Task has not been deleted from history");
        assertEquals(taskId2, history.get(0).getId(), "Task id is incorrect");
    }

    @Test
    void removeFromMiddleOfHistoryManager() {
        Task task = new Task("TASK 13", "DESCRIPTION 13");
        final int taskId1 = manager.addTask(task);

        Task task2 = new Task("TASK 21", "DESCRIPTION 21");
        final int taskId2 = manager.addTask(task2);

        Task task3 = new Task("TASK 31", "DESCRIPTION 31");
        final int taskId3 = manager.addTask(task3);

        for (Integer taskId : List.of(taskId1, taskId2, taskId3)) {
            manager.getTaskById(taskId);
        }

        manager.historyManager.remove(taskId2);
        List<Task> history = manager.historyManager.getHistory();

        assertEquals(2, history.size(), "Task has not been deleted from history");
        assertEquals(taskId1, history.get(0).getId(), "Task id is incorrect");
        assertEquals(taskId3, history.get(1).getId(), "Task id is incorrect");
    }

    @Test
    void removeNotExistingNodeFromHistory() {
        manager.historyManager.remove(100);
        List<Task> history = manager.historyManager.getHistory();

        assertEquals(0, history.size(), "History size is incorrect");
    }
}
