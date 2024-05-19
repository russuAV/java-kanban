import managers.InMemoryTaskManager;
import managers.TaskManager;
import model.*;
import model.enums.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        // Создание новой задачи
        Task task1 = new Task("Название задачи 1", "Описание задачи 1");
        Task task2 = new Task("Название задачи 2", "Описание задачи 2");

        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Epiiiic1");

        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("Epic1Subtask", "Subtask2", epic1.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "Epiiiic2");

        manager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Epic2Subtask", "Subtask3", epic2.getId());
        manager.addSubtask(subtask3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        System.out.println("Change status");

        task1.setStatus(TaskStatus.DONE); // меняем стаус задачи и подзадач

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        System.out.println("status subtask1: " + subtask1.getStatus());
        System.out.println("status subtask2: " + subtask2.getStatus());

        System.out.println("Task status: " + task1.getStatus()); // проверяем статусы
        System.out.println("Epic status: " + epic1.getStatus());

        manager.deleteTaskById(task1.getId()); // удаляем задачу и подзадачу по id
        manager.deleteSubtaskById(subtask1.getId());

        System.out.println(manager.getAllTasks()); // проверяем удалились ли задача и подзадача
        System.out.println(manager.getAllSubtasks());

        System.out.println("Удаляем эпик1");
        manager.deleteEpicById(epic1.getId());
        System.out.println(manager.getAllSubtasks());

        manager.deleteAllSubtasks();
        System.out.println(manager.getSubtasksIdsOfEpic(epic1));
    }
}
