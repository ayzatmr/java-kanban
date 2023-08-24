package service;

import enums.TaskStatus;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            syncEpicStatus(epic);
        }
        subtasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Task getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public Task getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteSubtaskById(int subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtaskId);
                subtasks.remove(subtaskId);
                syncEpicStatus(epic);
            } else {
                System.out.println("Эпик не найден");
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    public void deleteEpicById(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        } else {
            System.out.println("Эпик не найден");
        }
    }

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


    public void updateTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            syncEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic currentEpic = epics.get(epic.getId());
            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasks()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        else {
            System.out.println("Эпик не найден");
        }
        return epicSubtasks;
    }

    private void syncEpicStatus(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        int newTask = 0;
        int done = 0;

        for (Subtask task : subtasks) {
            if (task.getTaskStatus() == TaskStatus.NEW) {
                newTask += 1;
            }
            if (task.getTaskStatus() == TaskStatus.DONE) {
                done += 1;
            }
        }
        if (subtasks.size() == newTask) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        if (subtasks.size() == done) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
