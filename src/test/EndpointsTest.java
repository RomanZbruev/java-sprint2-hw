import com.google.gson.Gson;

import controllers.tracker.Status;
import controllers.tracker.server.HTTPTaskServer;
import controllers.tracker.server.KVServer;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class EndpointsTest {

    KVServer kvServer = new KVServer();
    HTTPTaskServer httpTaskServer;
    HttpClient client;
    Gson gson = new Gson();

    public EndpointsTest() throws IOException {
    }

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer.start();
        httpTaskServer = new HTTPTaskServer();
        httpTaskServer.getServer().start();
        client = HttpClient.newHttpClient();

    }

    @AfterEach
    void afterEach() {
        httpTaskServer.getServer().stop(1);
        kvServer.stop();
    }

    @Test
    public void GetTasksHttpWhenEmptyTaskListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of().toString(),response.body()); // т.к. придет пустой лист (т.е. "[]")
    }

    @Test
    public void GetTasksHttpTestWhenNormalTest() throws IOException,InterruptedException{
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1,
                        LocalDateTime.of(2000,1,1,1,1)));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expected = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        String expectedJson = gson.toJson(expected,Task.class);
        Assertions.assertEquals(List.of(expectedJson).toString(),response.body());
    }

    @Test
    public void GetTaskByIdHttpWhenNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1)));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expected = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        String expectedJson = gson.toJson(expected,Task.class);
        Assertions.assertEquals(expectedJson,response.body());
    }

    @Test
    public void GetTaskByIdHttpWhenIncorrectId() throws IOException,InterruptedException{
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1)));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode()); // задачи нет - по реализации возвращаю код 400
    }

    @Test
    public void PostTaskWhenEmptyListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expected = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        expected.setStatus(Status.NEW);
        expected.setId(1);
        Assertions.assertEquals(expected,httpTaskServer.getHttpTaskManager().getTaskById(1));
    }

    @Test
    public void PostTaskWhenNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1
                ,LocalDateTime.of(2001,1,1,1,1)));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expected1 = new Task("t","d",1,
                LocalDateTime.of(2001,1,1,1,1));
        expected1.setStatus(Status.NEW);
        expected1.setId(1);
        Task expected2 = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        expected2.setStatus(Status.NEW);
        expected2.setId(2);
        Assertions.assertEquals(List.of(expected1,expected2),httpTaskServer.getHttpTaskManager().getTaskList());
    }

    @Test
    public void DeleteTaskByIdNormalTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        httpTaskServer.getHttpTaskManager().createNewTask(task);
        Assertions.assertEquals(List.of(task),httpTaskServer.getHttpTaskManager().getTaskList());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getTaskList());
    }

    @Test
    public void DeleteTaskByIdIncorrectId() throws IOException,InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1)));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode());

    }

    @Test
    public void DeleteAllTasksNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1)));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getTaskList());
    }

    @Test
    public void DeleteAllTasksEmptyListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getTaskList());
    }

    @Test
    public void GetSubtasksHttpWhenEmptyTaskListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of().toString(),response.body()); // т.к. придет пустой лист (т.е. "[]")
    }

    @Test
    public void GetSubtasksHttpTestWhenNormalTest() throws IOException,InterruptedException{
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        httpTaskServer.getHttpTaskManager().createNewSubtask(new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1));
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask expected = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        expected.setId(2);
        expected.setStatus(Status.NEW);
        String expectedJson = gson.toJson(expected,Subtask.class);
        Assertions.assertEquals(List.of(expectedJson).toString(),response.body());
    }

    @Test
    public void GetSubtaskByIdHttpWhenNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        httpTaskServer.getHttpTaskManager().createNewSubtask(new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1));
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask expected = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        expected.setId(2);
        expected.setStatus(Status.NEW);
        String expectedJson = gson.toJson(expected,Subtask.class);
        Assertions.assertEquals(expectedJson,response.body());
    }

    @Test
    public void GetSubtasksByIdIncorrectTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        httpTaskServer.getHttpTaskManager().createNewSubtask(new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1));
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode());
    }

    @Test
    public void PostSubtaskWhenEmptyListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Task task = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode());
    }

    @Test
    public void PostSubtaskWhenIncorrectEpicIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        Subtask task = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),5);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode());
    }

    @Test
    public void PostSubtaskWhenNormalWorkTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        Subtask task = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask expected = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        expected.setId(2);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(expected,httpTaskServer.getHttpTaskManager().getSubtaskById(2));
    }

    @Test
    public void DeleteSubtaskByIdNormalTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        Subtask subtask = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        httpTaskServer.getHttpTaskManager().createNewSubtask(subtask);
        Assertions.assertEquals(List.of(subtask),httpTaskServer.getHttpTaskManager().getSubtaskList());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getSubtaskList());
    }

    @Test
    public void DeleteSubtaskByIdIncorrectId() throws IOException,InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        Subtask subtask = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        httpTaskServer.getHttpTaskManager().createNewSubtask(subtask);
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=6");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode());

    }

    @Test
    public void DeleteAllSubtasksNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        Subtask subtask = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        httpTaskServer.getHttpTaskManager().createNewSubtask(subtask);
        Assertions.assertEquals(List.of(subtask),httpTaskServer.getHttpTaskManager().getSubtaskList());
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getSubtaskList());
    }

    @Test
    public void DeleteAllSubtasksEmptyListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getSubtaskList());
    }

    @Test
    public void GetEpicsHttpWhenEmptyTaskListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of().toString(),response.body()); // т.к. придет пустой лист (т.е. "[]")
    }

    @Test
    public void GetEpicsHttpTestWhenNormalTest() throws IOException,InterruptedException{
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic expected = new Epic("t","d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        expected.setStartTime(null);
        expected.setEndTime(null);
        expected.setDuration(0);
        String expectedJson = gson.toJson(expected,Epic.class);
        Assertions.assertEquals(List.of(expectedJson).toString(),response.body());
    }

    @Test
    public void GetEpicByIdHttpWhenNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic expected = new Epic("t","d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        expected.setStartTime(null);
        expected.setEndTime(null);
        expected.setDuration(0);
        String expectedJson = gson.toJson(expected,Epic.class);
        Assertions.assertEquals(expectedJson,response.body());
    }

    @Test
    public void GetEpicByIdHttpWhenIncorrectId() throws IOException,InterruptedException{
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=6");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode()); // задачи нет - по реализации возвращаю код 400
    }

    @Test
    public void PostEpicWhenEmptyListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new Epic("t","d"));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic expected = new Epic("t","d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        expected.setStartTime(null);
        expected.setEndTime(null);
        expected.setDuration(0);
        Assertions.assertEquals(expected,httpTaskServer.getHttpTaskManager().getEpicById(1));
    }

    @Test
    public void PostEpicWhenNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1
                ,LocalDateTime.of(2001,1,1,1,1)));
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new Epic("t","d"));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expected1 = new Task("t","d",1,
                LocalDateTime.of(2001,1,1,1,1));
        expected1.setStatus(Status.NEW);
        expected1.setId(1);
        Epic expected = new Epic("t","d");
        expected.setId(2);
        expected.setStatus(Status.NEW);
        expected.setStartTime(null);
        expected.setEndTime(null);
        expected.setDuration(0);
        Assertions.assertEquals(List.of(expected1),httpTaskServer.getHttpTaskManager().getTaskList());
        Assertions.assertEquals(List.of(expected),httpTaskServer.getHttpTaskManager().getEpicList());
    }

    @Test
    public void DeleteEpicByIdNormalTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        Epic expected = new Epic("t","d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        expected.setStartTime(null);
        expected.setEndTime(null);
        expected.setDuration(0);
        Assertions.assertEquals(List.of(expected),httpTaskServer.getHttpTaskManager().getEpicList());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getEpicList());
    }

    @Test
    public void DeleteEpicByIdIncorrectId() throws IOException,InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=123123");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode());

    }

    @Test
    public void DeleteAllEpicsNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        Subtask subtask = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1),1);
        httpTaskServer.getHttpTaskManager().createNewSubtask(subtask);
        Assertions.assertEquals(List.of(subtask),httpTaskServer.getHttpTaskManager().getSubtaskList());
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getEpicList());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getSubtaskList());
    }

    @Test
    public void DeleteAllEpicsEmptyListTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of(), httpTaskServer.getHttpTaskManager().getEpicList());
    }

    @Test
    public void GetEpicSubtasksNewEpicTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of().toString(),response.body());
    }

    @Test
    public void GetEpicSubtasksNormalTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        httpTaskServer.getHttpTaskManager().createNewSubtask(new Subtask("t","d",1,
                LocalDateTime.of(2120,1,1,1,1),1));
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask expected = new Subtask("t","d",1,
                LocalDateTime.of(2120,1,1,1,1),1);
        expected.setId(2);
        expected.setStatus(Status.NEW);
        String json = gson.toJson(expected);
        Assertions.assertEquals(List.of(json).toString(),response.body());
    }

    @Test
    public void GetEpicSubtasksIncorrectTest() throws IOException, InterruptedException {
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        httpTaskServer.getHttpTaskManager().createNewSubtask(new Subtask("t","d",1,
                LocalDateTime.of(2120,1,1,1,1),1));
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1000");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400,response.statusCode());
    }

    @Test
    public void GetHistoryEmptyTest() throws IOException,InterruptedException{
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of().toString(),response.body());
    }

    @Test
    public void GetHistoryNormalTest() throws IOException,InterruptedException{
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1,
                LocalDateTime.of(2001,1,1,1,1)));
        httpTaskServer.getHttpTaskManager().getTaskById(1);
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expected = new Task("t","d",1,
                LocalDateTime.of(2001,1,1,1,1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        String json = gson.toJson(expected);
        Assertions.assertEquals(List.of(json).toString(),response.body());
    }

    @Test
    public void GetPrioritizedTasksEmptyList() throws IOException,InterruptedException{
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(List.of().toString(),response.body());
    }

    @Test
    public void GetPrioritizedTasksNormalTest() throws IOException,InterruptedException{
        httpTaskServer.getHttpTaskManager().createNewTask(new Task("t","d",1,
                LocalDateTime.of(2001,1,1,1,1)));
        httpTaskServer.getHttpTaskManager().createNewEpic(new Epic("t","d"));
        httpTaskServer.getHttpTaskManager().createNewSubtask(new Subtask("t","d",1,
                LocalDateTime.of(2001,2,2,2,2),2));
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expected = new Task("t","d",1,
                LocalDateTime.of(2001,1,1,1,1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        String json = gson.toJson(expected);
        Subtask expected2 = new Subtask("t","d",1,
                LocalDateTime.of(2001,2,2,2,2),2);
        expected2.setId(3);
        expected2.setStatus(Status.NEW);
        String json2 = gson.toJson(expected2);
        String expectedString = "[" + json + "," + json2 +"]";
        Assertions.assertEquals(expectedString,response.body());
    }

}
