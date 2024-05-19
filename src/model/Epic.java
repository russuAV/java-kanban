package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtaskIds = epic.subtaskIds;
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtaskIds;
    }

    public void addSubtaskIds(Subtask newSubtask) {
        if (!subtaskIds.contains(newSubtask.getId())) {
            subtaskIds.add(newSubtask.getId());
        }
    }

    public void deleteAllSubtasksIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}
