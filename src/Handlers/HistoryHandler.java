package Handlers;

import app.HttpTaskServer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public HistoryHandler(TaskManager taskManager) {
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
        List<Task> history = taskManager.getHistory();
        String response = gson.toJson(history);
        sendResponse(exchange, response, 200);
    }
}