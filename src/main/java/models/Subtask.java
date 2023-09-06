package models;

import java.util.Objects;

public class Subtask extends Task {
    // задается для задачи с типом Subtask
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", taskStatus=" + super.getTaskStatus() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", epicId=" + epicId + '\'' +
                ", viewed='" + isViewed + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
