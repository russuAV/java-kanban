package handlers;

import app.HttpTaskServer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public abstract class AbstractTaskHandler<T extends Task> extends BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = HttpTaskServer.getGson();

    public AbstractTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected abstract List<T> getAllTasks();

    protected abstract T getTaskById(int id);

    protected abstract void addTask(T task);

    protected abstract void updateTask(T task);

    protected abstract void deleteTaskById(int id);

    protected abstract void deleteAllTasks();

    protected abstract Class<T> getTaskClass();

    protected boolean checkThatCollectionHasSameId(int id) {
        return false;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, query);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, query);
                    break;
                default:
                    sendError(exchange, 405, "Неизвестный метод");
            }
        } catch (NumberFormatException e) {
            sendError(exchange, 400, "Некорректный формат числа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, "Некорректные данные: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Внутренняя ошибка сервера");
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query != null && query.contains("id=")) {
            int id = parseId(query);
            T task = getTaskById(id);
            if (task != null) {
                String response = gson.toJson(task);
                sendResponse(exchange, response, 200);
            } else {
                sendError(exchange, 404, "Задача не найдена.");
            }
        } else {
            List<T> tasks = getAllTasks();
            String response = gson.toJson(tasks);
            sendResponse(exchange, response, 200);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        T task = gson.fromJson(body, getTaskClass());
        try {
            if (task == null) {
                sendError(exchange, 400, "Тело запроса пустое или содержит некорректные данные.");
                return;
            }
            if (!checkThatCollectionHasSameId(task.getId())) {
                addTask(task);
                sendResponse(exchange, "Задача создана", 201);
            } else {
                updateTask(task);
                sendResponse(exchange, "Задача обновлена", 200);
            }
        } catch (IllegalArgumentException e) {
            sendError(exchange, 406, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query != null && query.contains("id=")) {
            int id = parseId(query);
            deleteTaskById(id);
            sendResponse(exchange, "Задача удалена", 200);
        } else {
            deleteAllTasks();
            sendResponse(exchange, "Все задачи удалены", 200);
        }
    }

    private int parseId(String query) throws NumberFormatException {
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("id=")) {
                return Integer.parseInt(param.substring(3));
            }
        }
        throw new NumberFormatException("Некорректный параметр id");
    }
}