package managers;

import app.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
}