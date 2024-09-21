package model;

import model.enums.TaskStatus;
import model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private static int idCounter = 1;
    protected int id;
    protected String name;
    protected String description;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected TaskStatus status;
    private final TaskType taskType = TaskType.TASK;

    public Task(String name, String description) {
        this.id = idCounter++;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.id = idCounter++;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = duration;
        this.startTime =startTime;
    }

    public Task(int id, String name, String description, Duration duration, LocalDateTime startTime, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
    }

    public Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plus(duration) : null;
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

    public int getId() {
        return id;
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public static void setIdCounter(int newIdCounter) {
        idCounter = newIdCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task that)) return false;
        return getId() == that.getId() && Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getDuration(), that.getDuration()) &&
                Objects.equals(getStartTime(), that.getStartTime()) &&
                getStatus() == that.getStatus() && taskType == that.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getDuration(), getStartTime(), getStatus(), taskType);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", status=" + status +
                ", taskType=" + taskType +
                '}';
    }
}