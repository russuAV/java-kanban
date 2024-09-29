package app;

import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        client = HttpClient.newHttpClient();
        gson = HttpTaskServer.getGson();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        // Создаём задачу
        Task task = new Task("Test Task", "Test Description",
                Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        // Отправляем POST-запрос для добавления задачи
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(201, response.statusCode(), "Задача не была создана успешно.");

        // Проверяем, что задача добавлена в менеджер задач
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size(), "Количество задач не соответствует ожидаемому.");
        assertEquals("Test Task", tasks.get(0).getName(), "Имя задачи не соответствует ожидаемому.");
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        // Предварительно добавляем задачу в менеджер
        Task task = new Task("Test Task", "Test Description",
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        // Отправляем GET-запрос для получения списка задач
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Не удалось получить список задач.");

        // Десериализуем ответ и проверяем содержимое
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, tasks.length, "Количество задач в ответе не соответствует ожидаемому.");
        assertEquals("Test Task", tasks[0].getName(), "Имя задачи в ответе не соответствует ожидаемому.");
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        // Добавляем задачу в менеджер
        Task originalTask = new Task("Initial Task", "Initial Description",
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(originalTask);

        // Создаем новый объект задачи с тем же ID, но с обновленными данными
        Task updatedTask = new Task("Updated Task", "Updated Description",
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        updatedTask.setId(originalTask.getId());

        // Сериализуем обновленную задачу
        String taskJson = gson.toJson(updatedTask);

        // Отправляем POST-запрос для обновления задачи
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Задача не была обновлена успешно.");

        // Получаем задачу из менеджера задач и проверяем изменения
        Task taskFromManager = taskManager.getTaskById(originalTask.getId());
        assertNotNull(taskFromManager, "Задача не найдена в менеджере.");
        assertEquals("Updated Task", taskFromManager.getName(), "Имя задачи не обновлено.");
        assertEquals("Updated Description", taskFromManager.getDescription(), "Описание задачи не обновлено.");
        assertEquals(Duration.ofMinutes(45), taskFromManager.getDuration(), "Длительность задачи не обновлена.");
        assertEquals(LocalDateTime.now().plusHours(1).withNano(0), taskFromManager.getStartTime().withNano(0), "Время начала задачи не обновлено.");
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        // Добавляем задачу в менеджер
        Task task = new Task("Task to Delete", "Description",
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        // Отправляем DELETE-запрос для удаления задачи
        URI url = URI.create("http://localhost:8080/tasks?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Задача не была удалена успешно.");

        // Проверяем, что задача удалена из менеджера
        Task deletedTask = taskManager.getTaskById(task.getId());
        assertNull(deletedTask, "Задача не была удалена из менеджера.");
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        // Добавляем задачу в менеджер
        Task task = new Task("Task", "Description",
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        // Отправляем GET-запрос для получения задачи по ID
        URI url = URI.create("http://localhost:8080/tasks?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Не удалось получить задачу по ID.");

        // Десериализуем ответ и проверяем содержимое
        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), returnedTask.getId(), "ID задачи не соответствует ожидаемому.");
        assertEquals(task.getName(), returnedTask.getName(), "Имя задачи не соответствует ожидаемому.");
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        // Создаём эпик
        Epic epic = new Epic("Test Epic", "Epic Description");
        String epicJson = gson.toJson(epic);

        // Отправляем POST-запрос для добавления эпика
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(201, response.statusCode(), "Эпик не был создан успешно.");

        // Проверяем, что эпик добавлен в менеджер задач
        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size(), "Количество эпиков не соответствует ожидаемому.");
        assertEquals("Test Epic", epics.get(0).getName(), "Имя эпика не соответствует ожидаемому.");
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        // Предварительно добавляем эпик в менеджер
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        // Отправляем GET-запрос для получения списка эпиков
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Не удалось получить список эпиков.");

        // Десериализуем ответ и проверяем содержимое
        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(1, epics.length, "Количество эпиков в ответе не соответствует ожидаемому.");
        assertEquals("Test Epic", epics[0].getName(), "Имя эпика в ответе не соответствует ожидаемому.");
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        // Добавляем эпик в менеджер
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        // Отправляем GET-запрос для получения эпика по ID
        URI url = URI.create("http://localhost:8080/epics?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Не удалось получить эпик по ID.");

        // Десериализуем ответ и проверяем содержимое
        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getId(), returnedEpic.getId(), "ID эпика не соответствует ожидаемому.");
        assertEquals(epic.getName(), returnedEpic.getName(), "Имя эпика не соответствует ожидаемому.");
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        // Добавляем эпик в менеджер
        Epic originalEpic = new Epic("Initial Epic", "Initial Description");
        taskManager.addEpic(originalEpic);

        // Создаём новый объект эпика с тем же ID, но изменёнными данными
        Epic updatedEpic = new Epic("Updated Epic", "Updated Description");
        updatedEpic.setId(originalEpic.getId());

        // Сериализуем обновлённый эпик
        String epicJson = gson.toJson(updatedEpic);

        // Отправляем POST-запрос для обновления эпика
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Эпик не был обновлён успешно.");

        // Получаем эпик из менеджера задач и проверяем изменения
        Epic epicFromManager = taskManager.getEpicById(originalEpic.getId());
        assertNotNull(epicFromManager, "Эпик не найден в менеджере.");
        assertEquals("Updated Epic", epicFromManager.getName(), "Имя эпика не обновлено.");
        assertEquals("Updated Description", epicFromManager.getDescription(), "Описание эпика не обновлено.");
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        // Добавляем эпик в менеджер
        Epic epic = new Epic("Epic to Delete", "Description");
        taskManager.addEpic(epic);

        // Отправляем DELETE-запрос для удаления эпика
        URI url = URI.create("http://localhost:8080/epics?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Эпик не был удален успешно.");

        // Проверяем, что эпик удален из менеджера
        Epic deletedEpic = taskManager.getEpicById(epic.getId());
        assertNull(deletedEpic, "Эпик не был удален из менеджера.");
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to Delete", "Description");
        taskManager.addEpic(epic);
        // Создаём подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description",
                Duration.ofMinutes(60), LocalDateTime.now(), epic.getId());
        String subtaskJson = gson.toJson(subtask);

        // Отправляем POST-запрос для добавления подзадачи
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(201, response.statusCode(), "Подзадача не была создана успешно.");

        // Проверяем, что подзадача добавлена в менеджер задач
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(1, subtasks.size(), "Количество подзадач не соответствует ожидаемому.");
        assertEquals("Test Subtask", subtasks.get(0).getName(), "Имя подзадачи не соответствует ожидаемому.");
    }

    @Test
    void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to Delete", "Description");
        taskManager.addEpic(epic);
        // Предварительно добавляем подзадачу в менеджер
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description",
                Duration.ofMinutes(60), LocalDateTime.now(), epic.getId());
        taskManager.addSubtask(subtask);

        // Отправляем GET-запрос для получения списка подзадач
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Не удалось получить список подзадач.");

        // Десериализуем ответ и проверяем содержимое
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(1, subtasks.length, "Количество подзадач в ответе не соответствует ожидаемому.");
        assertEquals("Test Subtask", subtasks[0].getName(), "Имя подзадачи в ответе не соответствует ожидаемому.");
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to Delete", "Description");
        taskManager.addEpic(epic);
        // Добавляем подзадачу в менеджер
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description",
                Duration.ofMinutes(60), LocalDateTime.now(), epic.getId());
        taskManager.addSubtask(subtask);

        // Отправляем GET-запрос для получения подзадачи по ID
        URI url = URI.create("http://localhost:8080/subtasks?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Не удалось получить подзадачу по ID.");

        // Десериализуем ответ и проверяем содержимое
        Subtask returnedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask.getId(), returnedSubtask.getId(), "ID подзадачи не соответствует ожидаемому.");
        assertEquals(subtask.getName(), returnedSubtask.getName(), "Имя подзадачи не соответствует ожидаемому.");
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        // Создаём и добавляем эпик
        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.addEpic(epic);

        // Создаём и добавляем подзадачу
        Subtask originalSubtask = new Subtask("Initial Subtask", "Initial Description",
                Duration.ofMinutes(60), LocalDateTime.now(), epic.getId());
        taskManager.addSubtask(originalSubtask);

        // Создаём новый объект подзадачи с тем же ID, но изменёнными данными
        Subtask updatedSubtask = new Subtask("Updated Subtask", "Updated Description",
                Duration.ofMinutes(90), LocalDateTime.now().plusHours(1), epic.getId());
        updatedSubtask.setId(originalSubtask.getId());

        // Сериализуем обновлённую подзадачу
        String subtaskJson = gson.toJson(updatedSubtask);

        // Отправляем POST-запрос для обновления подзадачи
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Подзадача не была обновлена успешно.");

        // Получаем подзадачу из менеджера задач и проверяем изменения
        Subtask subtaskFromManager = taskManager.getSubtaskById(originalSubtask.getId());
        assertNotNull(subtaskFromManager, "Подзадача не найдена в менеджере.");
        assertEquals("Updated Subtask", subtaskFromManager.getName(),
                "Имя подзадачи не обновлено.");
        assertEquals("Updated Description", subtaskFromManager.getDescription(),
                "Описание подзадачи не обновлено.");
        assertEquals(Duration.ofMinutes(90), subtaskFromManager.getDuration(),
                "Длительность подзадачи не обновлена.");
        assertEquals(LocalDateTime.now().plusHours(1).withNano(0),
                subtaskFromManager.getStartTime().withNano(0),
                "Время начала подзадачи не обновлено.");
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to Delete", "Description");
        taskManager.addEpic(epic);
        // Добавляем подзадачу в менеджер
        Subtask subtask = new Subtask("Subtask to Delete", "Description",
                Duration.ofMinutes(60), LocalDateTime.now(), epic.getId());
        taskManager.addSubtask(subtask);

        // Отправляем DELETE-запрос для удаления подзадачи
        URI url = URI.create("http://localhost:8080/subtasks?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Подзадача не была удалена успешно.");

        // Проверяем, что подзадача удалена из менеджера
        Subtask deletedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNull(deletedSubtask, "Подзадача не была удалена из менеджера.");
    }
}