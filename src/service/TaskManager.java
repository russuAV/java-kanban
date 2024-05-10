package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasksIds();
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void addTask(Task newTask) {
        tasks.put(newTask.getId(), newTask);
    }

    public void addEpic(Epic newEpic) {
        epics.put(newEpic.getId(), newEpic);
    }

    public void addSubtask(Subtask newSubtask) {
        if (epics.containsKey(newSubtask.getEpicId())) {
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic epic = epics.get(newSubtask.getEpicId());
            epic.addSubtaskIds(newSubtask);
            updateStatus(epic);
        }
    }

    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    public void updateSubtask(Subtask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = epics.get(updatedSubtask.getEpicId());
            epic.addSubtaskIds(updatedSubtask);
            updateStatus(epic);
        }
    }

    public void updateStatus(Epic epic) {
        epic = epics.get(epic.getId());
        boolean allSubtasksDone = true;

        for (Integer subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && subtask.getStatus() != TaskStatus.DONE) {
                allSubtasksDone = false;
                break;
            }
        }
        if (allSubtasksDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksIds()) {
                subtasks.remove(subtaskId);
            }
            epic.deleteAllSubtasksIds();
            epics.remove(id);
        }
    }

    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
    }

    public ArrayList<Integer> getSubtasksIdsOfEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return epic.getSubtasksIds();
        }
        return new ArrayList<>();
    }
}