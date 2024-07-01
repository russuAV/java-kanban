package managers;

import app.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    public void addManager() {
        manager = Managers.getDefault();
    }

    @Test
    public void checkThatAppAddTaskAndGiveById() {
        Task task1 = new Task("Op1", "Op1");
        manager.addTask(task1);
        Assertions.assertEquals(task1, manager.getTaskById(task1.getId()));
    }

    @Test
    public void checkThatAppAddEpicAndGiveById() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        Assertions.assertEquals(epic1, manager.getEpicById(epic1.getId()));
    }

    @Test
    public void checkThatAppAddESubtaskAndGiveById() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        manager.addSubtask(subtask1);
        Assertions.assertEquals(subtask1, manager.getSubtaskById(subtask1.getId()));
    }

    @Test
    public void testTaskImmutabilityAfterAdd() {
        Task task1 = new Task("Op1", "Op1");
        manager.addTask(task1);
        String originalTaskName = task1.getName();
        String originalTaskDescription = task1.getDescription();

        Assertions.assertEquals(originalTaskName, manager.getTaskById(task1.getId()).getName());
        Assertions.assertEquals(originalTaskDescription, manager.getTaskById(task1.getId()).getDescription());
    }

    @Test
    public void testEpicImmutabilityAfterAdd() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        String originalEpicName = epic1.getName();
        String originalEpicDescription = epic1.getDescription();

        Assertions.assertEquals(originalEpicName, manager.getEpicById(epic1.getId()).getName());
        Assertions.assertEquals(originalEpicDescription, manager.getEpicById(epic1.getId()).getDescription());
    }

    @Test
    public void testSubtaskImmutabilityAfterAdd() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        manager.addSubtask(subtask1);
        String originalSubtaskName = subtask1.getName();
        String originalSubtaskDescription = subtask1.getDescription();
        int originalSubtaskId = subtask1.getId();

        Assertions.assertEquals(originalSubtaskName, manager.getSubtaskById(subtask1.getId()).getName());
        Assertions.assertEquals(originalSubtaskDescription, manager.getSubtaskById(subtask1.getId()).getDescription());
        Assertions.assertEquals(originalSubtaskId, manager.getSubtaskById(subtask1.getId()).getId());
    }

    @Test
    public void shouldRemoveTask() {
        Task task1 = new Task("Op1", "Op1");
        manager.addTask(task1);
        manager.deleteTaskById(task1.getId());

        Assertions.assertEquals(0, manager.getAllTasks().size());
    }
}