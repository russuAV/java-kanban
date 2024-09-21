package managers;

import app.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(task1, manager.getTaskById(task1.getId()));
    }

    @Test
    public void checkThatAppAddEpicAndGiveById() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        assertEquals(epic1, manager.getEpicById(epic1.getId()));
    }

    @Test
    public void checkThatAppAddSubtaskAndGiveById() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Epic1Subtask", "Subtask1", epic1.getId());
        manager.addSubtask(subtask1);
        assertEquals(subtask1, manager.getSubtaskById(subtask1.getId()));
    }

    @Test
    public void testTaskImmutabilityAfterAdd() {
        Task task1 = new Task("Op1", "Op1");
        manager.addTask(task1);
        String originalTaskName = task1.getName();
        String originalTaskDescription = task1.getDescription();

        assertEquals(originalTaskName, manager.getTaskById(task1.getId()).getName());
        assertEquals(originalTaskDescription, manager.getTaskById(task1.getId()).getDescription());
    }

    @Test
    public void testEpicImmutabilityAfterAdd() {
        Epic epic1 = new Epic("epic1", "epic1");
        manager.addEpic(epic1);
        String originalEpicName = epic1.getName();
        String originalEpicDescription = epic1.getDescription();

        assertEquals(originalEpicName, manager.getEpicById(epic1.getId()).getName());
        assertEquals(originalEpicDescription, manager.getEpicById(epic1.getId()).getDescription());
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

        assertEquals(originalSubtaskName, manager.getSubtaskById(subtask1.getId()).getName());
        assertEquals(originalSubtaskDescription, manager.getSubtaskById(subtask1.getId()).getDescription());
        assertEquals(originalSubtaskId, manager.getSubtaskById(subtask1.getId()).getId());
    }

    @Test
    public void shouldRemoveTask() {
        Task task1 = new Task("Op1", "Op1");
        manager.addTask(task1);
        manager.deleteTaskById(task1.getId());

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldDeleteEpicAndSubtasks() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Description2", epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getEpicById(epic.getId()));
        assertNull(manager.getSubtaskById(subtask1.getId()));
        assertNull(manager.getSubtaskById(subtask2.getId()));
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("Task1", "Description1");
        manager.addTask(task);

        Task updatedTask = new Task(task.getId(), "Updated Task", "Updated Description",
                task.getStatus());
        manager.updateTask(updatedTask);

        Task retrievedTask = manager.getTaskById(task.getId());
        assertEquals("Updated Task", retrievedTask.getName());
        assertEquals("Updated Description", retrievedTask.getDescription());
    }

    @Test
    public void shouldReturnTasksInPriorityOrder() {
        Task task1 = new Task("Task1", "Description1", Duration.ofHours(1),
                LocalDateTime.of(2023, 1, 1, 10, 0));
        Task task2 = new Task("Task2", "Description2", Duration.ofHours(1),
                LocalDateTime.of(2023, 1, 1, 9, 0));
        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(task2, prioritizedTasks.get(0));
        assertEquals(task1, prioritizedTasks.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenAddingOverlappingTask() {
        Task task1 = new Task("Task1", "Description1", Duration.ofHours(2),
                LocalDateTime.of(2023, 1, 1, 10, 0));
        Task task2 = new Task("Task2", "Description2", Duration.ofHours(1),
                LocalDateTime.of(2023, 1, 1, 11, 0));

        manager.addTask(task1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addTask(task2);
        });

        assertEquals("Задача пересекается по времени с существующей.", exception.getMessage());
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void testEpicEndTimeCalculation() {
        Epic epic = new Epic("Epic Test", "Description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description", Duration.ofHours(1),
                LocalDateTime.of(2023, 1, 1, 9, 0), epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Description", Duration.ofHours(2),
                LocalDateTime.of(2023, 1, 1, 12, 0),  epic.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        LocalDateTime expectedEndTime = LocalDateTime.of(2023, 1, 1, 14, 0);
        assertEquals(expectedEndTime, epic.getEndTime(), "Время завершения эпика неверно рассчитано");
    }

    @Test
    public void testTaskWithoutStartTimeDoesNotCausePrioritizationError() {
        Task task = new Task("Задача без времени", "Задача без времени старта",
                null, null);
        manager.addTask(task);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertFalse(prioritizedTasks.contains(task),
                "Задача без времени старта не должна участвовать в приоритезации");
    }

    @Test
    public void testEpicStatusWithAllSubtasksDone() {
        Epic epic = new Epic("Epic Test", "Description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Description", epic.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        manager.updateEpic(epic);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    public void shouldReturnNewWhenAllSubtasksAreNew() {
        Epic epic = new Epic("Epic", "Test Epic");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, manager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть NEW, если все подзадачи имеют статус NEW");
    }

    @Test
    public void shouldReturnInProgressWhenSubtasksAreNewAndDone() {
        Epic epic = new Epic("Epic", "Test Epic");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());

        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.DONE);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если подзадачи имеют статусы NEW и DONE");
    }

    @Test
    public void shouldReturnInProgressWhenAllSubtasksAreInProgress() {
        Epic epic = new Epic("Epic", "Test Epic");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если все подзадачи имеют статус IN_PROGRESS");
    }
}