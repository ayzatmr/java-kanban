import exceptions.ValidateException;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void before() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void getTaskByIdShouldReturnNewAddedTask() {
        Task task = new Task("TASK 1", "DESCRIPTION 1");
        final int taskId = manager.addTask(task);

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Task is not found");
        assertEquals(task, savedTask, "Tasks are not equal");
    }

    @Test
    void getSubTaskByIdShouldReturnNewAddedSubtask() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epicId);
        int subtaskId = manager.addSubtask(subtask1);
        Subtask savedSubtask = (Subtask) manager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Task is not found");
        assertEquals(subtask1, savedSubtask, "Tasks are not equal");
    }

    @Test
    void getEpicByIdShouldReturnNewAddedEpic() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);
        Epic savedEpic = (Epic) manager.getEpicById(epicId);

        assertNotNull(savedEpic, "Epic is not found");
        assertEquals(epic1, savedEpic, "Epics are not equal");
    }

    @Test
    void checkNullOnGetTaskById() {
        final Task savedTask = manager.getTaskById(0);
        assertNull(savedTask, "Task is found");
    }

    @Test
    void checkNullOnGetEpicById() {
        Epic savedEpic = (Epic) manager.getEpicById(0);
        assertNull(savedEpic, "Epic is found");
    }

    @Test
    void checkNullOnGetSubtaskById() {
        Subtask savedSubtask = (Subtask) manager.getSubtaskById(0);
        assertNull(savedSubtask, "Subtask is found");
    }

    @Test
    void getAllTasksShouldReturnAllCreatedTasks() {
        Task task = new Task("TASK 1", "DESCRIPTION 1");
        manager.addTask(task);

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Task is not found");
        assertEquals(1, tasks.size(), "Wrong amount of tasks");
        assertEquals(task, tasks.get(0), "Tasks are not equal");
    }

    @Test
    void getAllEpicsShouldReturnAllCreatedEpics() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);
        Epic savedEpic = (Epic) manager.getEpicById(epicId);

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Epic is not found");
        assertEquals(1, epics.size(), "Wrong amount of epics");
        assertEquals(savedEpic, epics.get(0), "Epics are not equal");
    }

    @Test
    void getAllSubtasksShouldReturnAllCreatedSubtasks() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epicId);
        int subtaskId = manager.addSubtask(subtask1);
        Subtask savedSubtask = (Subtask) manager.getSubtaskById(subtaskId);

        final List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtasks, "Subtasks are not found");
        assertEquals(1, subtasks.size(), "Wrong amount of Subtasks");
        assertEquals(savedSubtask, subtasks.get(0), "Subtasks are not equal");
    }

    @Test
    void checkNullOnAddTask() {
        final Integer taskId = manager.addTask(null);
        assertNull(taskId, "Task is not found");
    }

    @Test
    void checkNullOnAddSubtaskTask() {
        final Integer taskId = manager.addSubtask(null);
        assertNull(taskId, "Subtask is not found");
    }

    @Test
    void checkNullOnAddEpicTask() {
        final Integer taskId = manager.addEpic(null);
        assertNull(taskId, "Epic is not found");
    }

    @Test
    void checkNullOnAddUpdateSubtask() {
        final Integer taskId = manager.addSubtask(null);
        assertNull(taskId, "Subtask is not found");
    }

    @Test
    void checkNullOnUpdateEpicTask() {
        final Integer taskId = manager.addEpic(null);
        assertNull(taskId, "Epic is not found");
    }

    @Test
    void addEqualTasksShouldThrowException() {
        int minutes = 15;
        LocalDateTime time1 = LocalDateTime.now();

        Duration duration1 = Duration.ofMinutes(minutes);
        Task task = new Task("TASK 1", "DESCRIPTION 1", duration1, time1);
        Task task2 = new Task("TASK 2", "DESCRIPTION 1", duration1, time1);
        final int taskId = manager.addTask(task);
        ValidateException ex = assertThrows(
                ValidateException.class,
                () -> manager.addTask(task2));
        assertEquals("It is not allowed to start 2 tasks simultaneously", ex.getMessage());
    }

    @Test
    void addRightOverlappedTasksShouldThrowException() {
        int minutes = 15;
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusMinutes(5);

        Duration duration1 = Duration.ofMinutes(minutes);
        Task task = new Task("TASK 1", "DESCRIPTION 1", duration1, time1);
        Task task2 = new Task("TASK 2", "DESCRIPTION 1", duration1, time2);
        final int taskId = manager.addTask(task);
        ValidateException ex = assertThrows(
                ValidateException.class,
                () -> manager.addTask(task2));
        assertEquals("It is not allowed to start 2 tasks simultaneously", ex.getMessage());
    }

    @Test
    void addLeftOverlappedTasksShouldThrowException() {
        int minutes = 15;
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().minusMinutes(5);

        Duration duration1 = Duration.ofMinutes(minutes);
        Task task = new Task("TASK 1", "DESCRIPTION 1", duration1, time1);
        Task task2 = new Task("TASK 2", "DESCRIPTION 1", duration1, time2);
        final int taskId = manager.addTask(task);
        ValidateException ex = assertThrows(
                ValidateException.class,
                () -> manager.addTask(task2));
        assertEquals("It is not allowed to start 2 tasks simultaneously", ex.getMessage());
    }

    @Test
    void addNotOverlappedTasks() {
        int minutes = 15;
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().minusMinutes(minutes + 1);
        Duration duration1 = Duration.ofMinutes(minutes);

        Task task = new Task("TASK 1", "DESCRIPTION 1", duration1, time1);
        Task task2 = new Task("TASK 2", "DESCRIPTION 1", duration1, time2);
        final int taskId = manager.addTask(task);
        final int taskId2 = manager.addTask(task2);

        Task saveTask1 = manager.getTaskById(taskId);
        Task saveTask2 = manager.getTaskById(taskId2);

        assertEquals(task, saveTask1, "Tasks are not equal");
        assertEquals(task2, saveTask2, "Tasks are not equal");
    }

    @Test
    void getPrioritizedTasksShouldReturnSortedByDateTimeMap() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusMinutes(10);
        LocalDateTime time3 = LocalDateTime.now().plusMinutes(20);
        Duration duration = Duration.ofMinutes(5);

        Task task = new Task("TASK 1", "DESCRIPTION 1", duration, time1);
        Task task2 = new Task("TASK 2", "DESCRIPTION 1", duration, time2);
        final int taskId2 = manager.addTask(task2);
        final int taskId = manager.addTask(task);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time3);
        int subtaskId = manager.addSubtask(subtask1);

        List<Integer> expectedList = List.of(taskId, taskId2, subtaskId);

        List<Integer> result = manager.getPrioritizedTasks()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        assertIterableEquals(expectedList, result, "Collection is not sorted properly");
    }
}