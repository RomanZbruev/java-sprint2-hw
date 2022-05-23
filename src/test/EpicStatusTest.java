import controllers.tracker.TaskManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import controllers.tracker.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import controllers.tracker.Status;

public class EpicStatusTest {
    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void beforeEach(){
        taskManager = new InMemoryTaskManager(); // создается экземпляр менеджера, так как обновление статуса эпика
        // прописано в нем
        epic = new Epic("title","description");
        taskManager.createNewEpic(epic);
    }

    @Test
    public void EpicWithoutSubtaskTest(){
        Status status = epic.getStatus();
        Status expectedStatus = Status.NEW;
        Assertions.assertEquals(expectedStatus,status);
    }

    @Test
    public void EpicWithNewSubtask(){
        Subtask subtask = new Subtask("title","descr");
        taskManager.createNewSubtask(subtask,epic.getId());
        Status status = epic.getStatus();
        Status expectedStatus = Status.NEW;
        Assertions.assertEquals(expectedStatus,status);
    }

    @Test
    public void EpicWithDoneSubtask(){
        Subtask subtask = new Subtask("title","descr");
        taskManager.createNewSubtask(subtask,epic.getId());
        taskManager.updateSubtask(subtask.getId(),subtask,subtask.getTitle(),subtask.getDescription()
                ,Status.DONE,epic.getId());
        Status status = epic.getStatus();
        Status expectedStatus = Status.DONE;
        Assertions.assertEquals(expectedStatus,status);
    }

    @Test
    public void EpicWithNewAndDoneSubtasks(){
        Subtask subtask1 = new Subtask("title","descr");
        taskManager.createNewSubtask(subtask1,epic.getId());
        taskManager.updateSubtask(subtask1.getId(),subtask1,subtask1.getTitle(),subtask1.getDescription()
                ,Status.DONE,epic.getId());
        Subtask subtask2 = new Subtask("title","descr");
        taskManager.createNewSubtask(subtask2,epic.getId());
        Status status = epic.getStatus();
        Status expectedStatus = Status.IN_PROGRESS;
        Assertions.assertEquals(expectedStatus,status);
    }

    @Test
    public void EpicWithInProgressSubtask(){
        Subtask subtask = new Subtask("title","descr");
        taskManager.createNewSubtask(subtask,epic.getId());
        taskManager.updateSubtask(subtask.getId(),subtask,subtask.getTitle(),subtask.getDescription()
                ,Status.IN_PROGRESS,epic.getId());
        Status status = epic.getStatus();
        Status expectedStatus = Status.IN_PROGRESS;
        Assertions.assertEquals(expectedStatus,status);
    }

}
