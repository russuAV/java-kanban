package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileBackedTaskManagerTest {

    private static File file;
    private static FileBackedTaskManager manager;

    @BeforeAll
    static void setUp() {
        try {
            file = File.createTempFile("tasks", ".csv");
            manager = new FileBackedTaskManager(file);

            Task task1 = new Task("Task1", "Description task1");
            manager.addTask(task1);

            Epic epic1 = new Epic("Epic1", "Description epic1");
            manager.addEpic(epic1);

            Subtask subtask1 = new Subtask("Subtask1", "Description subtask1", epic1.getId());
            manager.addSubtask(subtask1);

            manager.getTaskById(task1.getId());
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
        assertEquals(1, loadedManager.getAllTasks().size(), "Количество задач не совпадает");
        assertEquals(1, loadedManager.getAllEpics().size(), "Количество эпиков не совпадает");
        assertEquals(1, loadedManager.getAllSubtasks().size(), "Количество подзадач не совпадает");
        assertEquals(3, loadedManager.getHistory().size(), "Количество элементов в истории не совпадает");
    }
}
