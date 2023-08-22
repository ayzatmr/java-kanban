package service;

import enums.TaskStatus;
import enums.TaskType;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskService {
    public HashMap<Integer, Task> tasks = new HashMap<>();

    private void addSubtask(Task task, int epicId) {
        Task epic = tasks.get(epicId);
        epic.getSubtasks().add(task);
        checkEpicStatus(tasks.get(epicId));
    }

    private Map<Integer, Task> getAllSubtasks() {
        Map<Integer, Task> subTasks = new HashMap<>();
        Map<Integer, Task> epics = getTasksByType(TaskType.EPIC);
        for (Task epic : epics.values()) {
            for (Task subtask : epic.getSubtasks()) {
                subTasks.put(subtask.getId(), subtask);
            }
        }
        return subTasks;
    }

    private void checkEpicStatus(Task task) {
        if (task.getSubtasks().size() == 0) {
            task.setTaskStatus(TaskStatus.NEW);
            return;
        }
        boolean isAllOpened = task.getSubtasks()
                .stream()
                .allMatch(s -> s.getTaskStatus() == TaskStatus.NEW);

        if (isAllOpened) {
            task.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean isAllClosed = task.getSubtasks()
                .stream()
                .allMatch(s -> s.getTaskStatus() == TaskStatus.DONE);

        if (isAllClosed) {
            task.setTaskStatus(TaskStatus.DONE);
            return;
        }
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
    }

    public Task getTaskById(int taskId) {
        Map<Integer, Task> allTasks = new HashMap<>();
        allTasks.putAll(getAllSubtasks());
        allTasks.putAll(tasks);

        if (allTasks.containsKey(taskId)) {
            return allTasks.get(taskId);
        } else {
            System.out.println("Такой задачи не существует");
            return null;
        }
    }

    public Map<Integer, Task> getTasksByType(TaskType taskType) {
        if (taskType == TaskType.SUBTASK) {
            return getAllSubtasks();
        }
        return tasks.entrySet()
                .stream()
                .filter(t -> t.getValue().getTaskType() == taskType)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    public void addNewTask(Task task) {
        if (task.getTaskType() == TaskType.SUBTASK) {
            addSubtask(task, task.getEpicId());
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (task.getTaskType() == TaskType.SUBTASK) {
            Task epic = tasks.get(task.getEpicId());
            for (Task subtask : epic.getSubtasks()) {
                if (task.getId() == subtask.getId()) {
                    epic.getSubtasks().remove(subtask);
                    epic.getSubtasks().add(task);
                }
            }
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void deleteTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.remove(task.getId());
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    public void deleteAllTasksByType(TaskType taskType) {
        Map<Integer, Task> tasksToRemove = getTasksByType(taskType);
        if (taskType == TaskType.SUBTASK) {
            for (Task subtask : tasksToRemove.values()) {
                Task epic = tasks.get(subtask.getEpicId());
                epic.getSubtasks().remove(subtask);
                checkEpicStatus(epic);
            }
            return;
        }
        tasks.entrySet().removeIf(entry -> tasksToRemove.containsKey(entry.getKey()));
    }


    public ArrayList<Task> getEpicSubtasks(int taskId) {
        ArrayList<Task> subtasks = new ArrayList<>();
        if (tasks.containsKey(taskId)) {
            subtasks = tasks.get(taskId).getSubtasks();
        } else {
            System.out.println("Такого эпика не существует");
        }
        return subtasks;
    }

    public void setTaskStatus(Task task, TaskStatus taskStatus) {
        if (task != null && taskStatus != null) {
            switch (task.getTaskType()) {
                case TASK:
                    task.setTaskStatus(taskStatus);
                    break;
                case SUBTASK:
                    task.setTaskStatus(taskStatus);
                    // находим эпик подзадачи и меняем ей статус
                    int taskId = task.getEpicId();
                    checkEpicStatus(tasks.get(taskId));
                    break;
                case EPIC:
                    System.out.println("Нельзя выставить статус эпику");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + task.getTaskType());
            }
        }
    }
}
