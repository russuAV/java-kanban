package managers;

import app.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparingInt(Task::getId)
    );

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private boolean isTimeOverlapping(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        for (Task task : prioritizedTasks) {
            if (task.equals(newTask) || task.getStartTime() == null || task.getDuration() == null) {
                continue;
            }

            LocalDateTime existingStart = task.getStartTime();
            LocalDateTime existingEnd = task.getEndTime();

            if (newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        for (Task task : allTasks) {
            historyManager.add(task);
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        List<Subtask> allSubtasks = new ArrayList<>(subtasks.values());
        for (Subtask subtask : allSubtasks) {
            historyManager.add(subtask);
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>(epics.values());
        for (Epic epic : allEpics) {
            historyManager.add(epic);
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        clearCollection(tasks);
    }

    @Override
    public void deleteAllEpics() {
        clearCollection(epics);
        clearCollection(subtasks);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasksIds();
            updateStatus(epic);
            updateEpicTimes(epic);
        }
        clearCollection(subtasks);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Task addTask(Task newTask) {
        if (isTimeOverlapping(newTask)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей.");
        }
        if (newTask.getId() == 0) {
            newTask.setId();
        }
        tasks.put(newTask.getId(), newTask);
        if (newTask.getStartTime() != null && newTask.getDuration() != null) {
            prioritizedTasks.add(newTask);
        }
        return newTask;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        if (newEpic.getId() == 0) {
            newEpic.setId();
        }
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask newSubtask) {
        if (newSubtask.getId() == 0) {
            newSubtask.setId();
        }
        if (epics.containsKey(newSubtask.getEpicId())) {
            if (isTimeOverlapping(newSubtask)) {
                throw new IllegalArgumentException("Подзадача пересекается по времени с существующей.");
            }
            subtasks.put(newSubtask.getId(), newSubtask);
            if (newSubtask.getStartTime() != null && newSubtask.getDuration() != null) {
                prioritizedTasks.add(newSubtask);
            }
            Epic epic = epics.get(newSubtask.getEpicId());
            epic.addSubtaskIds(newSubtask);
            updateStatus(epic);
            updateEpicTimes(epic);
        }
        return newSubtask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            prioritizedTasks.remove(tasks.get(updatedTask.getId()));
            if (isTimeOverlapping(updatedTask)) {
                throw new IllegalArgumentException("Задача пересекается по времени с существующей.");
            }
            tasks.put(updatedTask.getId(), updatedTask);
            if (updatedTask.getStartTime() != null && updatedTask.getDuration() != null) {
                prioritizedTasks.add(updatedTask);
            }
        }
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
            updateEpicTimes(updatedEpic);
            updateStatus(updatedEpic);
        }
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            prioritizedTasks.remove(subtasks.get(updatedSubtask.getId()));
            if (isTimeOverlapping(updatedSubtask)) {
                throw new IllegalArgumentException("Подзадача пересекается по времени с существующей.");
            }
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            if (updatedSubtask.getStartTime() != null && updatedSubtask.getDuration() != null) {
                prioritizedTasks.add(updatedSubtask);
            }
            Epic epic = epics.get(updatedSubtask.getEpicId());
            updateStatus(epic);
            updateEpicTimes(epic);
        }
        return updatedSubtask;
    }

    @Override
    public void updateStatus(Epic epic) {
        epic = epics.get(epic.getId());
        List<Subtask> epicSubtasks = epic.getSubtasksIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = epicSubtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = epicSubtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    private void updateEpicTimes(Epic epic) {
        List<Subtask> epicSubtasks = epic.getSubtasksIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        epic.updateTimes(epicSubtasks);
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                    historyManager.remove(subtaskId);
                }
            }
            epic.deleteAllSubtasksIds();
            historyManager.remove(epic.getId());
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasksIds().remove(Integer.valueOf(id));
                updateStatus(epic);
                updateEpicTimes(epic);
            }
        }
    }

    @Override
    public ArrayList<Integer> getSubtasksIdsOfEpic(Epic epic) {
        return epics.containsKey(epic.getId()) ? new ArrayList<>(epic.getSubtasksIds())
                : new ArrayList<>();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private <T extends Task> void clearCollection(Map<Integer, T> collection) {
        for (T task : collection.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        collection.clear();
    }
}