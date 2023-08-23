package service;

import enums.TaskStatus;
import enums.TaskType;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager {
    public final HashMap<Integer, Task> tasks = new HashMap<>();
    public final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public final HashMap<Integer, Epic> epics = new HashMap<>();
    private static final AtomicInteger uniqueId = new AtomicInteger();

    public Collection<? extends Task> getAllTasks(TaskType taskType) {
        if (taskType == TaskType.TASK) {
            return tasks.values();
        }
        if (taskType == TaskType.SUBTASK) {
            return subtasks.values();
        }
        if (taskType == TaskType.EPIC) {
            return epics.values();
        }
        System.out.println("Запрошен неверный тип задачи");
        return null;
    }

    public void deleteAllTasksByType(TaskType taskType) {
        if (taskType == TaskType.TASK) {
            tasks.clear();
            return;
        }
        if (taskType == TaskType.SUBTASK) {
            subtasks.clear();
            return;
        }
        if (taskType == TaskType.EPIC) {
            // при удалении эпика все подзадачи тоже удаляются
            epics.clear();
            subtasks.clear();
        } else {
            System.out.println("Запрошен неверный тип задачи");
        }
    }

    public <T extends Task> Task getTaskById(int taskId, TaskType taskType) {
        if (taskType == TaskType.TASK) {
            return tasks.get(taskId);
        }
        if (taskType == TaskType.SUBTASK) {
            return subtasks.get(taskId);
        }
        if (taskType == TaskType.EPIC) {
            return epics.get(taskId);
        }
        System.out.println("Запрошен неверный тип задачи");
        return null;
    }

    public void deleteTaskById(int taskId, TaskType taskType) {
        if (taskType == TaskType.TASK) {
            tasks.remove(taskId);
            return;
        }
        if (taskType == TaskType.SUBTASK) {
            subtasks.remove(taskId);
            return;
        }
        if (taskType == TaskType.EPIC) {
            // при удалении эпика все подзадачи тоже удаляются
            Epic epic = epics.get(taskId);
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(taskId);
        } else {
            System.out.println("Запрошен неверный тип задачи");
        }
    }

    public void addSubtask(Subtask task) {
        Epic epic = epics.get(task.getEpicId());
        if (epic != null) {
            task.setId(uniqueId.incrementAndGet());
            subtasks.put(task.getId(), task);
            epic.getSubtasks().add(task.getId());
            // обновляем статус эпика при добавлении подзадачи
            syncEpicStatus(task.getEpicId());
        } else {
            System.out.println("Указанный эпик не найден");
        }
    }

    public <T extends Task> void addTask(T task) {
        if (task instanceof Epic) {
            task.setId(uniqueId.incrementAndGet());
            epics.put(task.getId(), (Epic) task);
            return;
        }
        if (task instanceof Subtask) {
            addSubtask((Subtask) task);
            return;
        }
        if (task != null) {
            task.setId(uniqueId.incrementAndGet());
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    public <T extends Task> void updateTask(T task) {
        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
            return;
        }
        if (task instanceof Subtask) {
            Epic epic = epics.get(task.getId());
            if (epic != null) {
                subtasks.put(task.getId(), (Subtask) task);
                epic.getSubtasks().add(task.getId());
                // обновляем статус эпика при обновлении подзадачи
                syncEpicStatus(epic.getId());
            } else {
                System.out.println("Эпик не найден");
            }
            return;
        }
        if (task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Передан неверный тип данных");
        }
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getSubtasks()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    private void syncEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subtasks = getEpicSubtasks(epicId);
        int subtaskSize = epic.getSubtasks().size();

        if (subtaskSize == 0) {
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


    public void setTaskStatus(Task task, TaskStatus taskStatus) {
        if (task != null && taskStatus != null) {
            if (task instanceof Epic) {
                System.out.println("Нельзя выставить статус эпику");
                return;
            }
            if (task instanceof Subtask) {
                Subtask subtask = subtasks.get(task.getId());
                subtask.setTaskStatus(taskStatus);
                syncEpicStatus(subtask.getEpicId());
                return;
            }
            Task newTask = tasks.get(task.getId());
            newTask.setTaskStatus(taskStatus);
        }
    }
}
