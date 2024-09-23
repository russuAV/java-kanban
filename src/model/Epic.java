package model;

import model.enums.TaskStatus;
import model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();
    private final TaskType taskType = TaskType.EPIC;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, Duration.ZERO, null, status);
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtaskIds = new ArrayList<>(epic.subtaskIds);
        this.duration = epic.duration;
        this.startTime = epic.startTime;
        this.endTime = epic.endTime;
    }

    public ArrayList<Integer> getSubtasksIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskIds(Subtask newSubtask) {
        if (!subtaskIds.contains(newSubtask.getId())) {
            subtaskIds.add(newSubtask.getId());
        }
    }

    public void deleteAllSubtasksIds() {
        subtaskIds.clear();
        startTime = null;
        endTime = null;
        duration = Duration.ZERO;
    }

    public void updateTimes(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            startTime = null;
            endTime = null;
            duration = Duration.ZERO;
            return;
        }

        startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        duration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);

    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", taskType=" + taskType +
                ", endTime=" + endTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic epic)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(subtaskIds, epic.subtaskIds) &&
                taskType == epic.taskType && Objects.equals(getEndTime(), epic.getEndTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, taskType, getEndTime());
    }
}
