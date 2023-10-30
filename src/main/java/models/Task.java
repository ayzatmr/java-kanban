package models;

import enums.TaskStatus;
import enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected int id;
    protected TaskStatus taskStatus;
    protected TaskType taskType;
    protected String name;
    protected String description;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.taskStatus = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.taskStatus = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
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
                duration.equals(task.duration) &&
                startTime.equals(task.startTime) &&
                description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskStatus, taskType, name, description, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}