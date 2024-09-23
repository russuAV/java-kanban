package managers;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.TaskStatus;
import model.enums.TaskType;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");
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
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getName(), e);
        }
    }

    public String taskToString(Task task) {
        String[] fields = {
                String.valueOf(task.getId()),
                task.getTaskType().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                task.getStartTime() != null ? task.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "",
                (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : ""
        };
        return String.join(",", fields);
    }

    private static Task taskFromString(String value) {
        String[] fields = value.split(",", -1);
        if (fields.length < 7) {
            throw new ManagerSaveException("Некорректный формат строки задачи: " + value);
        }
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].isEmpty() ? null : LocalDateTime.parse(fields[5]);
        Duration duration = fields[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(fields[6]));

        try {
            switch (type) {
                case TASK:
                    return new Task(id, name, description, duration, startTime, status);
                case EPIC:
                    return new Epic(id, name, description, status);
                case SUBTASK:
                    int epicId = Integer.parseInt(fields[7]);
                    return new Subtask(id, name, description, status, duration, startTime, epicId);
                default:
                    throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
            }
        } catch (NumberFormatException e) {
            throw new ManagerSaveException("Ошибка формата данных для строки: " + value, e);
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
        int maxId = 0; // счетчик для обновления значения idCounter после загрузки задач из файла
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // пропускаем заголовок
            String line;
            // Считываем задачи
            while ((line = reader.readLine()) != null && !line.trim().equals("History")) {
                if (!line.trim().isEmpty()) {
                    Task task = taskFromString(line);
                    int taskId = task.getId();
                    if (taskId > maxId) {
                        maxId = taskId;
                    }
                    if (task instanceof Subtask) {
                        manager.addSubtask((Subtask) task);
                    } else if (task instanceof Epic) {
                        manager.addEpic((Epic) task);
                    } else {
                        manager.addTask(task);
                    }
                }
            }
            Task.setIdCounter(maxId + 1);
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
            throw new ManagerSaveException("Ошибка загрузки данных из файла: " + file.getName(), e);
        } catch (NumberFormatException e) {
            throw new ManagerSaveException("Ошибка формата данных в файле: " + file.getName(), e);
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