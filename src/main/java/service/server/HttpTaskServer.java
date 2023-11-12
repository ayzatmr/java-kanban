package service.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.ValidateException;
import models.Epic;
import models.Subtask;
import models.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.manager.FileBackedTasksManager;
import utils.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static service.manager.FileBackedTasksManager.loadFromFile;

public class HttpTaskServer {
    private final HttpServer server;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static final int PORT = 8080;
    public FileBackedTasksManager manager;
    private static final Logger log = LoggerFactory.getLogger(HttpTaskServer.class);


    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::getPrioritizedTasks);
        server.createContext("/tasks/task", this::tasks);
        server.createContext("/tasks/subtask", this::subtasks);
        server.createContext("/tasks/epics", this::epics);
        server.createContext("/tasks/history", this::history);
        server.createContext("/tasks/subtask/epic", this::getEpicSubtasks);
    }

    public void start(String path) {
        log.info("Starting server on localhost:{}", PORT);
        server.start();
        log.info("Loading data from file ={}", path);
        manager = loadFromFile(path);
    }

    public void stop() {
        log.info("Stopping HttpTaskServer");
        server.stop(1);
    }


    private static Map<String, String> getParamMap(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();

        return Stream.of(query.split("&"))
                .filter(s -> !s.isEmpty())
                .map(kv -> kv.split("=", 2))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));

    }

    private void getTaskById(HttpExchange h, String taskId) throws IOException {
        String response;
        log.info("get task by id = {}", taskId);
        Task expectedTask = manager.getTaskById(Integer.parseInt(taskId));
        if (expectedTask == null) {
            response = "Task is not found";
            h.sendResponseHeaders(404, response.getBytes().length);
        } else {
            response = gson.toJson(expectedTask);
            h.sendResponseHeaders(200, response.getBytes().length);
        }
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void getAllTasks(HttpExchange h) throws IOException {
        log.info("get all tasks");
        String response = gson.toJson(manager.getAllTasks());
        h.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void deleteTaskById(HttpExchange h, String taskId) throws IOException {
        log.info("delete task by id = {}", taskId);
        manager.deleteTaskById(Integer.parseInt(taskId));
        h.sendResponseHeaders(204, -1);
    }

    private void deleteAllTasks(HttpExchange h) throws IOException {
        log.info("delete all tasks");
        manager.deleteAllTasks();
        h.sendResponseHeaders(204, -1);
    }

    private void createNewTask(HttpExchange h, Task task) throws IOException {
        log.info("create new task: {}", task);
        Integer newTaskId = manager.addTask(task);
        log.info("new task id: {}", newTaskId);
        h.sendResponseHeaders(200, 0);
    }

    private void updateTask(HttpExchange h, Task task) throws IOException {
        log.info("update task: {}", task);
        manager.updateTask(task);
        h.sendResponseHeaders(200, 0);
    }

    private void tasks(HttpExchange h) throws IOException {
        URI uri = h.getRequestURI();
        String taskId = getParamMap(uri.getQuery()).get("id");
        log.info(uri.toString());
        try {
            if ("GET".equals(h.getRequestMethod())) {
                if (taskId != null) {
                    getTaskById(h, taskId);
                } else {
                    getAllTasks(h);
                }
            } else if ("DELETE".equals(h.getRequestMethod())) {
                if (taskId != null) {
                    deleteTaskById(h, taskId);
                } else {
                    deleteAllTasks(h);
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                String body = new String(h.getRequestBody().readAllBytes(), UTF_8);
                Task taskFromJson = gson.fromJson(body, Task.class);
                Task task = manager.getTaskById(taskFromJson.getId());
                if (task == null) {
                    createNewTask(h, taskFromJson);
                } else {
                    updateTask(h, taskFromJson);
                }
            } else {
                log.warn("/tasks/task is waiting GET/POST/DELETE request, but {} got ", h.getRequestMethod());
                h.sendResponseHeaders(403, 0);
            }
        } catch (ValidateException e) {
            log.error(e.getMessage());
        } finally {
            h.close();
        }
    }


    private void getSubtaskById(HttpExchange h, String taskId) throws IOException {
        String response;
        log.info("get subtask by id = {}", taskId);
        Task expectedTask = manager.getSubtaskById(Integer.parseInt(taskId));
        if (expectedTask == null) {
            response = "Subtask is not found";
            h.sendResponseHeaders(404, response.getBytes().length);
        } else {
            response = gson.toJson(expectedTask);
            h.sendResponseHeaders(200, response.getBytes().length);
        }
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void getAllSubtasks(HttpExchange h) throws IOException {
        log.info("get all subtasks");
        String response = gson.toJson(manager.getAllSubtasks());
        h.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void deleteSubtaskById(HttpExchange h, String taskId) throws IOException {
        log.info("delete subtask by id = {}", taskId);
        manager.deleteSubtaskById(Integer.parseInt(taskId));
        h.sendResponseHeaders(204, -1);
    }

    private void deleteAllSubtasks(HttpExchange h) throws IOException {
        log.info("delete all subtasks");
        manager.deleteAllSubtasks();
        h.sendResponseHeaders(204, -1);
    }

    private void createNewSubtask(HttpExchange h, Subtask task) throws IOException {
        log.info("create new task: {}", task);
        Integer newTaskId = manager.addSubtask(task);
        log.info("new subtask id: {}", newTaskId);
        h.sendResponseHeaders(200, 0);
    }

    private void updateSubtask(HttpExchange h, Subtask task) throws IOException {
        log.info("update subtask: {}", task);
        manager.updateSubtask(task);
        h.sendResponseHeaders(200, 0);
    }

    private void subtasks(HttpExchange h) throws IOException {
        URI uri = h.getRequestURI();
        String subtaskId = getParamMap(uri.getQuery()).get("id");
        log.info(uri.toString());
        try {
            if ("GET".equals(h.getRequestMethod())) {
                if (subtaskId != null) {
                    getSubtaskById(h, subtaskId);
                } else {
                    getAllSubtasks(h);
                }
            } else if ("DELETE".equals(h.getRequestMethod())) {
                if (subtaskId != null) {
                    deleteSubtaskById(h, subtaskId);
                } else {
                    deleteAllSubtasks(h);
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                String body = new String(h.getRequestBody().readAllBytes(), UTF_8);
                Subtask taskFromJson = gson.fromJson(body, Subtask.class);
                Task task = manager.getSubtaskById(taskFromJson.getId());
                if (task == null) {
                    createNewSubtask(h, taskFromJson);
                } else {
                    updateSubtask(h, taskFromJson);
                }
            } else {
                log.warn("/tasks/subtask is waiting GET/POST/DELETE request, but {} got ", h.getRequestMethod());
                h.sendResponseHeaders(403, 0);
            }
        } catch (ValidateException e) {
            log.error(e.getMessage());
        } finally {
            h.close();
        }
    }


    private void getEpicById(HttpExchange h, String taskId) throws IOException {
        String response;
        log.info("get epic by id = {}", taskId);
        Task expectedTask = manager.getEpicById(Integer.parseInt(taskId));
        if (expectedTask == null) {
            response = "Epic is not found";
            h.sendResponseHeaders(404, response.getBytes().length);
        } else {
            response = gson.toJson(expectedTask);
            h.sendResponseHeaders(200, response.getBytes().length);
        }
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void getAllEpics(HttpExchange h) throws IOException {
        log.info("get all epics");
        String response = gson.toJson(manager.getAllEpics());
        h.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void deleteEpicById(HttpExchange h, String epicId) throws IOException {
        log.info("delete epic by id = {}", epicId);
        manager.deleteEpicById(Integer.parseInt(epicId));
        h.sendResponseHeaders(204, -1);
    }

    private void deleteAllEpics(HttpExchange h) throws IOException {
        log.info("delete all epics");
        manager.deleteAllEpics();
        h.sendResponseHeaders(204, -1);
    }

    private void createNewEpic(HttpExchange h, Epic epic) throws IOException {
        log.info("create new epic: {}", epic);
        Integer epicId = manager.addEpic(epic);
        log.info("new epic id: {}", epicId);
        h.sendResponseHeaders(200, 0);
    }

    private void updateEpic(HttpExchange h, Epic epic) throws IOException {
        log.info("update epic: {}", epic);
        manager.updateEpic(epic);
        h.sendResponseHeaders(200, 0);
    }

    private void epics(HttpExchange h) throws IOException {
        URI uri = h.getRequestURI();
        String epicId = getParamMap(uri.getQuery()).get("id");
        log.info(uri.toString());
        try {
            if ("GET".equals(h.getRequestMethod())) {
                if (epicId != null) {
                    getEpicById(h, epicId);
                } else {
                    getAllEpics(h);
                }
            } else if ("DELETE".equals(h.getRequestMethod())) {
                if (epicId != null) {
                    deleteEpicById(h, epicId);
                } else {
                    deleteAllEpics(h);
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                String body = new String(h.getRequestBody().readAllBytes(), UTF_8);
                Epic taskFromJson = gson.fromJson(body, Epic.class);
                Task task = manager.getEpicById(taskFromJson.getId());
                if (task == null) {
                    createNewEpic(h, taskFromJson);
                } else {
                    updateEpic(h, taskFromJson);
                }
            } else {
                log.warn("/tasks/epics is waiting GET/POST/DELETE request, but {} got ", h.getRequestMethod());
                h.sendResponseHeaders(403, 0);
            }
        } catch (ValidateException e) {
            log.error(e.getMessage());
        } finally {
            h.close();
        }
    }

    private void getPrioritizedTasks(HttpExchange h) throws IOException {
        try {
            URI uri = h.getRequestURI();
            log.info(uri.toString());
            if ("GET".equals(h.getRequestMethod())) {
                List<Task> tasks = manager.getPrioritizedTasks()
                        .stream()
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
                String response = gson.toJson(tasks);
                h.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                log.warn("/tasks is waiting GET request, but {} got ", h.getRequestMethod());
                h.sendResponseHeaders(403, 0);
            }
        } catch (ValidateException e) {
            log.error(e.getMessage());
        } finally {
            h.close();
        }
    }

    private void getEpicSubtasks(HttpExchange h) throws IOException {
        String response;
        try {
            URI uri = h.getRequestURI();
            String epicId = getParamMap(uri.getQuery()).get("id");
            log.info(uri.toString());
            if ("GET".equals(h.getRequestMethod())) {
                if (epicId != null) {
                    log.info("get epic subtasks by epicId = {}", epicId);
                    response = gson.toJson(manager.getEpicSubtasks(Integer.parseInt(epicId)));
                    h.sendResponseHeaders(200, response.getBytes().length);
                } else {
                    log.info("epicId is missed");
                    response = "epicId is missed";
                    h.sendResponseHeaders(400, response.getBytes().length);
                }
                try (OutputStream os = h.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                log.warn("/tasks/subtasks/epic is waiting GET request, but {} got ", h.getRequestMethod());
                h.sendResponseHeaders(403, 0);
            }
        } finally {
            h.close();
        }
    }

    private void history(HttpExchange h) throws IOException {
        String response;
        try {
            URI uri = h.getRequestURI();
            log.info(uri.toString());
            if ("GET".equals(h.getRequestMethod())) {
                List<? extends Task> history = manager.historyManager.getHistory();
                response = gson.toJson(history);
                log.info("get history: {}", response);
                h.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                log.warn("/tasks/history is waiting GET request, but {} got ", h.getRequestMethod());
                h.sendResponseHeaders(403, 0);
            }
        } finally {
            h.close();
        }
    }
}
