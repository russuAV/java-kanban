import service.TaskManager;
import model.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        // Создание новой задачи
        Task task1 = new Task("Название задачи 1", "Описание задачи 1");
        Task task2 = new Task("Название задачи 2", "Описание задачи 2");

        taskManager.addTask(task1);
        taskManager.addTask(task2);


        Epic epic1 = new Epic("Epic1", "Epiiiic1");

        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("Epic1Subtask", "Subtask2", epic1.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "Epiiiic2");

        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Epic2Subtask", "Subtask3", epic2.getId());
        taskManager.addSubtask(subtask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("Change status");

        task1.setStatus(TaskStatus.DONE); // меняем стаус задачи и подзадач

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        System.out.println("status subtask1: " + subtask1.getStatus());
        System.out.println("status subtask2: " + subtask2.getStatus());

        System.out.println("Task status: " + task1.getStatus()); // проверяем статусы
        System.out.println("Epic status: " + epic1.getStatus());

        taskManager.deleteTaskById(task1.getId()); // удаляем задачу и подзадачу по id
        taskManager.deleteSubtaskById(subtask1.getId());

        System.out.println(taskManager.getAllTasks()); // проверяем удалились ли задача и подзадача
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("Удаляем эпик1");
        taskManager.deleteEpicById(epic1.getId());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getSubtasksIdsOfEpic(epic1));
    }
}
