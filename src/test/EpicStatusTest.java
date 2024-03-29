import controllers.tracker.TaskManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import controllers.tracker.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import controllers.tracker.Status;

import java.time.LocalDateTime;

public class EpicStatusTest {
    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager(); // создается экземпляр менеджера, так как обновление статуса эпика
        // прописано в нем
        epic = new Epic("title", "description");
        taskManager.createNewEpic(epic);
    }

    @Test
    public void EpicWithoutSubtaskTest() {
        Status status = epic.getStatus();
        Status expectedStatus = Status.NEW;
        Assertions.assertEquals(expectedStatus, status);
    }

    @Test
    public void EpicWithNewSubtask() {
        Subtask subtask = new Subtask("title", "descr", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Status status = epic.getStatus();
        Status expectedStatus = Status.NEW;
        Assertions.assertEquals(expectedStatus, status);
    }

    @Test
    public void EpicWithDoneSubtask() {
        Subtask subtask = new Subtask("title", "descr", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask(subtask.getTitle(), subtask.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask.setId(subtask.getId());
        taskManager.updateSubtask(updatedSubtask);
        Status status = epic.getStatus();
        Status expectedStatus = Status.DONE;
        Assertions.assertEquals(expectedStatus, status);
    }

    @Test
    public void EpicWithNewAndDoneSubtasks() {
        Subtask subtask1 = new Subtask("title", "descr", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask1);
        Subtask updatedSubtask = new Subtask(subtask1.getTitle(), subtask1.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask.setId(subtask1.getId());
        taskManager.updateSubtask(updatedSubtask);
        Subtask subtask2 = new Subtask("title", "descr", 1,
                LocalDateTime.of(2000, 1, 1, 1, 10),epic.getId());
        taskManager.createNewSubtask(subtask2);
        Status status = epic.getStatus();
        Status expectedStatus = Status.IN_PROGRESS;
        Assertions.assertEquals(expectedStatus, status);
    }

    @Test
    public void EpicWithInProgressSubtask() {
        Subtask subtask = new Subtask("title", "descr", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask(subtask.getTitle(), subtask.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask.setStatus(Status.IN_PROGRESS);
        updatedSubtask.setId(subtask.getId());
        taskManager.updateSubtask(updatedSubtask);
        Status status = epic.getStatus();
        Status expectedStatus = Status.IN_PROGRESS;
        Assertions.assertEquals(expectedStatus, status);
    }

}
