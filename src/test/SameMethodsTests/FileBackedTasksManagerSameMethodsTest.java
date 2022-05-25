package SameMethodsTests;

import controllers.tracker.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

public class FileBackedTasksManagerSameMethodsTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    public void init() {
        File file = new File("./src/storage/historyTest.csv");
        taskManager = new FileBackedTasksManager(file);
    }
}