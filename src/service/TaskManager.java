package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        listOfTasks.addAll(tasks.values());
        return listOfTasks;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> listOfEpics = new ArrayList<>();
        listOfEpics.addAll(subtasks.values());
        return listOfEpics;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        listOfEpics.addAll(epics.values());
        return listOfEpics;
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
            epic.deleteAllSubtasks();
        }
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
            epic.addSubtask(newSubtask);
            epic.updateStatus();
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
            epic.addSubtask(updatedSubtask);
            epic.updateStatus();
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {

        subtasks.remove(id);
    }

    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return epic.getSubtasks();
        }
        return new ArrayList<>();
    }
}





