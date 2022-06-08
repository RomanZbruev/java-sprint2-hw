import controllers.tracker.util.Managers;
import controllers.history.HistoryManager;
import model.tracker.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void EmptyHistoryManager() {
        List<Task> history = historyManager.getHistory();
        List<Task> expectedList = new ArrayList<>(); //должен вернуть пустой лист
        Assertions.assertEquals(expectedList, history);
    }

    @Test
    public void DuplicateInHistoryManager() {
        Task task1 = new Task("t1", "d1", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task1.setId(1); //айди нужен для работы методов менеджера истории
        Task task2 = new Task("t2", "d2", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(List.of(task2, task1), history);
    }

    @Test
    public void BeginningRemoveHistoryManager() {
        Task task1 = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task1.setId(1);
        Task task2 = new Task("t2", "d2", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task2.setId(2);
        Task task3 = new Task("t3", "d3", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(List.of(task2, task3), history);
    }

    @Test
    public void MiddleRemoveHistoryManager() {
        Task task1 = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task1.setId(1);
        Task task2 = new Task("t2", "d2", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task2.setId(2);
        Task task3 = new Task("t3", "d3", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(List.of(task1, task3), history);
    }

    @Test
    public void EndRemoveHistoryManager() {
        Task task1 = new Task("t3", "d3", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task1.setId(1);
        Task task2 = new Task("t3", "d3", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task2.setId(2);
        Task task3 = new Task("t3", "d3", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(List.of(task1, task2), history);
    }
}
