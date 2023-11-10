package service.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.httpClient.KVTaskClient;
import utils.adapter.LocalDateTimeAdapter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private final KVTaskClient httpClient;
    private static final String historyHeader = "history";
    private static final Logger log = LoggerFactory.getLogger(HttpTaskManager.class);

    public HttpTaskManager(String url) {
        httpClient = new KVTaskClient(url);
    }

    public void save() {
        List<Task> tasksForServer = Stream.of(tasks.values(), epics.values(), subtasks.values())
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(Task::getId))
                .collect(Collectors.toList());

        for (Task task : tasksForServer) {
            httpClient.put(String.valueOf(task.getId()), gson.toJson(task));
        }
        List<Task> history = historyManager.getHistory();
        if (history.size() > 0) {
            httpClient.put(historyHeader, gson.toJson(history));
        }
    }

    public void loadFromServer() {
        int initialKey = 1;
        String response = httpClient.load(String.valueOf(initialKey));
        while (response != null) {
            Task taskFromString = gson.fromJson(response, Task.class);
            switch (taskFromString.getTaskType()) {
                case TASK:
                    tasks.put(taskFromString.getId(), taskFromString);
                    break;
                case SUBTASK:
                    Subtask subtask = gson.fromJson(response, Subtask.class);
                    subtasks.put(subtask.getId(), subtask);
                    break;
                case EPIC:
                    Epic epic = gson.fromJson(response, Epic.class);
                    epics.put(epic.getId(), epic);
                    break;
            }
            initialKey++;
            response = httpClient.load(String.valueOf(initialKey));
        }
        String history = httpClient.load(historyHeader);
        if (history != null) {
            Type historyIds = new TypeToken<ArrayList<? extends Task>>() {
            }.getType();
            List<? extends Task> fromJson = gson.fromJson(history, historyIds);
            for (Task task : fromJson) {
                historyManager.add(task);
            }
        }
    }

    public void clearData() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.clear();
        httpClient.clear();
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
