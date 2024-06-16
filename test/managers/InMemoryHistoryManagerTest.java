package managers;

import app.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class InMemoryHistoryManagerTest {
    private TaskManager manager;

    @BeforeEach
    public void addManager() {
        manager = Managers.getDefault();
    }

    @Test
    public void checkThatNullDoesNotAddToHistory() {
        Task task1 = new Task("Task1", "Task1");
        manager.addTask(task1);
        manager.getTaskById(task1.getId());

        Assertions.assertNotNull(manager.getHistory());
    }

    @Test
    public void checkThatEpicNotChangeInHistoryList() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        manager.getEpicById(epic1.getId());
        epic1.setName("epic1New");
        manager.updateEpic(epic1);

        Assertions.assertNotEquals(manager.getHistory().getFirst().getName(),
                manager.getEpicById(epic1.getId()).getName());
    }

    @Test
    public void checkThatSubtaskNotChangeInHistoryList() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        manager.addSubtask(subtask1);
        manager.getSubtaskById(subtask1.getId());
        subtask1.setName("Epic1SubtaskNew");
        manager.updateSubtask(subtask1);

        Assertions.assertNotEquals(manager.getHistory().getFirst().getName(),
                manager.getSubtaskById(subtask1.getId()).getName());
    }

    @Test
    public void checkThatTaskNotChangeInHistoryList() {
        Task task1 = new Task("Task1", "Task1");
        manager.addTask(task1);
        manager.getTaskById(task1.getId());
        task1.setName("Task1New");
        manager.updateTask(task1);

        Assertions.assertNotEquals(manager.getHistory().getFirst().getName(),
                manager.getTaskById(task1.getId()).getName());
    }

    @Test
    public void addDuplicateTask() {
        Task task1 = new Task("Task1", "Task1");
        manager.addTask(task1);
        manager.getTaskById(task1.getId());

        assertEquals(1, manager.getHistory().size());

        manager.addTask(task1);
        manager.getTaskById(task1.getId());

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    public void addDuplicateEpic() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        manager.getEpicById(epic1.getId());

        assertEquals(1, manager.getHistory().size());

        manager.addEpic(epic1);
        manager.getEpicById(epic1.getId());

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    public void addDuplicateSubtask() {
        Epic epic1 = new Epic("epic1", "epic1");
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());


        assertEquals(2, manager.getHistory().size());

        manager.addSubtask(subtask1);
        manager.getSubtaskById(subtask1.getId());

        assertEquals(2, manager.getHistory().size());
    }

    @Test
    public void removeSubtaskAndEpic() {
        Epic epic1 = new Epic("epic1", "epic1");
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        assertEquals(2, manager.getHistory().size());

        manager.deleteEpicById(epic1.getId());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void removeAllTasks() {
        Task task1 = new Task("Task1", "Task1");
        Task task2 = new Task("Task2", "Task2");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        assertEquals(2, manager.getHistory().size());

        manager.deleteAllTasks();
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void removeAllEpics() {
        Epic epic1 = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());

        assertEquals(2, manager.getHistory().size());

        manager.deleteAllEpics();
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void removeAllSubtasks() {
        Epic epic1 = new Epic("epic1", "epic1");
        Subtask subtask1 = new Subtask("Epic1Subtask1", "Subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("Epic1Subtask2", "Subtask2", epic1.getId());
        Subtask subtask3 = new Subtask("Epic1Subtask3", "Subtask3", epic1.getId());

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask3.getId());


        assertEquals(4, manager.getHistory().size());

        manager.deleteAllSubtasks();
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    public void removeTaskById() {
        Task task1 = new Task("Task1", "Task1");

        manager.addTask(task1);
        manager.getTaskById(task1.getId());

        assertEquals(1, manager.getHistory().size());

        manager.deleteTaskById(task1.getId());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void removeEpicById() {
        Epic epic1 = new Epic("epic1", "epic1");

        manager.addEpic(epic1);
        manager.getEpicById(epic1.getId());

        assertEquals(1, manager.getHistory().size());

        manager.deleteEpicById(epic1.getId());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void removeSubtaskById() {
        Epic epic1 = new Epic("epic1", "epic1");
        Subtask subtask1 = new Subtask("Epic1Subtask1", "Subtask1", epic1.getId());

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        assertEquals(2, manager.getHistory().size());

        manager.deleteSubtaskById(subtask1.getId());
        assertEquals(1, manager.getHistory().size());
    }
}