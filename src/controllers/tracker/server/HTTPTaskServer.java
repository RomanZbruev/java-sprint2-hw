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
import java.time.LocalDateTime;



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
        if (uri.endsWith("tasks/task/")
                && httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(httpTaskManager.getTaskList()).getBytes(DEFAULT_CHARSET));
            }
        } else if (uri.contains("id") &&
                httpExchange.getRequestMethod().equals("GET") &&
                httpExchange.getRequestURI().getPath().endsWith("tasks/task/")) {
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
        } else if (httpExchange.getRequestMethod().equals("POST") &&
                uri.endsWith("tasks/task/")
        ) {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, new TypeToken<Task>() {}.getType());
            if (body.contains("\"id\":0") || !body.contains("\"id\":")) { //если есть айди - обновление, нет - добавление новой
                httpTaskManager.createNewTask(task);
            } else {
                httpTaskManager.updateTask(task);
            }

            httpExchange.sendResponseHeaders(201, 0);
            httpExchange.close();
        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.endsWith("tasks/task/")) {
            httpTaskManager.clearTaskStorage();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("tasks/task/")) {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
            if (httpTaskManager.getTaskById(id) == null) {
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Задачи с таким айди не существует");
                httpExchange.close();
            }
            else {
                httpTaskManager.removeTaskById(id);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            }
        } else if (uri.endsWith("subtask/")
                && httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(httpTaskManager.getSubtaskList()).getBytes(DEFAULT_CHARSET));
            }
        } else if (uri.contains("id") &&
                httpExchange.getRequestMethod().equals("GET") &&
                httpExchange.getRequestURI().getPath().endsWith("subtask/")) {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
            if (httpTaskManager.getSubtaskById(id) == null) {
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Подзадачи с таким айди не существует");
                httpExchange.close();
            }
            else {
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(gson.toJson(httpTaskManager.getSubtaskById(id)).getBytes(DEFAULT_CHARSET));
                }
            }
        } else if (httpExchange.getRequestMethod().equals("POST") &&
                uri.endsWith("subtask/")) {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, new TypeToken<Subtask>() {
            }.getType());
            if (body.contains("\"id\":0") || !body.contains("\"id\":")) {
                if(httpTaskManager.getEpicList().size() == 0){
                    httpExchange.sendResponseHeaders(400, 0);
                    System.out.println("Нельзя создать подзадачу без эпика!");
                    httpExchange.close();
                }
                else if (!httpTaskManager.getEpics().containsKey(subtask.getYourEpicId())){
                    httpExchange.sendResponseHeaders(400, 0);
                    System.out.println("Ошибка добавления подзадачи! Эпика с таким айди не существует");
                    httpExchange.close();
                }
                else {
                    httpTaskManager.createNewSubtask(subtask);
                }
            } else {
                httpTaskManager.updateSubtask(subtask);
            }
            httpExchange.sendResponseHeaders(201, 0);
            httpExchange.close();
        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.endsWith("/subtask/")) {
            httpTaskManager.clearSubtaskStorage();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("subtask/")) {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
            if (httpTaskManager.getSubtaskById(id) == null) {
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Подзадачи с таким айди не существует");
                httpExchange.close();
            }
            else {
                httpTaskManager.removeSubtaskById(id);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            }
        } else if (uri.endsWith("epic/")
                && httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(httpTaskManager.getEpicList()).getBytes(DEFAULT_CHARSET));
            }
        } else if (uri.contains("id") &&
                httpExchange.getRequestMethod().equals("GET") &&
                httpExchange.getRequestURI().getPath().endsWith("tasks/epic/")) {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
            if(httpTaskManager.getEpicById(id) == null){
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Эпика с таким айди не существует");
                httpExchange.close();
            }
            else{
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(gson.toJson(httpTaskManager.getEpicById(id)).getBytes(DEFAULT_CHARSET));
                }
            }
        } else if (httpExchange.getRequestMethod().equals("POST") &&
                uri.endsWith("epic/")
        ) {
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
        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.endsWith("/epic/")) {
            httpTaskManager.clearEpicStorage();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } else if (httpExchange.getRequestMethod().equals("DELETE") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("epic/")) {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
            if (httpTaskManager.getEpicById(id) == null) {
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Эпика с таким айди не существует");
                httpExchange.close();
            }
            else {
                httpTaskManager.removeEpicById(id);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            }
        } else if (httpExchange.getRequestMethod().equals("GET") && uri.contains("id")
                && httpExchange.getRequestURI().getPath().endsWith("/subtask/epic/")) {
            String query = httpExchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(query.lastIndexOf("=") + 1));
            if(httpTaskManager.getEpicById(id) == null){
                httpExchange.sendResponseHeaders(400, 0);
                System.out.println("Эпика с таким айди не существует");
                httpExchange.close();
            }
            else {
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(gson.toJson(httpTaskManager.subtasksOfEpic(id)).getBytes(DEFAULT_CHARSET));
                }
            }
        } else if (httpExchange.getRequestMethod().equals("GET")
                && httpExchange.getRequestURI().getPath().endsWith("/tasks/history/")) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson
                        .toJson(httpTaskManager.getHistoryManager().getHistory()).getBytes(DEFAULT_CHARSET));
            }
        } else if (httpExchange.getRequestMethod().equals("GET")
                && httpExchange.getRequestURI().getPath().endsWith("/tasks/")) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson
                        .toJson(httpTaskManager.getPrioritizedTasks()).getBytes(DEFAULT_CHARSET));
            }
        }
    }

    public HTTPTaskManager getHttpTaskManager() {
        return httpTaskManager;
    }

    public HttpServer getServer() {
        return server;
    }

}
