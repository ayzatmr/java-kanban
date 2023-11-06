package service;

import enums.TaskStatus;
import exceptions.ValidateException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class InMemoryTaskManager implements TaskManager {
    private static final Logger log = LoggerFactory.getLogger(InMemoryTaskManager.class);
    protected final Map<Integer, Task> tasks = new TreeMap<>();
    protected final Map<Integer, Subtask> subtasks = new TreeMap<>();
    protected final Map<Integer, Epic> epics = new TreeMap<>();
    protected final AtomicInteger uniqueId = new AtomicInteger();
    private static final String ERROR = "It is not allowed to start 2 tasks simultaneously";

    protected SortedSet<Map.Entry<Integer, ? extends Task>> sortedset = new TreeSet<>(
            Comparator.comparing(e -> e.getValue().getStartTime(),
                    Comparator.nullsLast(Comparator.naturalOrder())));

    public final HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task getSubtaskById(int subtaskId) {
        Task subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Task getEpicById(int epicId) {
        Task epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
        }
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
                epic.getSubtasks().remove(Integer.valueOf(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                syncEpicStatus(epic);
            } else {
                log.info("Epic is not found");
            }
        } else {
            log.info("Subtask is not found");
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
            log.info("Epic is not found");
        }
    }

    @Override
    public Integer addTask(Task task) {
        if (task != null) {
            try {
                if (isOverlapped(task)) {
                    throw new ValidateException(ERROR);
                }
            } catch (ValidateException ex) {
                throw new ValidateException(ex.getMessage());
            }

            task.setId(uniqueId.incrementAndGet());
            tasks.put(task.getId(), task);
            return task.getId();
        } else {
            log.info("Wrong data type is provided");
        }
        return null;
    }

    @Override
    public Integer addSubtask(Subtask task) {
        if (task != null) {
            try {
                if (isOverlapped(task)) {
                    throw new ValidateException(ERROR);
                }
            } catch (ValidateException ex) {
                throw new ValidateException(ex.getMessage());
            }
            Epic epic = epics.get(task.getEpicId());
            if (epic != null) {
                task.setId(uniqueId.incrementAndGet());
                subtasks.put(task.getId(), task);
                epic.getSubtasks().add(task.getId());
                syncEpicStatus(epic);
                return task.getId();
            } else {
                log.info("Epic is not found");
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
            log.info("Wrong data type is provided");
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            try {
                if (isOverlapped(task)) {
                    throw new ValidateException(ERROR);
                }
            } catch (ValidateException ex) {
                throw new ValidateException(ex.getMessage());
            }
            tasks.put(task.getId(), task);
        } else {
            log.info("Wrong data type is provided");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            try {
                if (isOverlapped(subtask)) {
                    throw new ValidateException(ERROR);
                }
            } catch (ValidateException ex) {
                throw new ValidateException(ex.getMessage());
            }
            subtasks.put(subtask.getId(), subtask);
            syncEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            log.info("Wrong data type is provided");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic currentEpic = epics.get(epic.getId());
            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
        } else {
            log.info("Wrong data type is provided");
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
            log.info("Epic is not found");
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

        Optional<LocalDateTime> epicEndTime = getEpicEndTime(epic.getId());
        Optional<LocalDateTime> epicStartTime = getEpicStartTime(epic.getId());
        Duration epicDuration = getEpicDuration(epic.getId());
        epicStartTime.ifPresent(epic::setStartTime);
        epicEndTime.ifPresent(epic::setEndTime);
        epic.setDuration(epicDuration);

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

    private Optional<LocalDateTime> getEpicStartTime(int epicId) {
        return getEpicSubtasks(epicId).stream()
                .map(t -> Optional.ofNullable(t.getStartTime()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(LocalDateTime::compareTo);
    }

    private Optional<LocalDateTime> getEpicEndTime(int epicId) {
        return getEpicSubtasks(epicId).stream()
                .map(t -> Optional.ofNullable(t.getEndTime()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(LocalDateTime::compareTo);
    }

    private Duration getEpicDuration(int epicId) {
        return getEpicSubtasks(epicId).stream()
                .map(t -> Optional.ofNullable(t.getDuration()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public SortedSet<Map.Entry<Integer, ? extends Task>> getPrioritizedTasks() {
        sortedset.addAll(tasks.entrySet());
        sortedset.addAll(subtasks.entrySet());
        return sortedset;
    }

    private boolean isOverlapped(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        if (startTime == null || endTime == null) return false;
        for (Map.Entry<Integer, ? extends Task> entry : getPrioritizedTasks()) {
            if (entry.getValue().getStartTime() == null || entry.getValue().getEndTime() == null){
                continue;
            }
            if (startTime.isEqual(entry.getValue().getStartTime()) || endTime.isEqual(entry.getValue().getEndTime())) {
                return true;
            }
            if (startTime.isAfter(entry.getValue().getEndTime()) || endTime.isBefore(entry.getValue().getStartTime())) {
                continue;
            }
            return startTime.isAfter(entry.getValue().getStartTime())
                    && startTime.isBefore(entry.getValue().getEndTime())
                    || endTime.isAfter(entry.getValue().getStartTime())
                    && endTime.isBefore(entry.getValue().getEndTime());
        }
        return false;
    }
}
