package service;

import enums.TaskStatus;
import enums.TaskType;
import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final String file;
    private static final String FIRST_LINE = "id,type,name,status,description,epic,startTime,duration,endTime\n";

    public FileBackedTasksManager(String file) {
        this.file = file;
    }

    private String toString(Task task) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(String.valueOf(task.getId()))
                .add(String.valueOf(task.getTaskType()))
                .add(String.valueOf(task.getName()))
                .add(String.valueOf(task.getTaskStatus()))
                .add(String.valueOf(task.getDescription()));
        if (task instanceof Subtask) {
            joiner.add(String.valueOf(((Subtask) task).getEpicId()));
        }
        joiner.add(String.valueOf(task.getStartTime()))
                .add(String.valueOf(task.getDuration()))
                .add(String.valueOf(task.getEndTime()));
        return joiner.toString();
    }

    private Task fromString(String value) {
        String[] line = value.split(",");
        switch (TaskType.valueOf(line[1])) {
            case TASK:
                Task task = new Task(line[2], line[4]);
                task.setId(Integer.parseInt(line[0]));
                task.setTaskType(TaskType.valueOf(line[1]));
                task.setTaskStatus(TaskStatus.valueOf(line[3]));
                return task;
            case EPIC:
                Task epic = new Epic(line[2], line[4]);
                epic.setId(Integer.parseInt(line[0]));
                epic.setTaskType(TaskType.valueOf(line[1]));
                epic.setTaskStatus(TaskStatus.valueOf(line[3]));
                return epic;
            case SUBTASK:
                Task subtask = new Subtask(line[2], line[4], Integer.parseInt(line[5]));
                subtask.setId(Integer.parseInt(line[0]));
                subtask.setTaskType(TaskType.valueOf(line[1]));
                subtask.setTaskStatus(TaskStatus.valueOf(line[3]));
                return subtask;
            default:
                throw new IllegalStateException("Unexpected value: " + line[1]);
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (Task task : history) {
            sb.append(task.getId());
            sb.append(",");
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        List<Integer> historyIds = new ArrayList<>();
        if (value == null) {
            return historyIds;
        }
        String[] elements = value.split(",");
        if (pattern.matcher(elements[0]).matches()) {
            for (String el : elements) {
                historyIds.add(Integer.parseInt(el));
            }
        }
        return historyIds;
    }

    public void createEmptyFile(String path) throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, false))) {
            writer.write("");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            List<Task> tasksToWrite = Stream.of(tasks.values(), epics.values(), subtasks.values())
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparing(Task::getId))
                    .collect(Collectors.toList());

            writer.write(FIRST_LINE);
            for (Task task : tasksToWrite) {
                writer.write(toString(task) + "\n");
            }
            writer.newLine();
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }
    }

    public static FileBackedTasksManager loadFromFile(String file) throws ManagerSaveException {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        LinkedList<String> lines = new LinkedList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(manager.file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                lines.add(fileReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }

        String history = null;
        if (!lines.isEmpty()) {
            lines.removeFirst();
            history = lines.removeLast();
            lines.removeLast();
        }
        for (String task : lines) {
            Task taskFromString = manager.fromString(task);
            switch (taskFromString.getTaskType()) {
                case TASK:
                    manager.tasks.put(taskFromString.getId(), taskFromString);
                    break;
                case SUBTASK:
                    manager.subtasks.put(taskFromString.getId(), (Subtask) taskFromString);
                    break;
                case EPIC:
                    manager.epics.put(taskFromString.getId(), (Epic) taskFromString);
                    break;
            }
        }
        List<Integer> historyFromString = historyFromString(history);
        for (int taskId : historyFromString) {
            if (manager.tasks.containsKey(taskId))
                manager.historyManager.add(manager.tasks.get(taskId));
            else if (manager.subtasks.containsKey(taskId))
                manager.historyManager.add(manager.subtasks.get(taskId));
            else if (manager.epics.containsKey(taskId))
                manager.historyManager.add(manager.epics.get(taskId));
            else {
                System.out.printf("Задача с id= %s не найдена", taskId);
            }
        }
        manager.setUniqueId();
        return manager;
    }


    private void setUniqueId() {
        int size = tasks.size() + subtasks.size() + epics.size();
        uniqueId.set(size);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Task getSubtaskById(int subtaskId) {
        Task task = super.getSubtaskById(subtaskId);
        save();
        return task;
    }

    @Override
    public Task getEpicById(int epicId) {
        Task task = super.getEpicById(epicId);
        save();
        return task;
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public Integer addTask(Task task) {
        int taskId = super.addTask(task);
        save();
        return taskId;
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        int subtaskId = super.addSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public Integer addEpic(Epic epic) {
        int epicId = super.addEpic(epic);
        save();
        return epicId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
}
