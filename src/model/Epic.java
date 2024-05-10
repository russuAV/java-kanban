package model;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
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
}
