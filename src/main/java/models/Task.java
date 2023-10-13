package models;

import enums.TaskStatus;
import enums.TaskType;

import java.util.Objects;

public class Task {

    protected int id;
    protected TaskStatus taskStatus;
    protected TaskType taskType;
    protected String name;
    protected String description;

    public Task(String name, String description) {
        this.taskStatus = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
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

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id &&
                taskStatus == task.taskStatus &&
                taskType == task.taskType &&
                name.equals(task.name) &&
                description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskStatus, taskType, name, description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}