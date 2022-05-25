package SameMethodsTests;

import controllers.tracker.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class TaskManagerInMemorySameMethodsTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void init() {
        taskManager = new InMemoryTaskManager();
    }

}

