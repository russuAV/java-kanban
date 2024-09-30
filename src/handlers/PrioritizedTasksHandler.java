package handlers;

import app.HttpTaskServer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange);
            } else {
                sendError(exchange, 405, "Метод не поддерживается");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Внутренняя ошибка сервера");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendResponse(exchange, response, 200);
    }
}