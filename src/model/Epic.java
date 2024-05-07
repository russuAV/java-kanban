package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtask newSubtask) {
        if (!subtasks.containsKey(newSubtask.getId())) {
            subtasks.put(newSubtask.getId(), newSubtask);
        }
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }
    public void updateStatus() {
        boolean allSubtasksDone = true;
        boolean anySubtaskInProgress = false;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                anySubtaskInProgress = true;
            }
        }

        if (allSubtasksDone) {
            setStatus(TaskStatus.DONE);
        } else if (anySubtaskInProgress) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
