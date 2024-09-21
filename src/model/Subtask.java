package model;

import model.enums.TaskStatus;
import model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;
    private final TaskType taskType = TaskType.SUBTASK;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicId = subtask.epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, Duration.ZERO, null, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, Duration duration,
                   LocalDateTime startTime, int epicId) {
        super(id, name, description, duration, startTime, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask subtask)) return false;
        if (!super.equals(o)) return false;
        return getEpicId() == subtask.getEpicId() && taskType == subtask.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId(), taskType);
    }
}


