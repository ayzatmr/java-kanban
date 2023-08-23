package models;

import enums.TaskStatus;

import java.util.Objects;

public class Task {

    protected int id;
    protected TaskStatus taskStatus;
    protected String name;
    protected String description;

    public Task(String name, String description) {
        this.taskStatus = TaskStatus.NEW;
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


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskStatus=" + taskStatus +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id
                && taskStatus == task.taskStatus
                && name.equals(task.name)
                && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskStatus, name, description);
    }
}