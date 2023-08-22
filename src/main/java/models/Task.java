package models;

import enums.TaskStatus;
import enums.TaskType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {

    private static final AtomicInteger uniqueId = new AtomicInteger();
    private int id;
    private TaskStatus taskStatus;
    private TaskType taskType;
    private String name;
    private String description;

    // задается для задачи с типом Subtask
    private Integer epicId;

    // задается для задачи с типом Epic
    private ArrayList<Task> subtasks = new ArrayList<>();

    public Task(TaskType taskType, String name, String description, Integer epicId) {
        this.id = uniqueId.incrementAndGet();
        this.taskStatus = TaskStatus.NEW;
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.epicId = epicId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public ArrayList<Task> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Task> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id
                && epicId.equals(task.epicId)
                && taskStatus == task.taskStatus
                && taskType == task.taskType
                && name.equals(task.name)
                && description.equals(task.description)
                && Objects.equals(subtasks, task.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskStatus, taskType, name, description, epicId, subtasks);
    }
}
