import app.Managers;
import managers.TaskManager;
import model.*;

public class Main {
    static TaskManager manager = Managers.getDefault();

    public static void main(String[] args) {
        // Создание новой задачи
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");

        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Epiiiic1");
        Subtask subtask1 = new Subtask("Epic1Subtask1", "Subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("Epic1Subtask2", "Subtask2", epic1.getId());
        Subtask subtask3 = new Subtask("Epic1Subtask3", "Subtask3", epic1.getId());

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        Epic epic2 = new Epic("Epic2", "Epiiiic2");
        manager.addEpic(epic2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getEpicById(epic2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        System.out.println("История задач:");
        printHistory();

        System.out.println("История задач после удаления задачи 'task1'");
        manager.deleteTaskById(task1.getId());
        printHistory();

        System.out.println("История задач после удаления эпика 'epic1'");
        manager.deleteEpicById(epic1.getId());
        printHistory();
    }

    public static void printHistory() {
        for (Task task : manager.getHistory()) {
            System.out.println(task.getName());
        }
    }
}
