package interfaces;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(int taskId);

    Task getSubtaskById(int subtaskId);

    Task getEpicById(int epicId);

    void deleteTaskById(int taskId);

    void deleteSubtaskById(int subtaskId);

    void deleteEpicById(int epicId);

    Integer addTask(Task task);

    Integer addSubtask(Subtask task);

    Integer addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Subtask> getEpicSubtasks(int epicId);
}
