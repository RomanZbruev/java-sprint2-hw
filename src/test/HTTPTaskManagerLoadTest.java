
import controllers.tracker.Status;
import controllers.tracker.server.HTTPTaskManager;
import controllers.tracker.server.KVServer;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class HTTPTaskManagerLoadTest {
    HTTPTaskManager httpTaskManager;
    String url = "http://localhost:8078";
    KVServer kvServer;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = new HTTPTaskManager(url);
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    void SaveAndLoadWhenEmptyListTask(){
        httpTaskManager.save();
        httpTaskManager.load();
        Assertions.assertEquals(List.of(),httpTaskManager.getTaskList());
        Assertions.assertEquals(List.of(),httpTaskManager.getSubtaskList());
        Assertions.assertEquals(List.of(),httpTaskManager.getEpicList());
        Assertions.assertEquals(List.of(),httpTaskManager.getHistoryManager().getHistory());
        Assertions.assertEquals(0,httpTaskManager.getGlobalTaskId());
    }

    @Test
    public void saveAndLoadWhenEpicWithoutSubtasksListAndNonEmptyHistoryTest() {
        Epic epic = new Epic("t", "d");
        httpTaskManager.createNewEpic(epic);// при создании происходит запись
        httpTaskManager.getEpicById(epic.getId()); //непустая история
        httpTaskManager.load(); // устнавливаются значения  с сервера
        Epic expected = new Epic("t", "d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(List.of(expected), httpTaskManager.getEpicList());
        Assertions.assertEquals(List.of(expected), httpTaskManager.getHistoryManager().getHistory());
    }

    @Test
    public void saveAndLoadStandardWorkWhenEpicWithSubtaskListAndNonEmptyHistoryTest() {
        Epic epic = new Epic("t", "d");
        httpTaskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        httpTaskManager.createNewSubtask(subtask);
        httpTaskManager.getEpicById(epic.getId()); //непустая история
        httpTaskManager.load();
        Epic expected = new Epic("t", "d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(1);
        expected.setEndTime((LocalDateTime.of(2000, 1, 1, 1, 2)));
        Assertions.assertEquals(List.of(expected), httpTaskManager.getEpicList());
        Assertions.assertEquals(List.of(expected), httpTaskManager.getHistoryManager().getHistory());
    }

    @Test
    public void saveAndLoadWithTaskAndEmptyHistoryTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        httpTaskManager.createNewTask(task);// при создании происходит запись
        httpTaskManager.load();
        Task expected = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(List.of(expected), httpTaskManager.getTaskList());
        Assertions.assertEquals(List.of(), httpTaskManager.getHistoryManager().getHistory());
    }
}
