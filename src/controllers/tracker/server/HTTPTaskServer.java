package controllers.tracker.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;



public class HTTPTaskServer {

    private final HTTPTaskManager httpTaskManager;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();
    private final HttpServer server;

    public HTTPTaskServer() throws IOException {
        httpTaskManager = new HTTPTaskManager("http://localhost:8078");
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handle);
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String uri = httpExchange.getRequestURI().toString();
        if (uri.endsWith("tasks/task/") && httpExchange.getRequestMethod().equals("GET")) {
            taskGetContext(httpExchange);

        } else if (uri.contains("id") && httpExchange.getRequestMethod().equals("GET")
                && httpExchange.getRequestURI().getPath().endsWith("tasks/task/")) {
            taskGetByIdContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("POST") && uri.endsWith("tasks/task/")) {
            taskPostContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.endsWith("tasks/task/")) {
            taskClearContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("tasks/task/")) {
            taskDeleteByIdContext(httpExchange);

        } else if (uri.endsWith("subtask/") && httpExchange.getRequestMethod().equals("GET")) {
            subtaskGetContext(httpExchange);

        } else if (uri.contains("id") && httpExchange.getRequestMethod().equals("GET")
                && httpExchange.getRequestURI().getPath().endsWith("subtask/")) {
            subtaskGetByIdContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("POST") &&
                uri.endsWith("subtask/")) {
            subtaskPostContext(httpExchange);
        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.endsWith("/subtask/")) {
            subtaskClearContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("subtask/")) {
            subtaskDeleteByIdContext(httpExchange);

        } else if (uri.endsWith("epic/")
                && httpExchange.getRequestMethod().equals("GET")) {
            epicGetContext(httpExchange);
        } else if (uri.contains("id") && httpExchange.getRequestMethod().equals("GET")
                && httpExchange.getRequestURI().getPath().endsWith("tasks/epic/")) {
            epicGetByIdContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("POST") && uri.endsWith("epic/")) {
            epicPostContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.endsWith("/epic/")) {
            epicClearContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("epic/")) {
            epicDeleteByIdContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("GET") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("/subtask/epic/")) {
            subtaskOfEpicContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("GET")
                && httpExchange.getRequestURI().getPath().endsWith("/tasks/history/")) {
            getHistoryContext(httpExchange);

        } else if (httpExchange.getRequestMethod().equals("GET")
                && httpExchange.getRequestURI().getPath().endsWith("/tasks/")) {
            getPrioritizedTasksContext(httpExchange);

        }
    }

    private void taskGetContext(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(gson.toJson(httpTaskManager.getTaskList()).getBytes(DEFAULT_CHARSET));
        }
    }

    private void taskGetByIdContext(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
        if (httpTaskManager.getTaskById(id) == null) {
            httpExchange.sendResponseHeaders(400, 0);
            System.out.println("Задачи с таким айди не существует");
            httpExchange.close();
        } else {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(httpTaskManager.getTaskById(id)).getBytes(DEFAULT_CHARSET));
            }
        }
    }

    private void taskPostContext(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(body, new TypeToken<Task>() {
        }.getType());
        if (body.contains("\"id\":0") || !body.contains("\"id\":")) { //если есть айди - обновление, нет - добавление новой
            httpTaskManager.createNewTask(task);
        } else {
            httpTaskManager.updateTask(task);
        }

        httpExchange.sendResponseHeaders(201, 0);
        httpExchange.close();
    }

    private void taskClearContext(HttpExchange httpExchange) throws IOException {
        httpTaskManager.clearTaskStorage();
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    private void taskDeleteByIdContext(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
        if (httpTaskManager.getTaskById(id) == null) {
            httpExchange.sendResponseHeaders(400, 0);
            System.out.println("Задачи с таким айди не существует");
            httpExchange.close();
        } else {
            httpTaskManager.removeTaskById(id);
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        }
    }

    private void subtaskGetContext(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(gson.toJson(httpTaskManager.getSubtaskList()).getBytes(DEFAULT_CHARSET));
        }
    }

    private void subtaskGetByIdContext(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
        if (httpTaskManager.getSubtaskById(id) == null) {
            httpExchange.sendResponseHeaders(400, 0);
            System.out.println("Подзадачи с таким айди не существует");
            httpExchange.close();
        } else {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(httpTaskManager.getSubtaskById(id)).getBytes(DEFAULT_CHARSET));
            }
        }
    }

    private void subtaskPostContext(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(body, new TypeToken<Subtask>() {
        }.getType());
        if (body.contains("\"id\":0") || !body.contains("\"id\":")) {
            if (httpTaskManager.getEpicList().size() == 0) {
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Нельзя создать подзадачу без эпика!");
                httpExchange.close();
            } else if (!httpTaskManager.getEpics().containsKey(subtask.getYourEpicId())) {
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Ошибка добавления подзадачи! Эпика с таким айди не существует");
                httpExchange.close();
            } else {
                httpTaskManager.createNewSubtask(subtask);
            }
        } else {
            httpTaskManager.updateSubtask(subtask);
        }
        httpExchange.sendResponseHeaders(201, 0);
        httpExchange.close();
    }

    private void subtaskClearContext(HttpExchange httpExchange) throws IOException {
        httpTaskManager.clearSubtaskStorage();
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    private void subtaskDeleteByIdContext(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
        if (httpTaskManager.getSubtaskById(id) == null) {
            httpExchange.sendResponseHeaders(400, 0);
            System.out.println("Подзадачи с таким айди не существует");
            httpExchange.close();
        } else {
            httpTaskManager.removeSubtaskById(id);
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        }
    }

    private void epicGetContext(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(gson.toJson(httpTaskManager.getEpicList()).getBytes(DEFAULT_CHARSET));
        }
    }

    private void epicGetByIdContext(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
        if (httpTaskManager.getEpicById(id) == null) {
            httpExchange.sendResponseHeaders(400, 0);
            System.out.println("Эпика с таким айди не существует");
            httpExchange.close();
        } else {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(httpTaskManager.getEpicById(id)).getBytes(DEFAULT_CHARSET));
            }
        }
    }

    private void epicPostContext(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Epic epic = gson.fromJson(body, new TypeToken<Epic>() {
        }.getType());
        if (body.contains("\"id\":0") || !body.contains("\"id\":")) {
            httpTaskManager.createNewEpic(epic);
        } else {
            httpTaskManager.updateEpic(epic);
        }
        httpExchange.sendResponseHeaders(201, 0);
        httpExchange.close();
    }

    private void epicClearContext(HttpExchange httpExchange) throws IOException {
        httpTaskManager.clearEpicStorage();
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    private void epicDeleteByIdContext(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
        if (httpTaskManager.getEpicById(id) == null) {
            httpExchange.sendResponseHeaders(400, 0);
            System.out.println("Эпика с таким айди не существует");
            httpExchange.close();
        } else {
            httpTaskManager.removeEpicById(id);
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        }
    }

    private void subtaskOfEpicContext(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
        if (httpTaskManager.getEpicById(id) == null) {
            httpExchange.sendResponseHeaders(400, 0);
            System.out.println("Эпика с таким айди не существует");
            httpExchange.close();
        } else {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(httpTaskManager.subtasksOfEpic(id)).getBytes(DEFAULT_CHARSET));
            }
        }
    }

    private void getHistoryContext(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(gson
                    .toJson(httpTaskManager.getHistoryManager().getHistory()).getBytes(DEFAULT_CHARSET));
        }
    }

    private void getPrioritizedTasksContext(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(gson
                    .toJson(httpTaskManager.getPrioritizedTasks()).getBytes(DEFAULT_CHARSET));
        }
    }



    public HTTPTaskManager getHttpTaskManager() {
        return httpTaskManager;
    }

    public HttpServer getServer() {
        return server;
    }

}
