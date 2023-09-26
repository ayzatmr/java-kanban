package service;

import enums.TaskStatus;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    @Override
    public void deleteAllTasks() {
        for (int taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            List<Integer> epicSubtasks = epic.getSubtasks();
            for (int subtaskId : epicSubtasks) {
                historyManager.remove(subtaskId);
            }
            epicSubtasks.clear();
            syncEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (int taskId : epics.keySet()) {
            historyManager.remove(taskId);
        }
        epics.clear();

        for (int taskId : subtasks.keySet()) {
            historyManager.remove(taskId);
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        task.setViewed(true);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task getSubtaskById(int subtaskId) {
        Task subtask = subtasks.get(subtaskId);
        subtask.setViewed(true);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task getEpicById(int epicId) {
        Task epic = epics.get(epicId);
        epic.setViewed(true);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtaskId);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                syncEpicStatus(epic);
            } else {
                System.out.println("Эпик не найден");
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.remove(epicId);
        historyManager.remove(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public Integer addTask(Task task) {
        if (task != null) {
            task.setId(uniqueId.incrementAndGet());
            tasks.put(task.getId(), task);
            return task.getId();
        } else {
            System.out.println("Передан неверный тип данных");
        }
        return null;
    }

    @Override
    public Integer addSubtask(Subtask task) {
        if (task != null) {
            Epic epic = epics.get(task.getEpicId());
            if (epic != null) {
                task.setId(uniqueId.incrementAndGet());
                subtasks.put(task.getId(), task);
                epic.getSubtasks().add(task.getId());
                syncEpicStatus(epic);
                return task.getId();
            } else {
                System.out.println("Эпик не найден");
            }
        }
        return null;
    }

    @Override
    public Integer addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(uniqueId.incrementAndGet());
            epics.put(epic.getId(), epic);
            return epic.getId();
        } else {
            System.out.println("Передан неверный тип данных");
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            syncEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic currentEpic = epics.get(epic.getId());
            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasks()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        } else {
            System.out.println("Эпик не найден");
        }
        return epicSubtasks;
    }

    private void syncEpicStatus(Epic epic) {
        List<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());

        if (epicSubtasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        int newTask = 0;
        int done = 0;

        for (Subtask task : epicSubtasks) {
            if (task.getTaskStatus() == TaskStatus.NEW) {
                newTask += 1;
            }
            if (task.getTaskStatus() == TaskStatus.DONE) {
                done += 1;
            }
        }
        if (epicSubtasks.size() == newTask) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        if (epicSubtasks.size() == done) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
