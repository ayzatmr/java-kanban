import enums.TaskStatus;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EpicTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void before() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void noSubtasksEpicShouldHaveNewTaskStatus() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Epic savedEpic = (Epic) manager.getEpicById(epicId);

        assertEquals(0, savedEpic.getSubtasks().size(), "Empty epic has subtasks");
        assertEquals(TaskStatus.NEW, savedEpic.getTaskStatus(), "Epic Status is not NEW");
    }

    @Test
    public void epicWithOneOnlyNewSubtaskShouldHaveNewTaskStatus() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        int subtaskId = manager.addSubtask(subtask1);
        Epic savedEpic = (Epic) manager.getEpicById(epicId);

        Subtask savedSubtask = (Subtask) manager.getSubtaskById(subtaskId);
        assertEquals(TaskStatus.NEW, savedSubtask.getTaskStatus(), "Subtask Status is not NEW");

        assertEquals(1, savedEpic.getSubtasks().size(), "Epic subtasks size is not equal to 1");
        assertEquals(TaskStatus.NEW, savedEpic.getTaskStatus(), "Epic Status is not NEW");
    }

    @Test
    public void epicWithAllDoneSubtasksShouldHaveDoneTaskStatus() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        LocalDateTime time = LocalDateTime.now().plusDays(1);
        Duration duration = Duration.ofSeconds(1);
        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time);
        int subtaskId = manager.addSubtask(subtask1);
        subtask1.setTaskStatus(TaskStatus.DONE);
        subtask1.setId(subtaskId);
        manager.updateSubtask(subtask1);

        Epic savedEpic = (Epic) manager.getEpicById(epicId);

        Subtask savedSubtask = (Subtask) manager.getSubtaskById(subtaskId);
        assertEquals(TaskStatus.DONE, savedSubtask.getTaskStatus(), "Subtask Status is not DONE");

        assertEquals(1, savedEpic.getSubtasks().size(), "Epic subtasks size is not equal to 1");
        assertEquals(TaskStatus.DONE, savedEpic.getTaskStatus(), "Epic Status is not DONE");
    }

    @Test
    public void epicWithInProgressSubtasksShouldHaveInProgressTaskStatus() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        LocalDateTime time = LocalDateTime.now().plusDays(1);
        Duration duration = Duration.ofSeconds(1);
        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time);
        int subtaskId = manager.addSubtask(subtask1);
        subtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        subtask1.setId(subtaskId);
        manager.updateSubtask(subtask1);

        Epic savedEpic = (Epic) manager.getEpicById(epicId);

        Subtask savedSubtask = (Subtask) manager.getSubtaskById(subtaskId);
        assertEquals(TaskStatus.IN_PROGRESS, savedSubtask.getTaskStatus(), "Subtask Status is not DONE");

        assertEquals(1, savedEpic.getSubtasks().size(), "Epic subtasks size is not equal to 1");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(), "Epic Status is not DONE");
    }

    @Test
    public void epicWithDoneAndNewSubtasksShouldHaveInProgressTaskStatus() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        LocalDateTime time = LocalDateTime.now().plusDays(1);
        LocalDateTime time2 = LocalDateTime.now().plusDays(5);
        Duration duration = Duration.ofSeconds(1);
        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time);
        int subtaskId = manager.addSubtask(subtask1);

        subtask1.setTaskStatus(TaskStatus.DONE);
        subtask1.setId(subtaskId);
        manager.updateSubtask(subtask1);

        Subtask subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId(), duration, time2);
        manager.addSubtask(subtask2);

        Epic savedEpic = (Epic) manager.getEpicById(epicId);

        assertEquals(2, savedEpic.getSubtasks().size(), "Epic subtasks size is not equal to 2");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(), "Epic Status is not IN_PROGRESS");
    }

    @Test
    public void subtaskShouldHaveEpicId() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        int subtaskId = manager.addSubtask(subtask1);

        Subtask savedSubtask = (Subtask) manager.getSubtaskById(subtaskId);
        assertEquals(epicId, savedSubtask.getEpicId(), "Subtask has no epicId");
    }

    @Test
    public void changeEpicStatusOnSubtaskDelete() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        LocalDateTime time = LocalDateTime.now().plusDays(1);
        LocalDateTime time2 = LocalDateTime.now().plusDays(5);
        Duration duration = Duration.ofSeconds(1);
        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration, time);
        int subtaskId = manager.addSubtask(subtask1);
        subtask1.setTaskStatus(TaskStatus.DONE);
        subtask1.setId(subtaskId);
        manager.updateSubtask(subtask1);

        Subtask subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId(), duration, time2);
        int subtaskId2 = manager.addSubtask(subtask2);

        Epic savedEpic = (Epic) manager.getEpicById(epicId);
        assertEquals(2, savedEpic.getSubtasks().size(), "Epic subtasks size is not equal to 2");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(), "Epic Status is not IN_PROGRESS");

        manager.deleteSubtaskById(subtaskId2);

        savedEpic = (Epic) manager.getEpicById(epicId);
        assertEquals(1, savedEpic.getSubtasks().size(), "Epic subtasks size is equal to 2");
        assertEquals(TaskStatus.DONE, savedEpic.getTaskStatus(), "Epic Status is not DONE");
    }

    @Test
    public void clearAllSubtaskOnEpicsClear() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        int subtaskId = manager.addSubtask(subtask1);

        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(1, epics.size(), "Epics size is not equal to 1");
        assertEquals(1, subtasks.size(), "Subtasks size is not equal to 1");

        manager.deleteAllEpics();
        epics = manager.getAllEpics();
        subtasks = manager.getAllSubtasks();

        assertEquals(0, epics.size(), "Epics size is not equal to 0");
        assertEquals(0, subtasks.size(), "Subtasks size is not equal to 0");
    }

    @Test
    public void deleteSubtasksOnEpicDelete() {
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        int subtaskId = manager.addSubtask(subtask1);

        manager.deleteEpicById(epicId);
        Task epic = manager.getEpicById(epicId);
        Task subtask = manager.getSubtaskById(subtaskId);

        assertNull(epic, "Epic is not deleted");
        assertNull(subtask, "subtask is not deleted");
    }

    @Test
    public void checkEpicStartTimeAndDuration() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusMinutes(15);

        Duration duration1 = Duration.ofMinutes(5);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration1, time1);
        int subtaskId = manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId(), duration1, time2);
        int subtaskId2 = manager.addSubtask(subtask2);

        Epic savedEpic = (Epic) manager.getEpicById(epicId);
        Subtask savedSubtask1 = (Subtask) manager.getSubtaskById(subtaskId);
        Subtask savedSubtask2 = (Subtask) manager.getSubtaskById(subtaskId2);

        Duration totalDuration = savedSubtask1.getDuration().plus(savedSubtask2.getDuration());

        assertEquals(savedSubtask1.getStartTime(), savedEpic.getStartTime(), "Start time is incorrect");
        assertEquals(savedSubtask2.getEndTime(), savedEpic.getEndTime(), "End time is incorrect");
        assertEquals(totalDuration, savedEpic.getDuration(), "Duration is incorrect");
    }

    @Test
    public void checkEpicSyncWhenSubTaskHasNoDurationAndStartTime() {
        LocalDateTime time1 = LocalDateTime.now();
        Duration duration1 = Duration.ofMinutes(5);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        int epicId = manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId(), duration1, time1);
        int subtaskId = manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId());
        int subtaskId2 = manager.addSubtask(subtask2);

        Epic savedEpic = (Epic) manager.getEpicById(epicId);
        Subtask savedSubtask1 = (Subtask) manager.getSubtaskById(subtaskId);

        assertEquals(savedSubtask1.getStartTime(), savedEpic.getStartTime(), "Start time is incorrect");
        assertEquals(savedSubtask1.getEndTime(), savedEpic.getEndTime(), "End time is incorrect");
        assertEquals(savedSubtask1.getDuration(), savedEpic.getDuration(), "Duration is incorrect");
    }
}
