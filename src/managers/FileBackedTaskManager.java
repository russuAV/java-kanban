package managers;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task addTask(Task newTask) {
        Task task = super.addTask(newTask);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        Epic epic = super.addEpic(newEpic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask newSubtask) {
        Subtask subtask = super.addSubtask(newSubtask);
        save();
        return subtask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task task = super.updateTask(updatedTask);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic epic = super.updateEpic(updatedEpic);
        save();
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        Subtask subtask = super.updateSubtask(updatedSubtask);
        save();
        return subtask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask) + "\n");
            }
            writer.write("History \n");
            writer.write(historyToString(getHistoryManager()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных", e);
        }
    }

    public String taskToString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getId(), subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            return String.format("%d,EPIC,%s,%s,%s,",
                    epic.getId(), epic.getName(), epic.getStatus(), epic.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s,",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription());
        }
    }

    private static Task taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        switch (type) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private String historyToString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        List<String> taskStrings = new ArrayList<>();
        for (Task task : history) {
            taskStrings.add(taskToString(task));
        }
        return String.join("\n", taskStrings);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // пропускаем заголовок
            String line;
            // Считываем задачи
            while ((line = reader.readLine()) != null && !line.trim().equals("History")) {
                if (!line.trim().isEmpty()) {
                    Task task = taskFromString(line);
                    if (task instanceof Subtask) {
                        manager.addSubtask((Subtask) task);
                    } else if (task instanceof Epic) {
                        manager.addEpic((Epic) task);
                    } else {
                        manager.addTask(task);
                    }
                }
            }
            // Считываем историю
            String historyLine;
            HistoryManager historyManager = manager.getHistoryManager();
            while ((historyLine = reader.readLine()) != null && !historyLine.isEmpty()) {
                if (!historyLine.trim().isEmpty()) {
                    Task task = taskFromString(historyLine);
                        historyManager.add(task);
                        manager.save();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки данных", e);
        } catch (NumberFormatException e) {
            throw new ManagerSaveException("Ошибка формата данных", e);
        }
        return manager;
    }

    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task1", "Description task1");
        manager.addTask(task1);

        Epic epic1 = new Epic("Epic1", "Description epic1");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Description subtask1", epic1.getId());
        manager.addSubtask(subtask1);

        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        manager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(manager.getHistory().size());

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
    }
}