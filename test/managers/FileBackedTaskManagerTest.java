package managers;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private static File file;
    private static FileBackedTaskManager manager;

    @BeforeAll
    static void setUp() {
        try {
            file = File.createTempFile("tasks", ".csv");
            manager = new FileBackedTaskManager(file);

            Task task1 = new Task("Task1", "Description1", Duration.ofHours(2),
                    LocalDateTime.of(2023, 1, 1, 10, 0));
            Task task2 = new Task("Task2", "Description2", Duration.ofHours(1),
                    LocalDateTime.of(2023, 1, 1, 9, 0));
            Task task3 = new Task("Task3", "DescriptionTask3");

            manager.addTask(task1);
            manager.addTask(task2);
            manager.addTask(task3);

            Epic epic1 = new Epic("Epic1", "Description epic1");
            manager.addEpic(epic1);

            Subtask subtask1 = new Subtask("Subtask1", "Description subtask1", epic1.getId());
            manager.addSubtask(subtask1);

            manager.getAllTasks();
            manager.getEpicById(epic1.getId());
            manager.getSubtaskById(subtask1.getId());

        } catch (IOException e) {
            fail("Не удалось создать временный файл для тестов");
        }
    }

    @AfterAll
    static void tearDown() {
        file.delete();
    }

    @Test
    void testLoadFromFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = manager.getAllTasks();

        assertEquals(3, loadedManager.getAllTasks().size(),
                "Количество задач не совпадает");
        assertEquals(1, loadedManager.getAllEpics().size(),
                "Количество эпиков не совпадает");
        assertEquals(1, loadedManager.getAllSubtasks().size(),
                "Количество подзадач не совпадает");
        assertEquals(5, loadedManager.getHistory().size(),
                "Количество элементов в истории не совпадает");

        Task loadedTask1 = tasks.get(0);
        Task loadedTask2 = tasks.get(1);
        Task loadedTask3 = tasks.get(2);

        assertNotNull(loadedTask1, "Задача отсуствует");
        assertEquals(LocalDateTime.of(2023,1,1,10,0), loadedTask1.getStartTime(),
                "Время старта задачи не совпадает");
        assertEquals(Duration.ofHours(2), loadedTask1.getDuration(),
                "Продолжительность задачи не совпадает");
        assertEquals(LocalDateTime.of(2023,1,1,9,0), loadedTask2.getStartTime(),
                "Время старта задачи не совпадает");
        assertEquals(Duration.ofHours(1), loadedTask2.getDuration(),
                "Продолжительность задачи не совпадает");
        assertNull(loadedTask3.getStartTime());
        assertNull(loadedTask3.getDuration());
    }
}
