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

        task1.setStatus(TaskStatus.DONE); // меняем стаус задачи и подзадачи
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        System.out.println(task1.getStatus()); // проверяем статусы
        System.out.println(epic1.getStatus());


        taskManager.deleteTaskById(task1.getId()); // удаляем задачу и подзадачу по id
        taskManager.deleteSubtaskById(subtask1.getId());

        System.out.println(taskManager.getAllTasks()); // проверяем удалились ли задача и подзадача
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getSubtasksOfEpic(epic1));
    }
}
