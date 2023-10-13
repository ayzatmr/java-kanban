package models;

import enums.TaskType;

import java.util.Objects;

public class Subtask extends Task {
    // задается для задачи с типом Subtask
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
